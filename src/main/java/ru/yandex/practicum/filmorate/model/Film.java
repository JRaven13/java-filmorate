package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NonNull
    @NotBlank
    String name;
    @Size(min = 1, max = 200)
    String description;
    @NonNull
    LocalDate releaseDate;
    @Positive
    int duration;

}


