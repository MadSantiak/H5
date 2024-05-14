package com.example.gowork.controller;

import com.example.gowork.model.Workplace;
import com.example.gowork.repository.WorkplaceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workplace")
public class WorkplaceController {

    WorkplaceRepository repo;

    WorkplaceController(WorkplaceRepository workplaceRepository) {
        this.repo = workplaceRepository;
    }

    @GetMapping()
    List<Workplace> getAllWorkplaces() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Workplace read(@PathVariable int id) {
        return repo.findById(id).get();
    }

    /**
     * Bit jank, but to enforce only 1 record can ever exist of "Workplace" objects,
     * we delete every (1) record whenever the database contains 1 (or more, techincally speaking)
     * records.
     * @param workplace
     */
    @PostMapping()
    void create(@RequestBody Workplace workplace) {
        if (repo.count() == 0) {
            repo.save(workplace);
        } else {
            // Delete any pre-existing record before saving the new one.
            repo.deleteAll();
            repo.save(workplace);
        }
    }

    @PutMapping()
    void update(@RequestBody Workplace workplace) {
        repo.save(workplace);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id) {
        repo.deleteById(id);
    }

    @DeleteMapping()
    void deleteAll() {
        repo.deleteAll();
    }
}
