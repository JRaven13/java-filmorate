package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NonNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @Positive
    private int duration;
    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();
}


