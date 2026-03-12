package online.beneaththestars.btsbackend.controllers.publicAccess;

import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.entities.NewsPost;
import online.beneaththestars.btsbackend.services.News.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Page<NewsPost>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.getNews(pageable));
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsPost> getNewsById(@PathVariable Long newsId) {
        return ResponseEntity.ok(newsService.getNewsById(newsId));
    }
}