package com.example.myfilms.repository;

import com.example.myfilms.models.Film;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository  extends CrudRepository<Film, Long> {
    List<Film> findByTitleContaining(String title);
}
