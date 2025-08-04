package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.myblog.model.Comment;
import ru.yandex.practicum.myblog.repository.CommentRepository;
import ru.yandex.practicum.myblog.service.CommentService;
import service.config.TestConfigService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfigService.class)
public class CommentServiceImplTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("POST Добавление комментария")
    void addComment_shouldSaveCommentWithCorrectData() {
        Long postId = 1L;
        String text = "Test comment";

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        commentService.addComment(postId, text);

        verify(commentRepository, times(1)).save(captor.capture());
        Comment savedComment = captor.getValue();

        assertEquals(postId, savedComment.getPostId());
        assertEquals(text, savedComment.getText());
    }

    @Test
    @DisplayName("Редактирование комментария")
    void editComment_shouldUpdateCommentIfFound() {
        Long commentId = 2L;
        String newText = "Updated comment";
        Comment comment = new Comment(commentId, 1L, "Old text");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.editComment(commentId, newText);

        assertEquals(newText, comment.getText());
        verify(commentRepository, times(1)).update(comment);
    }

    @Test
    @DisplayName("Удаление комментария")
    void deleteComment_shouldCallDeleteById() {
        commentService.deleteComment(1L);
        verify(commentRepository,times(1)).deleteById(1L);
    }

}
