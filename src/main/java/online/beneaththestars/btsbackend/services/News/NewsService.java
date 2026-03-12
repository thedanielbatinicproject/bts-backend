package online.beneaththestars.btsbackend.services.News;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.NewsDTOs.CreateNewsRequest;
import online.beneaththestars.btsbackend.models.dto.NewsDTOs.UpdateNewsRequest;
import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.models.entities.NewsPost;
import online.beneaththestars.btsbackend.repo.NewsPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {

    private final NewsPostRepository newsPostRepository;

    public Page<NewsPost> getNews(Pageable pageable) {
        return newsPostRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public NewsPost getNewsById(Long newsId) {
        return newsPostRepository.findById(newsId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "News with id " + newsId + " was not found")
        );
    }

    public NewsPost createNewsPost(CreateNewsRequest req, AdminUser createdBy) {
        if (createdBy == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing admin user");
        }

        NewsPost np = new NewsPost();
        np.setCreatedBy(createdBy);
        np.setTitle(req.getTitle());
        np.setImageUrl(req.getImageUrl());
        np.setDescription(req.getDescription());

        return newsPostRepository.save(np);
    }

    public void deleteNewsPost(Long newsId) {
        if(!newsPostRepository.existsById(newsId)) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "News with id "+newsId.toString()+" cannot be found. Delete skipped!"
        );

        newsPostRepository.deleteById(newsId);
    }

    public NewsPost updateNewsPost(Long newsId, @Valid UpdateNewsRequest updateNewsRequest) {
        NewsPost np = newsPostRepository.findNewsPostById(newsId);
        np.setUpdatedAt(Instant.now());
        np.setDescription(updateNewsRequest.getDescription());
        np.setTitle(updateNewsRequest.getTitle());
        np.setImageUrl(updateNewsRequest.getImageUrl());
        newsPostRepository.save(np);
        return np;
    }
}