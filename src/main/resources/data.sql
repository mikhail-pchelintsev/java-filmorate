MERGE INTO mpa_rating
KEY (id)
VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genre
KEY (id)
VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO film
KEY (id)
VALUES
  ('1', 'mona', 'trash', '2023-01-01', 120, 1),
  ('2', 'витя', 'чёрт ебаный', '2023-01-02', 130, 2),
  ('3', 'mona3', 'trash3', '2023-01-03', 140, 1);
MERGE INTO users
KEY (id)
VALUES
  ('1', 'Иван Иванов', 'ivan@mail.ru', 'ivan', '1990-01-01'),
  ('2', 'Петр Петров', 'petr@mail.ru', 'petr', '1985-02-02');
MERGE INTO film_genre
KEY (film_id, genre_id)
VALUES
  (1, 1),
  (1, 2),
  (2, 1);