package com.example.myfilms.models;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "Users")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(unique=true)
    @Getter @Setter
    public String login;

    @Getter @Setter
    public String password;

    @Getter @Setter
    public String name;

    @Getter @Setter
    public String surname;

    @Getter @Setter
    public String role;


    public User(String login, String password, String name, String surname) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.role = "USER";
    }

    public User() {}

}
