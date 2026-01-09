package com.royalhalalmeats.royalmeats.controller;



import com.royalhalalmeats.royalmeats.model.MeatItem;
import com.royalhalalmeats.royalmeats.repository.MeatItemRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meats")
@CrossOrigin(origins = "*")
public class MeatItemController {
    private final MeatItemRepository repo;

    public MeatItemController(MeatItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<MeatItem> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public MeatItem add(@RequestBody MeatItem item) {
        return repo.save(item);
    }
}

