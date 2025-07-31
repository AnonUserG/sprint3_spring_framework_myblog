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

-- Таблица лайков (агрегированно, без привязки к пользователям)
/*CREATE TABLE likes (
                       post_id BIGINT NOT NULL,
                       direction VARCHAR(10) NOT NULL CHECK (direction IN ('UP', 'DOWN')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);*/


