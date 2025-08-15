package ru.practicum.myblog_boot.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.myblog_boot.model.Post;
import ru.practicum.myblog_boot.service.PostService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Value("${upload.path}")
    private String uploadPath;

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // GET "/" - редирект на "/posts"
    @GetMapping("/")
    public String root() {
        return "redirect:/posts";
    }

    // GET "posts" - список постов
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

    // GET "/posts/{id}" - страница с постом
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        model.addAttribute("post", postOpt.get());
        return "post";
    }

    // GET "/posts/add" - форма добавления поста
    @GetMapping("/posts/add")
    public String addPostForm(Model model) {
        model.addAttribute("post", null);
        return "add-post";
    }

    // GET "/images/{id}" - отдача байтов картинки
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty() || postOpt.get().getImagePath() == null) {
            return ResponseEntity.notFound().build();
        }

        String imagePath = Paths.get(uploadPath, postOpt.get().getImagePath()).toString();
        File imgFile = new File(imagePath);

        if (!imgFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Files.readAllBytes(imgFile.toPath());
        String contentType = Files.probeContentType(imgFile.toPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "image/jpeg")
                .body(imageBytes);
    }

    // POST "/posts" - добавление поста
    @PostMapping("/posts")
    public String createPost(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "tags", defaultValue = "") String tags) throws IOException {

        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setLikesCount(0);
        List<String> tagList = parseTags(tags);


        if (image != null && !image.isEmpty()) {
            post.setImagePath(saveImage(image));
        }

        Long id = postService.createPost(post, tagList);
        post.setId(id);

        if (id == null) {
            return "redirect:/posts";
        }
        return "redirect:/posts/" + id;
    }

    // POST "/posts/{id}" - редактирование поста
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
            post.setImagePath(saveImage(image));
        }

        List<String> tagList = parseTags(tags);
        postService.editPost(post, tagList);

        return "redirect:/posts/" + id;
    }


    // POST "/posts/{id}/like" - лайк/дизлайк
    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable("id") Long id, @RequestParam boolean like) {
        postService.likePost(id, like);
        return "redirect:/posts/" + id;
    }

    // POST "/posts/{id}/edit" - форма редактирования поста
    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable("id") Long id, Model model) {
        Optional<Post> postOpt = postService.getPostById(id);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        model.addAttribute("post", postOpt.get());
        return "add-post";
    }

    // POST "/posts/{id}/delete" - удаление поста
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir.getPath(), fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


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