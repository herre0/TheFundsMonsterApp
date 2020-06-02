package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.Category;
import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.repository.CategoryRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cat")
public class CategoryController {
    @Autowired
    FonRepository fonRepository;


    @GetMapping
    @CrossOrigin
    public List getCatList() {
        List a = fonRepository.getArtislarGroupByCategory();

        return a;
    }


}
