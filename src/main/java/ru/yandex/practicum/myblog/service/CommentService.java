package ru.yandex.practicum.myblog.service;

public interface CommentService {
    void addComment(Long postId, String text);
    void editComment(Long commentId, String newText);
    void deleteComment(Long commentId);
}

