package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

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


