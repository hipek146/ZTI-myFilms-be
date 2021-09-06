package com.example.myfilms;

import com.example.myfilms.controllers.FilmController;
import com.example.myfilms.models.Film;
import com.example.myfilms.repository.FilmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyFilmsApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    FilmRepository filmRepository;

    Film RECORD_1 = new Film(1L, "Skazani na Shawshank", 1994, "Frank Darabont", "Shawshank description", "url=Shawshank", "yt=Shawshank");
    Film RECORD_2 = new Film(2L, "Zielona mila", 1994, "Frank Darabont", "Mila description", "url=Mila", "yt=Mila");
    Film RECORD_3 = new Film(3L, "Mamma Mia!", 2008, "Phyllida Lloyd", "Mamma description", "url=Mamma", "yt=Mamma");

    @Test
    void contextLoads() {
    }

    @Test
    public void getFilms() throws Exception {
        List<Film> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));

        String titleToFind = "Shawshank";

        Mockito.when(filmRepository.findAll()).thenReturn(records);
        Mockito.when(filmRepository.findByTitleContaining(titleToFind)).thenReturn(Collections.singletonList(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/films")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].title", is("Mamma Mia!")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/films")
                .param("title", titleToFind)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Skazani na Shawshank")));
    }

    @Test
    public void getFilmById() throws Exception {
        Mockito.when(filmRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(RECORD_2));
        Mockito.when(filmRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(RECORD_3));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/film/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Zielona mila")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/film/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is("Mamma description")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/film/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createFilm() throws Exception {
        String admin_token = JwtFilter.createToken("test_admin", "ADMIN");
        String user_token = JwtFilter.createToken("test_user", "USER");
        assertNotNull(admin_token);
        assertNotNull(user_token);

        JSONObject request = new JSONObject();
        request.put("title", RECORD_1.getTitle());
        request.put("year", RECORD_1.getYear());
        request.put("director", RECORD_1.getDirector());
        request.put("description", RECORD_1.getDescription());
        request.put("poster", RECORD_1.getPoster());
        request.put("trailer", RECORD_1.getTrailer());

        Mockito.when(filmRepository.save(Mockito.any(Film.class))).thenReturn(RECORD_1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/film")
                .header("Authorization", "Bearer " + admin_token)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("title", is("Skazani na Shawshank")));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/film")
                .header("Authorization", "Bearer " + user_token)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateFilm() throws Exception {
        String admin_token = JwtFilter.createToken("test_admin", "ADMIN");
        String user_token = JwtFilter.createToken("test_user", "USER");
        assertNotNull(admin_token);
        assertNotNull(user_token);

        JSONObject request = new JSONObject();
        request.put("id", RECORD_3.getId());
        request.put("title", RECORD_3.getTitle());
        request.put("year", RECORD_3.getYear());
        request.put("director", RECORD_3.getDirector());
        request.put("description", RECORD_3.getDescription());
        request.put("poster", RECORD_3.getPoster());
        request.put("trailer", RECORD_3.getTrailer());
        JSONObject request_notFound = new JSONObject(request.toString());
        request_notFound.put("id", RECORD_3.getId() + 1);

        Mockito.when(filmRepository.save(Mockito.any(Film.class))).thenReturn(RECORD_3);
        Mockito.when(filmRepository.findById(RECORD_3.getId())).thenReturn(java.util.Optional.ofNullable(RECORD_3));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/film")
                .header("Authorization", "Bearer " + admin_token)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is("Mamma Mia!")));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/film")
                .header("Authorization", "Bearer " + user_token)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/film")
                .header("Authorization", "Bearer " + admin_token)
                .content(request_notFound.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFilm() throws Exception {
        String admin_token = JwtFilter.createToken("test_admin", "ADMIN");
        String user_token = JwtFilter.createToken("test_user", "USER");
        assertNotNull(admin_token);
        assertNotNull(user_token);

        Long id = RECORD_2.getId();
        Mockito.when(filmRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(RECORD_2));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/film/" + id)
                .header("Authorization", "Bearer " + admin_token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/film/" + id)
                .header("Authorization", "Bearer " + user_token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/film/" + id + 1)
                .header("Authorization", "Bearer " + admin_token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
