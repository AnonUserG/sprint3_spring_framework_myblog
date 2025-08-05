package ru.yandex.practicum.myblog.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.myblog.model.Post;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PostRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setImagePath(rs.getString("image_path"));
        post.setLikesCount(rs.getInt("likes_count"));
        return post;
    };

    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = :id";
        List<Post> result = jdbcTemplate.query(sql, Map.of("id", id), postRowMapper);
        return result.stream().findFirst();
    }

    public List<Post> findAll(int limit, int offset) {
        String sql = "SELECT * FROM posts ORDER BY id DESC LIMIT :limit OFFSET :offset";
        return jdbcTemplate.query(sql, Map.of("limit", limit, "offset", offset), postRowMapper);
    }

    public List<Post> findAllByTag(String tag, int limit, int offset) {
        String sql = """
            SELECT p.* FROM posts p
            JOIN post_tags pt ON p.id = pt.post_id
            JOIN tags t ON pt.tag_id = t.id
            WHERE t.name = :tag
            ORDER BY p.id DESC
            LIMIT :limit OFFSET :offset
        """;
        Map<String, Object> params = Map.of(
                "tag", tag,
                "limit", limit,
                "offset", offset
        );
        return jdbcTemplate.query(sql, params, postRowMapper);
    }

    public Long save(Post post) {
        String sql = """
        INSERT INTO posts (title, text, image_path, likes_count)
        VALUES (:title, :text, :imagePath, :likesCount)
    """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", post.getTitle())
                .addValue("text", post.getText())
                .addValue("imagePath", post.getImagePath())
                .addValue("likesCount", post.getLikesCount());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key != null) {
            return key.longValue();
        } else {
            throw new RuntimeException("Failed to retrieve generated key.");
        }
    }

    public void update(Post post) {
        String sql = """
            UPDATE posts
            SET title = :title, text = :text, image_path = :imagePath, likes_count = :likesCount
            WHERE id = :id
        """;
        Map<String, Object> params = new HashMap<>();
        params.put("id", post.getId());
        params.put("title", post.getTitle());
        params.put("text", post.getText());
        params.put("imagePath", post.getImagePath());
        params.put("likesCount", post.getLikesCount());
        jdbcTemplate.update(sql, params);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM posts WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public void incrementLikes(Long postId, boolean increment) {
        String sql = increment ?
                "UPDATE posts SET likes_count = likes_count + 1 WHERE id = :id" :
                "UPDATE posts SET likes_count = GREATEST(likes_count - 1, 0) WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", postId));
    }

}
