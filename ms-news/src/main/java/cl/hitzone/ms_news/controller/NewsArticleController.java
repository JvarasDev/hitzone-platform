package cl.hitzone.ms_news.controller;

import cl.hitzone.ms_news.dto.NewsArticleRequestDTO;
import cl.hitzone.ms_news.dto.NewsArticleResponseDTO;
import cl.hitzone.ms_news.service.NewsArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    // ─── GET /api/v1/news ─────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<NewsArticleResponseDTO>> getAllPublished() {
        return ResponseEntity.ok(newsArticleService.getAllPublished());
    }

    // ─── GET /api/v1/news/{id} ────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<NewsArticleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(newsArticleService.getById(id));
    }

    // ─── REPORTE: GET /api/v1/news/category/{cat} ────────────────────────────
    @GetMapping("/category/{cat}")
    public ResponseEntity<List<NewsArticleResponseDTO>> getByCategory(@PathVariable String cat) {
        return ResponseEntity.ok(newsArticleService.getByCategory(cat));
    }

    // ─── REPORTE: GET /api/v1/news/author/{author} ───────────────────────────
    @GetMapping("/author/{author}")
    public ResponseEntity<List<NewsArticleResponseDTO>> getByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(newsArticleService.getByAuthor(author));
    }

    // ─── REPORTE: GET /api/v1/news/count?published=true ─────────────────────
    @GetMapping("/count")
    public ResponseEntity<Long> countByPublished(
            @RequestParam(defaultValue = "true") boolean published) {
        return ResponseEntity.ok(newsArticleService.countByPublished(published));
    }

    // ─── POST /api/v1/news ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<NewsArticleResponseDTO> create(
            @Valid @RequestBody NewsArticleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsArticleService.create(dto));
    }

    // ─── PUT /api/v1/news/{id} ────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<NewsArticleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NewsArticleRequestDTO dto) {
        return ResponseEntity.ok(newsArticleService.update(id, dto));
    }

    // ─── DELETE /api/v1/news/{id} ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsArticleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
