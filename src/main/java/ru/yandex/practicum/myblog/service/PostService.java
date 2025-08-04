package ru.yandex.practicum.myblog.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.myblog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getAllPosts(String tag, int pageSize, int pageNumber);
    Optional<Post> getPostById(Long id);
    @Transactional
    Long createPost(Post post, List<String> tagNames);
    @Transactional
    void editPost(Post post, List<String> tagNames);
    void deletePost(Long postId);
    void likePost(Long postId, boolean isLike);

}
