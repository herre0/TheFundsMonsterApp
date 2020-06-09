package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.repository.FonRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/fon")
public class FonController {

    @Autowired
    FonRepository fonRepository;

    @GetMapping
    @CrossOrigin
    public Page<Fon> getFonList(@RequestParam(value="p") int page) {
        return fonRepository.findAllPage(PageRequest.of(page, 15, Sort.by("gunluk_artis").descending()));
    }

    @GetMapping("/{kod}")
    @CrossOrigin
    public Fon getFonByKod(@PathVariable String kod) {
        return fonRepository.findByKodu(kod);
    }

    @PostMapping(value = "") // admin
    public Boolean createFon(@RequestBody Fon[] fonlar) {

        if (fonRepository.count() < 10) {
            for (Fon item : fonlar)
                fonRepository.save(item);
            return true;
        } else
            return false;

    }

    @PutMapping("/{kod}") //admin
    public Boolean updateFon(@PathVariable String kod, @RequestBody Fon fon) {
        Fon fon_updated = fonRepository.findByKodu(kod);

        fon_updated.setAdi(fon.getAdi());
        fon_updated.setKodu(fon.getKodu());
        fon_updated.setAgirlik(fon.getAgirlik());
        fon_updated.setCategory(fon.getCategory());
        fon_updated.setFavori(fon.getFavori());
        fonRepository.save(fon_updated);

        return true;
    }

    @DeleteMapping("/{id}")
    public Boolean deleteFon(@PathVariable int id) {

        //fonRepository.deleteById(id);
        return true;
    }

    @GetMapping("/category")
    @CrossOrigin
    public List<Fon> getFonListByCategory(@RequestParam(value="c") String category) {
        return fonRepository.findFonlarByCategory(category);
    }

    private void fonKategoriUpdate() {
        List<Fon> fonlar = fonRepository.findAll();
        RestTemplate rest = new RestTemplate();
        String bugun, fonkod, url, json;
        Fon fon;
        for (int i = 0; i < fonlar.size();i++) {
            bugun = "2020-05-22";
            fonkod = fonlar.get(i).getKodu();
            url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;
            json = rest.getForObject(url, String.class);
            if(json == null) {
                continue;
            }
            json = json.replace("[", "");
            json = json.replace("]", "");
            fon = fonRepository.findByKodu(fonlar.get(i).getKodu());
            fon.setCategory(new JsonParser().parse(json).getAsJsonObject().get("FonTuru").getAsString());
           // fonRepository.save(fon);
        }
    }

}
