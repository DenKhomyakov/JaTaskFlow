CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    author_id BIGINT,
    executor_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (executor_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
    );

INSERT INTO users (firstname, lastname, email, password, role)
VALUES ('Admin', 'Adminov', 'admin@mail.com', '{bcrypt}$2a$10$g8ROG7wH/yfo4qsQObaL4uCClscLjsnh2LXfuK1LUBZX13M9gFUeG', 'ADMIN');

INSERT INTO users (firstname, lastname, email, password, role)
VALUES ('User', 'Userov', 'user@mail.com', '{bcrypt}$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'USER');

INSERT INTO tasks (title, description, status, priority, author_id)
VALUES ('Разработать API', 'Создать REST API для системы задач', 'WAITING', 'HIGH', 1);

INSERT INTO tasks (title, description, status, priority, author_id)
VALUES ('Написать тесты', 'Покрыть код unit-тестами', 'WAITING', 'MEDIUM', 2);

INSERT INTO comments (text, author_id, task_id, created_at)
VALUES ('Нужно использовать Spring Boot 3', 1, 1, CURRENT_TIMESTAMP);

INSERT INTO comments (text, author_id, task_id, created_at)
VALUES ('Добавить валидацию DTO', 2, 1, CURRENT_TIMESTAMP);