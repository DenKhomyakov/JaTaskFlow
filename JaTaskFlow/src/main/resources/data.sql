CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    author_id INT,
    executor_id INT,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (executor_id) REFERENCES users(id)
    );

INSERT INTO users (firstname, lastname, email, password, role)
VALUES ('Admin', 'Adminov', 'admin@mail.com', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'ADMIN');

INSERT INTO users (firstname, lastname, email, password, role)
VALUES ('User', 'Userov', 'user@mail.com', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W', 'USER');

INSERT INTO tasks (title, description, status, priority, author_id)
VALUES ('Разработать API', 'Создать REST API для системы задач', 'WAITING', 'HIGH', 1);

INSERT INTO tasks (title, description, status, priority, author_id)
VALUES ('Написать тесты', 'Покрыть код unit-тестами', 'WAITING', 'MEDIUM', 2);