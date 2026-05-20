package cl.hitzone.ms_news.dto;

import cl.hitzone.ms_news.model.NewsArticle;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class NewsArticleResponseDTO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String category;
    private String author;
    private String imageUrl;
    private Boolean published;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor desde entidad
    public NewsArticleResponseDTO(NewsArticle article) {
        this.id        = article.getId();
        this.title     = article.getTitle();
        this.summary   = article.getSummary();
        this.content   = article.getContent();
        this.category  = article.getCategory();
        this.author    = article.getAuthor();
        this.imageUrl  = article.getImageUrl();
        this.published = article.getPublished();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }
}
