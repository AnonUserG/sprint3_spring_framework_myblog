package ru.practicum.myblog_boot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private Long id;

    private String title;
    private String text;
    private String imagePath;
    private int likesCount;

    @Transient
    private List<String> tags = new ArrayList<>();

    @Transient
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String text, String imagePath) {
        this.title = title;
        this.text = text;
        this.imagePath = imagePath;
        this.likesCount = 0;
    }

    public String getTextPreview() {
        if (text == null) return "";
        int maxLength = 300;
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    public List<String> getTextParts() {
        if (text == null) return new ArrayList<>();
        return Arrays.asList(text.split("\n"));
    }

    public String getTagsAsText() {
        if (tags == null || tags.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String  tag : tags) {
            sb.append(tag.trim()).append(", ");
        }
        return sb.toString().trim();
    }
}
