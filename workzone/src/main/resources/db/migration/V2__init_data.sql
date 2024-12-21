INSERT INTO user (id, username, password, gender, age, email, phone_number, last_name, first_name, created_at, updated_at, status, is_enabled)
VALUES
    (1, 'admin', '$2a$12$JKqiYT8H2kSVB.4SAsL6MeUrML6ynKi7QGsnANJXdU6Hr6gUYs4MO', 'MALE', 30, 'admin@example.com', '1234567890', 'Admin', 'Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', TRUE),
    (2, 'moderator', '$$2a$12$JKqiYT8H2kSVB.4SAsL6MeUrML6ynKi7QGsnANJXdU6Hr6gUYs4MO', 'FEMALE', 28, 'moderator@example.com', '0987654321', 'Moderator', 'Moderator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', TRUE),
    (3, 'user', '$2a$12$JKqiYT8H2kSVB.4SAsL6MeUrML6ynKi7QGsnANJXdU6Hr6gUYs4MO', 'MALE', 25, 'user@example.com', '1122334455', 'User', 'User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', TRUE);

INSERT INTO role VALUES (1,'ROLE_ADMIN'),(2,'ROLE_MODERATOR'), (3,'ROLE_USER');

INSERT INTO user_roles VALUES (1,1),(2,2), (3,3);