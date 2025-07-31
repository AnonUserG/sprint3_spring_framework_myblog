package ru.yandex.practicum.myblog.repository;

import ru.yandex.practicum.myblog.model.Tag;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TagRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Tag> tagRowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        return tag;
    };

    public Optional<Tag> findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = :name";
        List<Tag> result = jdbcTemplate.query(sql, Map.of("name", name), tagRowMapper);
        return result.stream().findFirst();
    }

    public Long saveIfNotExists(String name) {
        Optional<Tag> existing = findByName(name);
        if (existing.isPresent()) {
            return existing.get().getId();
        }

        String sql = "INSERT INTO tags (name) VALUES (:name)";
        jdbcTemplate.update(sql, Map.of("name", name));

        return findByName(name).map(Tag::getId).orElseThrow(); // повторный запрос
    }

    public void bindPostTags(Long postId, List<String> tagNames) {
        String deleteSql = "DELETE FROM post_tags WHERE post_id = :postId";
        jdbcTemplate.update(deleteSql, Map.of("postId", postId));

        for (String tagName : tagNames) {
            Long tagId = saveIfNotExists(tagName.trim());
            String insertSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (:postId, :tagId)";
            jdbcTemplate.update(insertSql, Map.of("postId", postId, "tagId", tagId));
        }
    }

    public List<String> getTagsForPost(Long postId) {
        String sql = """
            SELECT t.name FROM tags t
            JOIN post_tags pt ON t.id = pt.tag_id
            WHERE pt.post_id = :postId
        """;
        return jdbcTemplate.queryForList(sql, Map.of("postId", postId), String.class);
    }
}
