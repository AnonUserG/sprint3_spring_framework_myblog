package ru.yandex.practicum.myblog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.myblog.model.Comment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CommentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CommentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setText(rs.getString("text"));
        return comment;
    };

    public void save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, text) VALUES (:postId, :text)";
        jdbcTemplate.update(sql, Map.of(
                "postId", comment.getPostId(),
                "text", comment.getText()
        ));
    }

    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = :text WHERE id = :id";
        jdbcTemplate.update(sql, Map.of(
                "id", comment.getId(),
                "text", comment.getText()
        ));
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    public List<Comment> findAllByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = :postId ORDER BY id ASC";
        return jdbcTemplate.query(sql, Map.of("postId", postId), commentRowMapper);
    }

    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = :id";
        List<Comment> result = jdbcTemplate.query(sql, Map.of("id", id), commentRowMapper);
        return result.stream().findFirst();
    }
}
