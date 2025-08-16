package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.*;

public class Film {
    private Integer id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Mpa getMpa() { return mpa; }
    public void setMpa(Mpa mpa) { this.mpa = mpa; }

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
