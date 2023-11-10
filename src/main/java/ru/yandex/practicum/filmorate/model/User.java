package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
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
