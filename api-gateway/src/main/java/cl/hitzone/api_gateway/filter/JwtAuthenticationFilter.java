package cl.hitzone.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Filtro global del API Gateway que centraliza toda la autenticación JWT.
 *
 * Responsabilidades:
 *  1. Dejar pasar las rutas públicas (login, register) sin validar token.
 *  2. Rechazar con 401 + JSON cualquier request a ruta protegida sin token válido.
 *  3. Propagar el header Authorization completo al microservicio destino.
 *
 * Los microservicios de negocio (ms-weapons, ms-agents, etc.) NO validan tokens;
 * confían ciegamente en que el gateway ya lo hizo.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ── 1. Rutas públicas — pasar sin validar token ───────────────────────
        if (isPublicEndpoint(path)) {
            System.out.println("[GW] Ruta pública, omitiendo validación JWT: " + path);
            return chain.filter(exchange);
        }

        // ── 2. Verificar presencia del header Authorization ───────────────────
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("[GW] 401 - Header Authorization ausente o malformado en: " + path);
            return onError(exchange, "Token inválido o ausente");
        }

        String token = authHeader.substring(7); // quitar "Bearer "

        // ── 3. Validar firma y expiración del JWT ─────────────────────────────
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            System.out.println("[GW] Token válido. Usuario: " + username + " → " + path);

            // ── 4. Propagar Authorization + username al microservicio destino ─
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, authHeader)   // preservar el token
                    .header("X-Auth-Username", username)             // header auxiliar útil
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            System.err.println("[GW] 401 - Token JWT inválido/expirado en " + path + ": " + e.getMessage());
            return onError(exchange, "Token inválido o ausente");
        }
    }

    @Override
    public int getOrder() {
        return -1; // Máxima prioridad — se ejecuta antes que cualquier otro filtro
    }

    /**
     * Rutas públicas que el gateway deja pasar sin token.
     * Whitelist mínima según requerimiento:
     *   POST /api/auth/login     → ms-auth (ruta real del microservicio)
     *   POST /api/auth/register  → ms-auth (ruta real del microservicio)
     *
     * También se permite /api/auth/validate para que el gateway pueda llamarlo
     * si en el futuro se cambia a validación remota.
     */
    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/register")
                || path.startsWith("/api/auth/validate");
    }

    /**
     * Respuesta de error 401 con body JSON estándar.
     *
     * Antes retornaba body vacío, lo que confundía al cliente JS que esperaba JSON.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                message
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
