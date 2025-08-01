-- Удаление таблиц (в правильном порядке из-за внешних ключей)
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS likes;

-- Таблица постов
CREATE TABLE posts (
                       id IDENTITY PRIMARY KEY,
                       title VARCHAR(500) NOT NULL,
                       text CLOB NOT NULL,
                       image_path VARCHAR(255),
                       likes_count INT DEFAULT 0
);

-- Таблица комментариев
CREATE TABLE comments (
                          id IDENTITY PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          text CLOB NOT NULL,
                          CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Таблица тегов
CREATE TABLE tags (
                      id IDENTITY PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE
);

-- Связь постов и тегов (многие-ко-многим)
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
                           PRIMARY KEY (post_id, tag_id)
);
-- Вставка 3 простых постов
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


-- Таблица лайков (агрегированно, без привязки к пользователям)
/*CREATE TABLE likes (
                       post_id BIGINT NOT NULL,
                       direction VARCHAR(10) NOT NULL CHECK (direction IN ('UP', 'DOWN')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);*/


