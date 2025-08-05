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
import ru.yandex.practicum.myblog.model.Comment;
import ru.yandex.practicum.myblog.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfigRepository.class)
public class CommentRepositoryTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.getJdbcTemplate().execute("""
        DROP TABLE IF EXISTS comments;
        """);

        jdbcTemplate.getJdbcTemplate().execute("""
        CREATE TABLE comments (
            id IDENTITY PRIMARY KEY,
            post_id BIGINT,
            text VARCHAR(255)
        );
        """);

        jdbcTemplate.getJdbcTemplate().execute("""
        INSERT INTO comments (post_id, text) VALUES (1, 'Test comment 1');
        """);
        jdbcTemplate.getJdbcTemplate().execute("""
        INSERT INTO comments (post_id, text) VALUES (2, 'Test comment 2');
        """);
        jdbcTemplate.getJdbcTemplate().execute("""
        INSERT INTO comments (post_id, text) VALUES (3, 'Another comment');
        """);

    }

    @Test
    @DisplayName("Сохранение комментария")
    void save_shouldInsertComment() {
        Comment comment = new Comment();
        comment.setPostId(4L);
        comment.setText("New comment");

        commentRepository.save(comment);

        List<Comment> result = commentRepository.findAllByPostId(4L);
        assertEquals(1, result.size());
        assertEquals("New comment", result.get(0).getText());
    }

    @Test
    @DisplayName("Обновление комментария")
    void update_shouldUpdateComment() {
        List<Comment> comments = commentRepository.findAllByPostId(1L);
        Comment comment = comments.get(0);
        comment.setText("Updated text");

        commentRepository.update(comment);

        Optional<Comment> updated = commentRepository.findById(comment.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated text", updated.get().getText());
    }

    @Test
    @DisplayName("Удаление комментария")
    void deleteById_shouldDeleteComment() {

        List<Comment> comments = commentRepository.findAllByPostId(1L);
        Long idToDelete = comments.get(0).getId();

        commentRepository.deleteById(idToDelete);

        Optional<Comment> deleted = commentRepository.findById(idToDelete);
        assertTrue(deleted.isEmpty());
    }

    @Test
    @DisplayName("Поиск всех комментариев по post_id")
    void findAllByPostId_shouldReturnComments() {

        List<Comment> result = commentRepository.findAllByPostId(1L);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Поиск комментария")
    void findById_shouldReturnComment() {

        Long commentId = 1L;

        Optional<Comment> found = commentRepository.findById(commentId);
        assertTrue(found.isPresent());
        assertEquals("Test comment 1", found.get().getText());
    }

}
