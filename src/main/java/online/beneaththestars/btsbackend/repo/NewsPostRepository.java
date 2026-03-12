package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.NewsPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {
    Page<NewsPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    NewsPost findNewsPostById(Long id);
}
