package com.example.myfilms.models;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "Films")
@ToString
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    public String title;

    @Getter @Setter
    public int year;

    @Getter @Setter
    public String director;

    @Getter @Setter
    public String description;

    @Getter @Setter
    public String poster;

    @Getter @Setter
    public String trailer;


    public Film(String title, int year, String director, String description, String poster, String trailer) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.description = description;
        this.poster = poster;
        this.trailer = trailer;
    }

    public Film(Long id, String title, int year, String director, String description, String poster, String trailer) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.description = description;
        this.poster = poster;
        this.trailer = trailer;
    }

    public Film() {}

}
