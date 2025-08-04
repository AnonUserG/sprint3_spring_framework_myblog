package repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.yandex.practicum.myblog.repository.CommentRepository;
import ru.yandex.practicum.myblog.repository.PostRepository;
import ru.yandex.practicum.myblog.repository.TagRepository;

import javax.sql.DataSource;

@Configuration
public class TestConfigRepository {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:usersdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public CommentRepository commentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new CommentRepository(jdbcTemplate);
    }

    @Bean
    public PostRepository postRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new PostRepository(jdbcTemplate);
    }

    @Bean
    public TagRepository tagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new TagRepository(jdbcTemplate);
    }
}
