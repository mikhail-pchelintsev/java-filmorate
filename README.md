# java-filmorate
Template repository for Filmorate project.

## ER-диаграмма базы данных

![ER Diagram](db/filmorate-erd.png)

### Описание
Эта диаграмма показывает структуру базы данных проекта Filmorate.  
В ней описаны таблицы для фильмов, пользователей, жанров, рейтингов МРА, лайков и дружбы.  
Связи:
- `film` ↔ `genre` через `film_genre` (многие-ко-многим)
- лайки фильмов — таблица `likes` (`user_id`, `film_id`)
- дружба — таблица `friendship` (`user_id`, `friend_id`, `status`)
- рейтинг МРА — `mpa_rating`, связь с `film`

### Примеры SQL-запросов

#### Получить все фильмы
```sql
SELECT * FROM film;

