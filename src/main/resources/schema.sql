CREATE TABLE IF NOT EXISTS mpa_rating (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL
);
CREATE TABLE IF NOT EXISTS genre (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL
);
CREATE TABLE IF NOT EXISTS film (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  release_date DATE,
  duration INT,
  mpa_rating_id INT,
  FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating(id)
);
CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL UNIQUE,
  login VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(50),
  birthday DATE
);
CREATE TABLE IF NOT EXISTS film_genre (
  film_id INT NOT NULL,
  genre_id INT NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES film(id),
  FOREIGN KEY (genre_id) REFERENCES genre(id)
);
CREATE TABLE IF NOT EXISTS likes (
  user_id INT NOT NULL,
  film_id INT NOT NULL,
  PRIMARY KEY (user_id, film_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (film_id) REFERENCES film(id)
);
CREATE TABLE IF NOT EXISTS friendship (
  user_id INT NOT NULL,
  friend_id INT NOT NULL,
  status VARCHAR(20) DEFAULT 'PENDING',
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (friend_id) REFERENCES users(id)
);