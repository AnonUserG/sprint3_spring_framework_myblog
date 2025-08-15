-- Удаление таблиц
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS likes;

-- Таблица постов
CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(500) NOT NULL,
                       text TEXT NOT NULL,
                       image_path VARCHAR(255),
                       likes_count INT DEFAULT 0
);

-- Таблица комментариев
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          text TEXT NOT NULL,
                          CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Таблица тегов
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE
);

-- Связь постов и тегов (многие ко многим)
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           PRIMARY KEY (post_id, tag_id),
                           CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Первичное наполнение
INSERT INTO posts (title, text, image_path, likes_count) VALUES
                                                             ('Первый пост', 'Это текст первого поста.', NULL, 0),
                                                             ('Второй пост', 'Это текст второго поста.', NULL, 2),
                                                             ('Третий пост', 'Это текст третьего поста.', NULL, 4),
                                                             ('Четвертый пост', 'Это текст четвертого поста.', NULL, 3),
                                                             ('Пятый пост', 'Это текст пятого поста.', NULL, 0),
                                                             ('Шестой пост', 'Это текст шестого поста.', NULL, 2),
                                                             ('Седьмой пост', 'Это текст седьмого поста.', NULL, 1);
