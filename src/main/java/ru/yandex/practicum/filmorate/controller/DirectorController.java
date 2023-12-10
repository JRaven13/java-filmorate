package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;


    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Поступил запрос на доваление режиссёра: {}", director);
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Поступил запрос на обновление режиссёра: {}", director);
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Поступил запрос на удаление режиссёра: {}", id);
        directorService.delete(id);
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение режиссёра: {}", id);
        return directorService.findById(id);
    }

    @GetMapping
    public List<Director> findAll() {
        log.info("Поступил запрос на получение всех режиссёров");
        return directorService.findAll();
    }


}
