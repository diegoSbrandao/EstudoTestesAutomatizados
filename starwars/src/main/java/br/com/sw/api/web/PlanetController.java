package br.com.sw.api.web;

import br.com.sw.api.domain.Planet;
import br.com.sw.api.domain.PlanetServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/planets")
@RestController
public class PlanetController {

    private final PlanetServices planetServices;


    @GetMapping("")
    public ResponseEntity<List<Planet>> findAll(@RequestParam(required = false) String terrain, @RequestParam(required = false) String climate) {
        List<Planet> planets = planetServices.list(terrain, climate);
        return ResponseEntity.ok(planets);
    }

    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
        return new ResponseEntity<>(planetServices.create(planet), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> findByID(@PathVariable Long id) {
        return planetServices.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> findByName(@PathVariable String name) {
        return planetServices.findByName(name).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") Long id) {
        planetServices.deletePlanet(id);
        return ResponseEntity.noContent().build();

    }

}
