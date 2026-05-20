package cl.hitzone.ms_news.service;

import cl.hitzone.ms_news.dto.NewsArticleRequestDTO;
import cl.hitzone.ms_news.dto.NewsArticleResponseDTO;

import java.util.List;

public interface NewsArticleService {
    List<NewsArticleResponseDTO> getAllPublished();
    NewsArticleResponseDTO getById(Long id);
    List<NewsArticleResponseDTO> getByCategory(String category);
    List<NewsArticleResponseDTO> getByAuthor(String author);
    Long countByPublished(boolean published);
    NewsArticleResponseDTO create(NewsArticleRequestDTO dto);
    NewsArticleResponseDTO update(Long id, NewsArticleRequestDTO dto);
    void delete(Long id);
}
