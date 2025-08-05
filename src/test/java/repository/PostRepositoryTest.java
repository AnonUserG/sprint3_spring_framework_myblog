package repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import repository.config.TestConfigRepository;
import ru.yandex.practicum.myblog.model.Post;
import ru.yandex.practicum.myblog.repository.PostRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfigRepository.class)
public class PostRepositoryTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS posts;");
        jdbcTemplate.getJdbcTemplate().execute("""
            CREATE TABLE posts (
                id IDENTITY PRIMARY KEY,
                title VARCHAR(255),
                text TEXT,
                image_path VARCHAR(255),
                likes_count INT
            );
        """);

        jdbcTemplate.getJdbcTemplate().execute("""
            INSERT INTO posts (title, text, image_path, likes_count)
            VALUES ('Пост 1', 'Текст 1', 'img1.jpg', 5),
                   ('Пост 2', 'Текст 2', 'img2.jpg', 2);
        """);
    }


    @Test
    @DisplayName("Поиск поста по ID")
    void findById_shouldReturnPost_whenPostExists() {
        Optional<Post> postOpt = postRepository.findById(1L);
        assertTrue(postOpt.isPresent());
        assertEquals("Пост 1", postOpt.get().getTitle());
    }

    @Test
    @DisplayName("Получение всех постов с пагинацией")
    void findAll_shouldReturnAllPostsWithPagination() {
        List<Post> posts = postRepository.findAll(10, 0);
        assertEquals(2, posts.size());
    }

    @Test
    @DisplayName("Сохранение нового поста и возврат его ID")
    void save_shouldPersistPost_andReturnGeneratedId() {
        Post post = new Post();
        post.setTitle("Новый пост");
        post.setText("Контент");
        post.setImagePath("image.png");
        post.setLikesCount(0);

        Long id = postRepository.save(post);
        assertNotNull(id);

        Optional<Post> saved = postRepository.findById(id);
        assertTrue(saved.isPresent());
        assertEquals("Новый пост", saved.get().getTitle());
    }

    @Test
    @DisplayName("Обновление поста")
    void update_shouldUpdatePostFields_whenPostExists() {
        Optional<Post> postOpt = postRepository.findById(1L);
        assertTrue(postOpt.isPresent());

        Post post = postOpt.get();
        post.setTitle("Обновлённый заголовок");
        post.setLikesCount(10);

        postRepository.update(post);

        Optional<Post> updated = postRepository.findById(1L);
        assertTrue(updated.isPresent());
        assertEquals("Обновлённый заголовок", updated.get().getTitle());
        assertEquals(10, updated.get().getLikesCount());
    }

    @Test
    @DisplayName("Удаление поста по ID")
    void deleteById_shouldRemovePost_whenPostExists() {
        postRepository.deleteById(2L);
        Optional<Post> deleted = postRepository.findById(2L);
        assertTrue(deleted.isEmpty());
    }

    @Test
    @DisplayName("Увеличение счётчика лайков")
    void incrementLikes_shouldIncreaseLikes_whenIncrementIsTrue() {
        postRepository.incrementLikes(1L, true);
        int likes = postRepository.findById(1L).get().getLikesCount();
        assertEquals(6, likes);
    }

    @Test
    @DisplayName("Уменьшение счётчика лайков (но не ниже 0)")
    void incrementLikes_shouldDecreaseLikes_whenIncrementIsFalse() {
        postRepository.incrementLikes(2L, false);
        int likes = postRepository.findById(2L).get().getLikesCount();
        assertEquals(1, likes);

        postRepository.incrementLikes(2L, false);
        postRepository.incrementLikes(2L, false);
        likes = postRepository.findById(2L).get().getLikesCount();
        assertEquals(0, likes);
    }

    @Test
    @DisplayName("Получение постов по тэгу")
    void findAllByTag_shouldReturnPostsWithGivenTag() {
        jdbcTemplate.getJdbcTemplate().execute("""
            CREATE TABLE IF NOT EXISTS tags (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255)
            );
        """);
        jdbcTemplate.getJdbcTemplate().execute("""
            CREATE TABLE IF NOT EXISTS post_tags (
                post_id BIGINT,
                tag_id BIGINT
            );
        """);
        jdbcTemplate.getJdbcTemplate().execute("""
            INSERT INTO tags (name) VALUES ('java');
            INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1);
        """);

        List<Post> posts = postRepository.findAllByTag("java", 10, 0);
        assertEquals(1, posts.size());
        assertEquals("Пост 1", posts.get(0).getTitle());
    }

}
