package controller;

import controller.config.TestConfigController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.myblog.controller.CommentController;
import ru.yandex.practicum.myblog.service.CommentService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfigController.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private CommentController commentController;

    @Autowired
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    @DisplayName("POST /posts/{postId}/comments - добавление комментария")
    void addComment_shouldRedirectAndCallService() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .param("text", "Отличный пост!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(commentService).addComment(postId, "Отличный пост!");
    }

    @Test
    @DisplayName("POST /posts/{postId}/comments/{commentId} — редактирование комментария")
    void editComment_shouldRedirectAndCallService() throws Exception {
        Long postId = 1L;
        Long commentId = 7L;

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .param("text", "Обновлённый текст"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(commentService).editComment(commentId, "Обновлённый текст");
    }

    @Test
    @DisplayName("/posts/{postId}/comments/{commentId}/delete — удаление комментария")
    void deleteComment_shouldRedirectAndCallService() throws Exception {
        Long postId = 1L;
        Long commentId = 7L;

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/delete", postId, commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(commentService).deleteComment(commentId);
    }

}
