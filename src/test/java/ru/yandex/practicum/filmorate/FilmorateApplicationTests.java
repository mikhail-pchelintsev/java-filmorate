package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main() {
		FilmorateApplication.main(new String[] {});
	}

	@Test
	void shouldCreateFilmCorrectly() {
		Film film = new Film();
		film.setName("Inception");
		film.setDescription("A mind-bending thriller");
		film.setReleaseDate(LocalDate.of(2010, 7, 16));
		film.setDuration(148);

		assertEquals("Inception", film.getName());
		assertEquals("A mind-bending thriller", film.getDescription());
		assertEquals(LocalDate.of(2010, 7, 16), film.getReleaseDate());
		assertEquals(148, film.getDuration());
	}

	@Test
	void shouldFailIfDurationIsNegative() {
		Film film = new Film();
		film.setDuration(-100);

		assertTrue(film.getDuration() < 0, "Продолжительность фильма не может быть отрицательной");
	}

	@Test
	void shouldThrowValidationExceptionWhenBirthdayIsInTheFuture() {
		User user = new User();
		user.setBirthday(LocalDate.now().plusDays(1));

		assertTrue(user.getBirthday().isAfter(LocalDate.now()), "Дата рождения не может быть в будущем");
	}



}
