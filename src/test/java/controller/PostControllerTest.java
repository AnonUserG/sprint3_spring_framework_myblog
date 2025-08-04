package controller;

import config.TestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.yandex.practicum.myblog.controller.PostController;
import ru.yandex.practicum.myblog.model.Post;
import ru.yandex.practicum.myblog.service.PostService;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private PostController postController;

    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("GET / - редирект на /posts")
    void root_shouldRedirectToPosts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

    }

    @Test
    @DisplayName("GET /posts - список постов с параметрами")
    void listPosts_shouldReturnPostsPage() throws Exception {
        List<Post> posts = Collections.singletonList(new Post());
        when(postService.getAllPosts("test", 5, 1)).thenReturn(posts);

        mockMvc.perform(get("/posts")
                        .param("search", "test")
                        .param("pageSize", "5")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("posts", posts))
                .andExpect(model().attribute("search", "test"))
                .andExpect(model().attributeExists("paging"));

        verify(postService).getAllPosts("test", 5, 1);
    }

    @Test
    @DisplayName("GET /posts/{id} - страница с постом")
    void getPost_shouldReturnPostView() throws Exception {
        Long id = 12L;
        Post post = new Post();

        when(postService.getPostById(id)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attribute("post", post));

        verify(postService).getPostById(id);
    }

    @Test
    @DisplayName("GET /posts/add - форма добавления поста")
    void addPostForm_shouldReturnAddPostView() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attribute("post", nullValue()));
    }

    @Test
    @DisplayName("GET /images/{id} - отдаёт изображение")
    void getImage_shouldReturnImageBytes() throws Exception {
        Long id = 15L;
        Post post = new Post();
        post.setImagePath("testImage.jpg");

        when(postService.getPostById(id)).thenReturn(Optional.of(post));

        File tempFile = File.createTempFile("testImage", ".jpg");
        Files.write(tempFile.toPath(), "image content".getBytes());

        java.lang.reflect.Field field = PostController.class.getDeclaredField("uploadPath");
        field.setAccessible(true);
        field.set(postController, tempFile.getParent());

        File renamed = new File(tempFile.getParent(), post.getImagePath());
        tempFile.renameTo(renamed);

        mockMvc.perform(get("/images/{id}", id))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, startsWith("image/")))
                .andExpect(content().bytes(Files.readAllBytes(renamed.toPath())));

        renamed.delete();
    }

    @Test
    @DisplayName("POST /posts - добавление поста")
    void createPost_shouldRedirect() throws Exception {
        Long id = 23L;
        when(postService.createPost(any(Post.class), anyList())).thenReturn(id);

        mockMvc.perform(post("/posts")
                .param("title", "Test Title")
                .param("text", "Test Text")
                .param("tags", "tag1,tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + id));

        verify(postService).createPost(any(Post.class), eq(List.of("tag1", "tag2")));

    }

    @Test
    @DisplayName("POST /posts/{id} - редактирование поста")
    void editPost_shouldRedirect() throws Exception {
        Long id = 20L;
        Post post = new Post();
        when(postService.getPostById(id)).thenReturn(Optional.of(post));

        mockMvc.perform(post("/posts/{id}", id)
                .param("title", "Test Title")
                .param("text", "Test Text")
                .param("tags", "tag1,tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + id));

        verify(postService).editPost(any(Post.class), eq(List.of("tag1", "tag2")));
    }

    @Test
    @DisplayName("POST /posts/{id}/like - лайк/дизлайк поста")
    void likePost_shouldRedirect() throws Exception {
        Long id = 20L;

        mockMvc.perform(post("/posts/{id}/like", id)
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + id));

        verify(postService).likePost(id, true);
    }

    @Test
    @DisplayName("GET /posts/{id}/edit - форма редактирования поста")
    void editPostForm_shouldReturnAddPostView() throws Exception {
        Long id = 20L;

        Post post = new Post();
        when(postService.getPostById(id)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    @DisplayName("POST /posts/{id}/delete - удаление поста")
    void deletePost_shouldRedirect() throws Exception {
        Long id = 200L;

        mockMvc.perform(post("/posts/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        verify(postService).deletePost(id);
    }

}
