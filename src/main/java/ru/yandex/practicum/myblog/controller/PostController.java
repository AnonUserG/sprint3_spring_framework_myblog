package ru.yandex.practicum.myblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.myblog.model.Post;
import ru.yandex.practicum.myblog.service.PostService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private final PostService postService;

    // Для работы с изображениями нужно предусмотреть хранение и отдачу байтов,
    // но пока предполагаем, что post.getImagePath() содержит нужные данные.

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // а) GET "/" - редирект на "/posts"
    @GetMapping("/")
    public String root() {
        return "redirect:/posts";
    }

    // б) GET "posts" - список постов
    @GetMapping("/posts")
    public String listPosts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {

        List<Post> posts = postService.getAllPosts(search, pageSize, pageNumber);

        boolean hasPrevious = pageNumber > 1;
        boolean hasNext = posts.size() == pageSize;

        model.addAttribute("posts", posts);
        model.addAttribute("search", search);
        model.addAttribute("paging", new Paging(pageNumber, pageSize, hasNext, hasPrevious));

        return "posts";
    }

    // в) GET "/posts/{id}" - страница с постом
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        model.addAttribute("post", postOpt.get());
        return "post";
    }

    // г) GET "/posts/add" - форма добавления поста
    @GetMapping("/posts/add")
    public String addPostForm(Model model) {
        model.addAttribute("post", null);
        return "add-post";
    }

    // д) POST "/posts" - добавление поста
    @PostMapping("/posts")
    public String createPost(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "tags", defaultValue = "") String tags) throws IOException {

        Post post = new Post();
        post.setTitle(title);
        post.setText(text);

        if (image != null && !image.isEmpty()) {
            // пока просто сохраняем путь/имя
            // post.setImagePath(saveImage(image));
            post.setImagePath(image.getOriginalFilename()); // пример
        }

        List<String> tagList = parseTags(tags);

        postService.createPost(post, tagList);
        Long id = post.getId();
        if (id == null) {
            return "redirect:/posts";
        }
        return "redirect:/posts/" + id;


    }

    // е) GET "/images/{id}" - отдача байтов картинки
    @GetMapping("/images/{id}")
    public void getImage(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty() || postOpt.get().getImagePath() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //надо получить байты картинки из хранилища

        byte[] imageBytes = loadImageBytes(postOpt.get().getImagePath());

        response.setContentType("image/jpeg"); // или другой mime type
        response.getOutputStream().write(imageBytes);
    }

    // ж) POST "/posts/{id}/like" - лайк/дизлайк
    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable("id") Long id, @RequestParam boolean like) {
        postService.likePost(id, like);
        return "redirect:/posts/" + id;
    }

    // з) POST "/posts/{id}/edit" - форма редактирования поста
    @PostMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        model.addAttribute("post", postOpt.get());
        return "add-post";
    }

    // и) POST "/posts/{id}" - редактирование поста
    @PostMapping("/posts/{id}")
    public String editPost(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "tags", defaultValue = "") String tags) throws IOException {

        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        Post post = postOpt.get();
        post.setTitle(title);
        post.setText(text);

        if (image != null && !image.isEmpty()) {
            //post.setImagePath(saveImage(image));
            post.setImagePath(image.getOriginalFilename());
        }

        List<String> tagList = parseTags(tags);
        postService.editPost(post, tagList);

        return "redirect:/posts/" + id;
    }

    // к) POST "/posts/{id}/comments" - добавление комментария
    @PostMapping("/posts/{id}/comments")
    public String addComment(
            @PathVariable("id") Long id,
            @RequestParam("text") String text) {

        // нужна реализация добавления комментария, её нет в PostService


        return "redirect:/posts/" + id;
    }

    // л) POST "/posts/{id}/comments/{commentId}" - редактирование комментария
    @PostMapping("/posts/{id}/comments/{commentId}")
    public String editComment(
            @PathVariable("id") Long id,
            @PathVariable("commentId") Long commentId,
            @RequestParam("text") String text) {

        //нужно реализовать в сервисе postService.editComment(commentId, text);

        return "redirect:/posts/" + id;
    }

    // м) POST "/posts/{id}/comments/{commentId}/delete" - удаление комментария
    @PostMapping("/posts/{id}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable("id") Long id,
            @PathVariable("commentId") Long commentId) {

        // нужно реализовать в сервисе postService.deleteComment(commentId);

        return "redirect:/posts/" + id;
    }

    // н) POST "/posts/{id}/delete" - удаление поста
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    // Вспомогательный метод для парсинга тегов из строки
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // Заглушка загрузки картинки по пути
    private byte[] loadImageBytes(String imagePath) {
        // TODO: реализовать загрузку изображения из файловой системы, базы или другого хранилища
        return new byte[0];
    }

    // Класс для передачи информации о пагинации в модель
    public static class Paging {
        private final int pageNumber;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;

        public Paging(int pageNumber, int pageSize, boolean hasNext, boolean hasPrevious) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public int pageNumber() {
            return pageNumber;
        }

        public int pageSize() {
            return pageSize;
        }

        public boolean hasNext() {
            return hasNext;
        }

        public boolean hasPrevious() {
            return hasPrevious;
        }
    }
}