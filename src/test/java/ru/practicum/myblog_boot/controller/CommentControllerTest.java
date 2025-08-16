package ru.practicum.myblog_boot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.myblog_boot.service.CommentService;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

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
