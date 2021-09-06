package com.example.myfilms.repository;

import com.example.myfilms.models.Rating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RatingRepository extends CrudRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndFilmId(Long user_id, Long film_id);

    @Query(value = "SELECT avg(rating) FROM Rating WHERE filmId = ?1 GROUP BY filmId")
    Float averageRatingByFilmId(Long filmId);

    Long countRatingByFilmId(Long filmId);

    Optional<Rating> getRatingByUserIdAndFilmId(Long userId, Long filmId);

    void deleteRatingsByFilmId(Long filmId);
}
