package br.com.sw.api.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlanetServices {

    private final PlanetRepository planetRepository;

    public List<Planet> list(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(climate, terrain));
        return planetRepository.findAll(query);
    }

    public Planet create(Planet planet) {
        return planetRepository.save(planet);
    }

    public Optional<Planet> findById(Long id) {
        return planetRepository.findById(id);
    }

    public Optional<Planet> findByName(String name) {
        return planetRepository.findByName(name);
    }

    public void deletePlanet(Long id) {
        planetRepository.deleteById(id);
    }
}
