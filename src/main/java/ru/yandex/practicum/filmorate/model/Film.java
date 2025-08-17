package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private int likesCount = 0;

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) {
        Map<Integer, Genre> seen = new LinkedHashMap<>();
        if (genres != null) {
            for (Genre g : genres) {
                if (g != null && g.getId() != null && !seen.containsKey(g.getId())) {
                    seen.put(g.getId(), g);
                }
            }
        }
        this.genres = new ArrayList<>(seen.values());
    }
}
