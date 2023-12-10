package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private DirectorStorage directorStorage;

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director).orElseThrow(() -> new NotFoundException("Режиссёер не найден"));
    }

    public void delete(Integer id) {
        directorStorage.delete(id);
    }

    public Director findById(Integer id) {
        return directorStorage.findById(id).orElseThrow(() -> new NotFoundException("Режиссёер не найден"));

    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }
}
