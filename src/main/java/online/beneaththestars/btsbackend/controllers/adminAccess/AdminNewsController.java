package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.NewsDTOs.CreateNewsRequest;
import online.beneaththestars.btsbackend.models.dto.NewsDTOs.UpdateNewsRequest;
import online.beneaththestars.btsbackend.models.entities.NewsPost;
import online.beneaththestars.btsbackend.services.Admin.AdminAuthService;
import online.beneaththestars.btsbackend.services.Admin.AdminUserService;
import online.beneaththestars.btsbackend.services.News.NewsService;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/news")
public class AdminNewsController {
    private final AdminAuthService adminAuthService;
    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<NewsPost> publishNews(
            @RequestBody @Valid CreateNewsRequest createNewsRequest,
            HttpServletRequest httpRequest) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        adminAuthService.getLoggedInAdminId(httpRequest);
        return
                ResponseEntity.ok(newsService.createNewsPost(
                        createNewsRequest,
                        adminAuthService.getLoggedInAdmin(httpRequest)));
    }

    @GetMapping
    public ResponseEntity<Page<NewsPost>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(newsService.getNews(pageable));
    }

    @GetMapping ("/{newsId}")
    public ResponseEntity<NewsPost> getNewsPost(
            @PathVariable Long newsId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        return ResponseEntity.ok(newsService.getNewsById(newsId));
    }

    @PatchMapping("/{newsId}")
    public ResponseEntity<NewsPost> updateNewsPost(
            @PathVariable Long newsId,
            @RequestBody @Valid UpdateNewsRequest updateNewsRequest,
            HttpServletRequest httpRequest
            ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        return ResponseEntity.ok(newsService.updateNewsPost(newsId, updateNewsRequest));
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> deleteNewsPost(
            @PathVariable Long newsId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        newsService.deleteNewsPost(newsId);
        return ResponseEntity.noContent().build();
    }
}
