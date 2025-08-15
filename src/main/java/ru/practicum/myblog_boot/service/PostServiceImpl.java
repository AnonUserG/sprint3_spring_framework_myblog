package ru.practicum.myblog_boot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.myblog_boot.model.Post;
import ru.practicum.myblog_boot.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(PostRepository postRepository,
                           TagRepository tagRepository,
                           CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    @Override
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

    @Override
    public Optional<Post> getPostById(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        postOpt.ifPresent(post -> {
            post.setTags(tagRepository.getTagsForPost(post.getId()));
            post.setComments(commentRepository.findAllByPostId(post.getId()));
        });
        return postOpt;
    }

    @Override
    @Transactional
    public Long createPost(Post post, List<String> tagNames) {
        post.setLikesCount(0);

        Long id = postRepository.save(post);
        post.setId(id);
        tagRepository.bindPostTags(id, tagNames);

        return id;
    }

    @Override
    @Transactional
    public void editPost(Post post, List<String> tagNames) {
        postRepository.update(post);
        tagRepository.bindPostTags(post.getId(), tagNames);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public void likePost(Long postId, boolean isLike) {
        postRepository.incrementLikes(postId, isLike);
    }
}