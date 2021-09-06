package com.example.myfilms.controllers;

import com.example.myfilms.models.Film;
import com.example.myfilms.repository.FilmRepository;
import com.example.myfilms.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class FilmController {

    @Autowired
    FilmRepository filmRepository;

    @Autowired
    RatingRepository ratingRepository;

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getFilms(@RequestParam(required = false) String title) {
        try {
            List<Film> films = new ArrayList<Film>();

            if (title == null)
                filmRepository.findAll().forEach(films::add);
            else {
                filmRepository.findByTitleContaining(title).forEach(films::add);
            }

            return new ResponseEntity<>(films, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/film/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") String id) {
        Optional<Film> filmData = filmRepository.findById(Long.valueOf(id));

        if (filmData.isPresent()) {
            return new ResponseEntity<>(filmData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping("/film/{id}")
    public ResponseEntity<Film> deleteFilmById(@PathVariable("id") String id) {
        Optional<Film> filmData = filmRepository.findById(Long.valueOf(id));
        if (filmData.isPresent()) {
            try {
                Film film = filmData.get();
                filmRepository.delete(film);
                ratingRepository.deleteRatingsByFilmId(Long.valueOf(id));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/film")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        try {
            Film _film = filmRepository.save(new Film(film.getTitle(), film.getYear(), film.getDirector(), film.getDescription(), film.getPoster(), film.getTrailer()));
            return new ResponseEntity<>(_film, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/film")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        Optional<Film> filmData = filmRepository.findById(film.getId());
        if (filmData.isPresent()) {
            try {
                Film filmToUpdate = filmData.get();
                filmToUpdate.setTitle(film.getTitle());
                filmToUpdate.setYear(film.getYear());
                filmToUpdate.setDirector(film.getDirector());
                filmToUpdate.setDescription(film.getDescription());
                filmToUpdate.setPoster(film.getPoster());
                filmToUpdate.setTrailer(film.getTrailer());
                filmRepository.save(filmToUpdate);
                return new ResponseEntity<>(filmToUpdate, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
