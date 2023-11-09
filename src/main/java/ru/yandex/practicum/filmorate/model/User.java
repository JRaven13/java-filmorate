package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    int id;
    @NotBlank
    @Email
    String email;
    @NotBlank
    String login;
    String Name;
    @Past
    LocalDate birthday;
}
