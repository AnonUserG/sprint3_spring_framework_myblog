# MyBlog

Java блог-приложение без Spring Boot на основе Spring Framework 6, Maven и H2.

### 🚀 Как запустить проект

#### 📦 Требования
- Java 21+
- Apache Tomcat 11+
- Maven 3.8+
- IntelliJ IDEA (рекомендуется)
- Git

#### 📁 Установка

1. Клонируй репозиторий
   ```bash
   git clone https://github.com/AnonUserG/sprint3_spring_framework_myblog.git
 
2. Измени в application.properties переменную upload.path= (Укажи свой путь где будут храниться загруженные изображения)
3. Перейди в папку с проектом
4. Собери проект (тесты прогонятся сами)
   ```bash
   mvn clean package
5. Разверни в Tomcat:
   - Скопируй содержимое из target/myblog_war_exploded.war в папку webapps/myblog_war_exploded на локальном Tomcat 11
   - ИЛИ
   - Добавь Tomcat 11 в Configurations проекта и используй артефакт типа war exploded с именем myblog_war_exploded
