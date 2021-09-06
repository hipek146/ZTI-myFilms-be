package com.example.myfilms.controllers;

import com.example.myfilms.models.Rating;
import com.example.myfilms.models.RatingDto;
import com.example.myfilms.models.User;
import com.example.myfilms.repository.RatingRepository;
import com.example.myfilms.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
public class RatingController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

    @PostMapping("/rate")
    public ResponseEntity<String> rate(@RequestBody RatingDto rating) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userData = userRepository.findByLogin(auth.getPrincipal().toString());
        if (userData.isPresent()) {
            User user = userData.get();
            try {
                Optional<Rating> ratingData = ratingRepository.findByUserIdAndFilmId(user.getId(), rating.getFilmId());
                if (ratingData.isPresent()) {
                    Rating ratingToUpdate = ratingData.get();
                    ratingToUpdate.setRating(rating.getRating());
                    ratingRepository.save(ratingToUpdate);
                } else {
                    ratingRepository.save(new Rating(user.getId(), rating.getFilmId(), rating.getRating()));
                }
                Float ratingResponse = ratingRepository.averageRatingByFilmId(rating.getFilmId());
                Long countResponse = ratingRepository.countRatingByFilmId(rating.getFilmId());

                JSONObject response = new JSONObject();
                response.put("rating", ratingResponse);
                response.put("count", countResponse);
                return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<String> getRatingByFilmId(@PathVariable("id") String id) {
        try {
            Float rating = ratingRepository.averageRatingByFilmId(Long.valueOf(id));
            Long count = ratingRepository.countRatingByFilmId(Long.valueOf(id));

            if (rating == null) {
                rating = 0f;
            }
            JSONObject response = new JSONObject();
            response.put("rating", rating);
            response.put("count", count);
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-rating/{id}")
    public ResponseEntity<String> getUserRatingByFilmId(@PathVariable("id") String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userData = userRepository.findByLogin(auth.getPrincipal().toString());
        if (userData.isPresent()) {
            try {
                User user = userData.get();
                Optional<Rating> ratingData = ratingRepository.getRatingByUserIdAndFilmId(user.getId(), Long.valueOf(id));
                if (ratingData.isPresent()) {
                    Rating rating = ratingData.get();
                    JSONObject response = new JSONObject();
                    response.put("rating", rating.rating);
                    return new ResponseEntity<>(response.toString(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/rating/{id}")
    public ResponseEntity<String> deleteRatingByFilmId(@PathVariable("id") String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userData = userRepository.findByLogin(auth.getPrincipal().toString());
        if (userData.isPresent()) {
            try {
                User user = userData.get();
                Optional<Rating> ratingData = ratingRepository.getRatingByUserIdAndFilmId(user.getId(), Long.valueOf(id));
                if (ratingData.isPresent()) {
                    Rating rating = ratingData.get();
                    ratingRepository.delete(rating);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
