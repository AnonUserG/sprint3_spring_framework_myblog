package ru.practicum.myblog_boot.service;

import ru.practicum.myblog_boot.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getAllPosts(String tag, int pageSize, int pageNumber);
    Optional<Post> getPostById(Long id);
    Long createPost(Post post, List<String> tagNames);
    void editPost(Post post, List<String> tagNames);
    void deletePost(Long postId);
    void likePost(Long postId, boolean isLike);

}
