package service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.myblog.repository.CommentRepository;
import ru.yandex.practicum.myblog.repository.PostRepository;
import ru.yandex.practicum.myblog.repository.TagRepository;
import ru.yandex.practicum.myblog.service.CommentService;
import ru.yandex.practicum.myblog.service.CommentServiceImpl;
import ru.yandex.practicum.myblog.service.PostService;
import ru.yandex.practicum.myblog.service.PostServiceImpl;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfigService {

    @Bean
    public PostRepository postRepository() {
        return mock(PostRepository.class);
    }

    @Bean
    public TagRepository tagRepository() {
        return mock(TagRepository.class);
    }

    @Bean
    public CommentRepository commentRepository() {
        return mock(CommentRepository.class);
    }

    @Bean
    public PostService postService(PostRepository postRepository,
                                   TagRepository tagRepository,
                                   CommentRepository commentRepository) {
        return new PostServiceImpl(postRepository, tagRepository, commentRepository);
    }

    @Bean
    public CommentService commentService(CommentRepository commentRepository) {
        return new CommentServiceImpl(commentRepository);
    }

}
