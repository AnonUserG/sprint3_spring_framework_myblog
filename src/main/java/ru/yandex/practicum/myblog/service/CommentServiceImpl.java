package ru.yandex.practicum.myblog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.myblog.model.Comment;
import ru.yandex.practicum.myblog.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void addComment(Long postId, String text) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setText(text);
        commentRepository.save(comment);
    }

    @Override
    public void editComment(Long commentId, String text) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            comment.setText(text);
            commentRepository.update(comment);
        });
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
