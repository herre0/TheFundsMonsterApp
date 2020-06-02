package com.foncanavari.fonApp.controller;


import com.foncanavari.fonApp.model.Settings;
import com.foncanavari.fonApp.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/settings")
public class SettingsController {
    @Autowired
    SettingsRepository settingsRepository;

    @GetMapping
    public List<Settings> getSettings() {
        return settingsRepository.findAll();
    }

    @PutMapping()
    public Settings updateSettings(@RequestBody Settings settings) {
        Settings settings_u = settingsRepository.getOne(1);

        settings_u.setAciklama(settings.getAciklama());
        settings_u.setAnahtar_kelime(settings.getAnahtar_kelime());
        settings_u.setEmail(settings.getEmail());
        settings_u.setFooter_text(settings.getFooter_text());
        settings_u.setTitle(settings.getTitle());
        settings_u.setYayin_tarihi(settings.getYayin_tarihi());

        return settingsRepository.save(settings_u);
    }
}
