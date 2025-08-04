package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.myblog.controller.CommentController;
import ru.yandex.practicum.myblog.controller.PostController;
import ru.yandex.practicum.myblog.service.CommentService;
import ru.yandex.practicum.myblog.service.PostService;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public CommentService commentService() {
        return mock(CommentService.class);
    }

    @Bean
    public PostService postService() {
        return mock(PostService.class);
    }

    @Bean
    public PostController postController(PostService postService) {
        return new PostController(postService);
    }

    @Bean
    public CommentController commentController(CommentService commentService) {
        return new CommentController(commentService);
    }

}
