DROP TABLE coffee IF EXISTS;

CREATE TABLE coffee  (
    coffee_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(20),
    origin VARCHAR(20),
    characteristics VARCHAR(30)
);
