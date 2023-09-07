package br.com.sw.api.domain;


import static br.com.sw.api.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

@ExtendWith(MockitoExtension.class)
class PlanetServicesTest {

    @InjectMocks
    private PlanetServices planetServices;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        Planet sut = planetServices.create(PLANET);

        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    void createPlanet_WithInValidData_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetServices.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);

    }

    @Test
    void findByIdPlanet_ByExistingId_ReturnsPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetServices.findById(1L);
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    void findByIdPlanet_ByUnexistingId_ReturnsEmpty() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Planet> sut = planetServices.findById(1L);

        assertThat(sut).isEmpty();
    }

    @Test
    void findByNamePlanet_ByExistingName_ReturnsPlanet() {
        when(planetRepository.findByName("Tatooine")).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetServices.findByName("Tatooine");
        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    void findByNamePlanet_ByUnexistingName_ReturnsEmpty() {
        final String name = "Unexisting Name";
        when(planetRepository.findByName(name)).thenReturn(Optional.empty());

        Optional<Planet> sut = planetServices.findByName(name);

        assertThat(sut).isEmpty();
    }

    @Test
    void listPlanets_ReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>() {
            {
                add(PLANET);
            }
        };
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetServices.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(PLANET);
    }

    @Test
    void listPlanets_ReturnsNoPlanets() {
        when(planetRepository.findAll((Example<Planet>) any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetServices.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();
    }

    @Test
    void removePlanet_WithExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> planetServices.deletePlanet(1L)).doesNotThrowAnyException();
    }

    @Test
    void removePlanet_WithUnexistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);

        assertThatThrownBy(() -> planetServices.deletePlanet(99L)).isInstanceOf(RuntimeException.class);
    }
}
