package cl.hitzone.ms_news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleRequestDTO {
    @NotBlank(message = "El título es requerido")
    @Size(max = 255, message = "El título no puede superar 255 caracteres")
    private String title;

    @Size(max = 500, message = "El resumen no puede superar 500 caracteres")
    private String summary;         // opcional

    @NotBlank(message = "El contenido es requerido")
    private String content;

    @NotBlank(message = "La categoría es requerida")
    @Size(max = 50, message = "La categoría no puede superar 50 caracteres")
    private String category;

    @Size(max = 100, message = "El autor no puede superar 100 caracteres")
    private String author;          // opcional

    @Size(max = 500, message = "La URL no puede superar 500 caracteres")
    private String imageUrl;        // opcional

    private Boolean published = false;
}
