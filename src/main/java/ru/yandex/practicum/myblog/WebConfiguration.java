package ru.yandex.practicum.myblog;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.yandex.practicum.myblog.configuration.DatabaseConfig;
import ru.yandex.practicum.myblog.configuration.MultipartConfig;
import ru.yandex.practicum.myblog.configuration.ThymeleafConfig;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.yandex.practicum.myblog"})
@PropertySource("classpath:application.properties")
@Import({DatabaseConfig.class, ThymeleafConfig.class, MultipartConfig.class})
public class WebConfiguration {}
