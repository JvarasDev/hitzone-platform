-- V1: Creación inicial de las tablas de autenticación
-- Microservicio: ms-auth (fullstack)
-- Fecha: 2026-05-15

CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    name_rol    VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles (id) ON DELETE CASCADE
);

-- Índices declarados en la entidad User
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email    ON users (email);

-- Datos iniciales: roles base del sistema
INSERT INTO roles (name_rol, description)
VALUES
    ('ROLE_USER',  'Usuario estándar del sistema'),
    ('ROLE_ADMIN', 'Administrador con acceso completo')
ON CONFLICT (name_rol) DO NOTHING;
