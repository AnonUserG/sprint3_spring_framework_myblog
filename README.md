# MyBlog

Java блог-приложение на основе Spring Boot и H2.

### 🚀 Как запустить проект на Windows

#### 📦 Требования
- Java 21+
- IntelliJ IDEA (рекомендуется)
- Git

#### 📁 Запуск

1. Клонируй ветку myblog_boot из репозитория используя git bash
   ```bash
   git clone --branch myblog_boot --single-branch https://github.com/AnonUserG/sprint3_spring_framework_myblog.git

2. Перейди в папку с проектом
3. Измени в application.yml переменную upload:path:"..." (Укажи свой путь, где будут храниться загруженные изображения)
4. Запусти проект (тесты прогонятся автоматически)
   ```bash
   gradlew.bat clean build bootRun
5. Перейди в браузере на [http://localhost:8080/](http://localhost:8080/)
