package br.com.sw.api.web;

import br.com.sw.api.domain.Planet;
import br.com.sw.api.domain.PlanetServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.sw.api.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PlanetController.class)
class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanetServices planetServices;

    @Test
    void createPlanet_WithValidData_ReturnsCreated() throws Exception {
        when(planetServices.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").content
                                (objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void createPlanet_WithInValidData_ReturnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(emptyPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());


        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(invalidPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
        when(planetServices.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc
                .perform
                        (post("/planets").content(objectMapper.writeValueAsString(PLANET))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

    @Test
    void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
        when(planetServices.findById(1L)).thenReturn(Optional.of(PLANET));
        mockMvc
                .perform
                        (get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {
        mockMvc
                .perform
                        (get("/planets/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
        when(planetServices.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        mockMvc
                .perform(
                        get("/planets/name/" + PLANET.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception {
        mockMvc
                .perform(
                        get("/planets/name/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void listPlanets_ReturnsFilteredPlanets() throws Exception {
        when(planetServices.list(null, null)).thenReturn(PLANETS);
        when(planetServices.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc
                .perform(
                        get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc
                .perform(
                        get("/planets?"
                                + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    void listPlanets_ReturnsNoPlanets() throws Exception {
        when(planetServices.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc
                .perform(
                        get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/planets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
        final Long planetId = 1L;

        doThrow(new EmptyResultDataAccessException(1)).when(planetServices).deletePlanet(planetId);

        mockMvc.perform(delete("/planets/" + planetId))
                .andExpect(status().isNotFound());
    }
}
