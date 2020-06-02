package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.repository.FonDetayRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/detay")
public class FonDetayController {
    @Autowired
    FonDetayRepository fonDetayRepository;
    @Autowired
    FonRepository fonRepository;


    @GetMapping("/{kod}")
    @CrossOrigin
    public FonDetay getDataByKod(@PathVariable String kod) {
        return fonDetayRepository.getByKod(kod);
    }

    @GetMapping("/alem")
    @CrossOrigin
    public Page<FonDetay> getDetayList() {
        return fonDetayRepository.findAll(PageRequest.of(0,20));
    }

    @GetMapping("/search")
    @CrossOrigin
    public List<FonDetay> getDetayListSearch(@RequestParam(value="s") String search) {
        return fonDetayRepository.findAllLike(search);
    }

    @GetMapping
    @CrossOrigin
    public List<Fon> getFonListSearch(@RequestParam(value="s") String search) {
        return fonRepository.findAllLike(search);
    }

    @GetMapping("/category")
    @CrossOrigin
    public List<FonDetay> getDetayListByCategory(@RequestParam(value="c") String search) {
        return fonDetayRepository.findByCategory(search);
    }


}
