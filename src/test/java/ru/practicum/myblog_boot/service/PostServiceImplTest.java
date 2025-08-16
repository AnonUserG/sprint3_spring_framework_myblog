package ru.practicum.myblog_boot.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.practicum.myblog_boot.model.Comment;
import ru.practicum.myblog_boot.model.Post;
import ru.practicum.myblog_boot.repository.CommentRepository;
import ru.practicum.myblog_boot.repository.PostRepository;
import ru.practicum.myblog_boot.repository.TagRepository;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class PostServiceImplTest {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private TagRepository tagRepository;

    @MockitoBean
    private CommentRepository commentRepository;


    @Test
    @DisplayName("Получение всех постов")
    void getAllPosts_shouldReturnPostsWithTagsAndComments() {

        Post post = new Post(1L, "Title", "Text", null, 0, new ArrayList<>(), new ArrayList<>());
        when(postRepository.findAll(10, 0)).thenReturn(List.of(post));
        when(tagRepository.getTagsForPost(1L)).thenReturn(List.of("tag1"));
        when(commentRepository.findAllByPostId(1L)).thenReturn(List.of(new Comment(1L, 1L, "comment")));

        List<Post> posts = postService.getAllPosts(null, 10, 1);

        assertEquals(1, posts.size());
        assertEquals("tag1", posts.get(0).getTags().get(0));
        assertEquals("comment", posts.get(0).getComments().get(0).getText());
    }

    @Test
    @DisplayName("Создание поста")
    void createPost_shouldSavePostAndBindTags() {
        Post post = new Post("Title", "Text", null);
        when(postRepository.save(any(Post.class))).thenReturn(1L);

        Long id = postService.createPost(post, List.of("tag1", "tag2"));

        assertEquals(1L, id);
        verify(postRepository).save(any(Post.class));
        verify(tagRepository).bindPostTags(eq(1L), eq(List.of("tag1", "tag2")));
    }


    @Test
    @DisplayName("Редактирование поста")
    void editPost_shouldUpdatePostAndTags() {
        Post post = new Post(1L, "Title", "Text", null, 0, null, null);

        postService.editPost(post, List.of("tag1"));

        verify(postRepository).update(post);
        verify(tagRepository).bindPostTags(1L, List.of("tag1"));
    }

    @Test
    @DisplayName("Удаление поста")
    void deletePost_shouldCallRepository() {
        postService.deletePost(42L);
        verify(postRepository).deleteById(42L);
    }

    @Test
    @DisplayName("Лайки поста")
    void likePost_shouldCallIncrementLikes() {
        postService.likePost(7L, true);
        verify(postRepository).incrementLikes(7L, true);
    }

}
