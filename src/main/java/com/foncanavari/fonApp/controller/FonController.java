package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.model.Traffic;
import com.foncanavari.fonApp.repository.FonDetayRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import com.foncanavari.fonApp.repository.TrafficRepository;
import com.foncanavari.fonApp.servis.FonDetayServis;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fon")
public class FonController {

    @Autowired
    FonRepository fonRepository;
    @Autowired
    FonDetayRepository fonDetayRepository;
    @Autowired
    TrafficRepository trafficRepository;

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
        fon_updated.setCategory(fon.getCategory());
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


    @GetMapping("/eniyi")
    @CrossOrigin
    public List<?> GetEnIyiler(@RequestParam(value = "tip") String date_tip) {
        List<?> eniyilerList = new ArrayList<>();
        switch (date_tip) {
            case "day": eniyilerList = fonDetayRepository.getEniyilerFonGunluk();
                break;
            case "week": eniyilerList = fonDetayRepository.getEniyilerFonHaftalik();
                break;
            case "month": eniyilerList = fonDetayRepository.getEniyilerFonAylik();
                break;
            case "sixmonths": eniyilerList = fonDetayRepository.getEniyilerFonAltiAylik();
                break;
            case "year": eniyilerList = fonDetayRepository.getEniyilerFonYillik();
                break;
        }

        return eniyilerList;
    }

    @GetMapping("/yenile")
    @CrossOrigin
    public void IpKaydet(@RequestParam(value = "sip") String ip) {
        Traffic traffic = trafficRepository.findTrafficByIpAdress(ip);

        if(traffic == null) {
            traffic = new Traffic();
            traffic.setIp_adress(ip);
        }

        traffic.setGiris_sayisi(traffic.getGiris_sayisi() + 1);
        traffic.setTarih(FonDetayServis.tarihSaatHesapla());

        trafficRepository.save(traffic);
   }

}
