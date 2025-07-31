package ru.yandex.practicum.myblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.myblog.service.CommentServiceImpl;

@Controller
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    // POST /posts/{postId}/comments — добавление комментария
    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @RequestParam String text) {

        commentService.addComment(postId, text);
        return "redirect:/posts/" + postId;
    }

    // POST /posts/{postId}/comments/{commentId} — редактирование комментария
    @PostMapping("/{commentId}")
    public String editComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              @RequestParam String text) {

        commentService.editComment(commentId, text);
        return "redirect:/posts/" + postId;
    }

    // POST /posts/{postId}/comments/{commentId}/delete — удаление комментария
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }
}