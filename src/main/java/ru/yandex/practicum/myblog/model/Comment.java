package ru.yandex.practicum.myblog.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private Long id;

    private Long postId;
    private String text;

}