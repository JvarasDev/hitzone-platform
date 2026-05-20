package cl.hitzone.ms_news.service;

import cl.hitzone.ms_news.dto.NewsArticleRequestDTO;
import cl.hitzone.ms_news.dto.NewsArticleResponseDTO;
import cl.hitzone.ms_news.exception.DuplicateResourceException;
import cl.hitzone.ms_news.exception.ResourceNotFoundException;
import cl.hitzone.ms_news.model.NewsArticle;
import cl.hitzone.ms_news.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsArticleServiceImpl implements NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NewsArticleResponseDTO> getAllPublished() {
        return newsArticleRepository
                .findByPublishedTrue(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(NewsArticleResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NewsArticleResponseDTO getById(Long id) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia no encontrada con id: " + id));
        return new NewsArticleResponseDTO(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsArticleResponseDTO> getByCategory(String category) {
        return newsArticleRepository
                .findByPublishedTrueAndCategory(
                        category.toUpperCase(),
                        Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(NewsArticleResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsArticleResponseDTO> getByAuthor(String author) {
        return newsArticleRepository
                .findByAuthor(author, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(NewsArticleResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByPublished(boolean published) {
        return newsArticleRepository.findAll().stream()
                .filter(a -> a.getPublished() == published)
                .count();
    }

    @Override
    @Transactional
    public NewsArticleResponseDTO create(NewsArticleRequestDTO dto) {
        if (newsArticleRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Ya existe una noticia con ese título: " + dto.getTitle());
        }

        NewsArticle article = new NewsArticle();
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory().toUpperCase());
        article.setAuthor(dto.getAuthor());
        article.setImageUrl(dto.getImageUrl());
        article.setPublished(dto.getPublished() != null ? dto.getPublished() : false);

        return new NewsArticleResponseDTO(newsArticleRepository.save(article));
    }

    @Override
    @Transactional
    public NewsArticleResponseDTO update(Long id, NewsArticleRequestDTO dto) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia no encontrada con id: " + id));

        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory().toUpperCase());
        article.setAuthor(dto.getAuthor());
        article.setImageUrl(dto.getImageUrl());
        article.setPublished(dto.getPublished() != null ? dto.getPublished() : article.getPublished());

        return new NewsArticleResponseDTO(newsArticleRepository.save(article));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        NewsArticle article = newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noticia no encontrada con id: " + id));
        newsArticleRepository.delete(article);
    }
}
