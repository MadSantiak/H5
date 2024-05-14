package com.example.gowork.controller;

import com.example.gowork.model.Workperiod;
import com.example.gowork.repository.WorkperiodRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workperiod")
public class WorkperiodController {

    WorkperiodRepository repo;

    WorkperiodController(WorkperiodRepository workperiodRepository) {
        this.repo = workperiodRepository;
    }

    @GetMapping()
    List<Workperiod> getAllWorkperiods() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Workperiod read(@PathVariable int id) {
        return repo.findById(id).get();
    }

    @PostMapping()
    void create(@RequestBody Workperiod workperiod) {
        repo.save(workperiod);
    }

    @PutMapping()
    void update(@RequestBody Workperiod workperiod) {
        repo.save(workperiod);
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
