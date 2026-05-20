package cl.hitzone.ms_news.repository;

import cl.hitzone.ms_news.model.NewsArticle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    // GET solo artículos publicados
    List<NewsArticle> findByPublishedTrue(Sort sort);

    // GET publicados por categoría
    List<NewsArticle> findByPublishedTrueAndCategory(String category, Sort sort);

    // GET por autor
    List<NewsArticle> findByAuthor(String author, Sort sort);

    // Verificar si existe un título
    boolean existsByTitle(String title);

    // GET por categoría (todos, publicados y no publicados)
    List<NewsArticle> findByCategory(String category, Sort sort);
}
