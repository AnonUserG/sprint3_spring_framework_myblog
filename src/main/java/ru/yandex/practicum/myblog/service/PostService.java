package ru.yandex.practicum.myblog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.myblog.model.Post;
import ru.yandex.practicum.myblog.repository.CommentRepository;
import ru.yandex.practicum.myblog.repository.PostRepository;
import ru.yandex.practicum.myblog.repository.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository,
                       TagRepository tagRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> getAllPosts(String tag, int pageSize, int pageNumber) {
        int offset = (pageNumber - 1) * pageSize;
        List<Post> posts;

        if (tag == null || tag.isBlank()) {
            posts = postRepository.findAll(pageSize, offset);
        } else {
            posts = postRepository.findAllByTag(tag, pageSize, offset);
        }

        for (Post post : posts) {
            post.setTags(tagRepository.getTagsForPost(post.getId()));
            post.setComments(commentRepository.findAllByPostId(post.getId()));
        }

        return posts;
    }

    public Optional<Post> getPostById(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        postOpt.ifPresent(post -> {
            post.setTags(tagRepository.getTagsForPost(post.getId()));
            post.setComments(commentRepository.findAllByPostId(post.getId()));
        });
        return postOpt;
    }

    @Transactional
    public void createPost(Post post, List<String> tagNames) {
        post.setLikesCount(0);
        postRepository.save(post);

        Long id = postRepository.save(post);
        post.setId(id);
        tagRepository.bindPostTags(id, tagNames);
    }

    @Transactional
    public void editPost(Post post, List<String> tagNames) {
        postRepository.update(post);
        tagRepository.bindPostTags(post.getId(), tagNames);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public void likePost(Long postId, boolean isLike) {
        postRepository.incrementLikes(postId, isLike);
    }
}