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
import ru.yandex.practicum.myblog.model.Tag;
import ru.yandex.practicum.myblog.repository.TagRepository;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfigRepository.class)
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUpSchema() {
        jdbcTemplate.getJdbcTemplate().execute("""
            CREATE TABLE IF NOT EXISTS tags (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255) UNIQUE
            );

            CREATE TABLE IF NOT EXISTS post_tags (
                post_id BIGINT,
                tag_id BIGINT,
                PRIMARY KEY (post_id, tag_id)
            );
        """);
    }

    @Test
    @DisplayName("Поиск тега по имени должен возвращать Optional.empty, если тег не найден")
    void findByName_shouldReturnEmptyIfNotFound() {
        Optional<Tag> tag = tagRepository.findByName("nonexistent");
        assertTrue(tag.isEmpty());
    }

    @Test
    @DisplayName("Сохранение нового тега должно возвращать его ID")
    void saveIfNotExists_shouldInsertTagAndReturnId() {
        Long id = tagRepository.saveIfNotExists("spring");
        assertNotNull(id);

        Optional<Tag> fetched = tagRepository.findByName("spring");
        assertTrue(fetched.isPresent());
        assertEquals(id, fetched.get().getId());
    }


    @Test
    @DisplayName("Сохранение существующего тега не должно дублировать запись")
    void saveIfNotExists_shouldNotDuplicate() {
        Long id1 = tagRepository.saveIfNotExists("java");
        Long id2 = tagRepository.saveIfNotExists("java");

        assertEquals(id1, id2);
    }

    @Test
    @DisplayName("Должен связывать пост с тегами")
    void bindPostTags_shouldBindTagsToPost() {
        Long postId = 100L;
        tagRepository.bindPostTags(postId, List.of("spring", "jdbc"));

        List<String> tags = tagRepository.getTagsForPost(postId);
        assertEquals(2, tags.size());
        assertTrue(tags.contains("spring"));
        assertTrue(tags.contains("jdbc"));
    }

    @Test
    @DisplayName("Должен возвращать все теги, связанные с постом")
    void getTagsForPost_shouldReturnBoundTags() {
        Long postId = 200L;
        tagRepository.bindPostTags(postId, List.of("junit", "mockito"));

        List<String> tags = tagRepository.getTagsForPost(postId);
        assertEquals(List.of("junit", "mockito"), tags);
    }

}
