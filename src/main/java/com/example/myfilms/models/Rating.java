package com.example.myfilms.models;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "Ratings")
@ToString
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(name="user_id")
    @Getter @Setter
    public Long userId;

    @Column(name="film_id")
    @Getter @Setter
    public Long filmId;

    @Getter @Setter
    public Long rating;


    public Rating(Long user_id, Long film_id, Long rating) {
        this.userId = user_id;
        this.filmId = film_id;
        this.rating = rating;
    }

    public Rating() {}

}
