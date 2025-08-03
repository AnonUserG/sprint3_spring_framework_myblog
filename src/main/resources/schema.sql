-- Удаление таблиц
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS likes;

-- Таблица постов
CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(500) NOT NULL,
                       text TEXT NOT NULL,
                       image_path VARCHAR(255) DEFAULT 'D:\\sprint3.myblog\\',
                       likes_count INT DEFAULT 0
);

-- Таблица комментариев
CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          text TEXT NOT NULL,
                          CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Таблица тегов
CREATE TABLE tags (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE
);

-- Связь постов и тегов
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           PRIMARY KEY (post_id, tag_id),
                           CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Вставка постов
INSERT INTO posts (title, text, image_path, likes_count) VALUES
                                                             ('Первый пост', 'Это текст первого поста.', NULL, 0),
                                                             ('Второй пост', 'Это текст второго поста.', NULL, 0),
                                                             ('Третий пост', 'Это текст третьего поста.', NULL, 0),
                                                             ('Четвертый пост', 'Это текст первого поста.', NULL, 0),
                                                             ('Пятый пост', 'Это текст второго поста.', NULL, 0),
                                                             ('Шестой пост', 'Это текст третьего поста.', NULL, 0),
                                                             ('Седьмой пост', 'Это текст первого поста.', NULL, 0),
                                                             ('Восьмой пост', 'Это текст второго поста.', NULL, 0),
                                                             ('Девятый пост', 'Это текст третьего поста.', NULL, 0),
                                                             ('Десятый пост', 'Это текст первого поста.', NULL, 0),
                                                             ('Одиннадцатый пост', 'Это текст второго поста.', NULL, 0),
                                                             ('Двенадцатый пост', 'Это текст третьего поста.', NULL, 0);
