package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.FonAppApplication;
import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.model.Life;
import com.foncanavari.fonApp.model.PortFon;
import com.foncanavari.fonApp.payload.response.MessageResponse;
import com.foncanavari.fonApp.repository.*;
import com.foncanavari.fonApp.servis.FonDetayServis;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
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
    UserRepository userRepository;
    @Autowired
    PortfonRepository portfonRepository;
    @Autowired
    LifeRepository lifeRepository;

    @GetMapping
    @CrossOrigin
    public Page<Fon> getFonList(@RequestParam(value = "p") int page) {
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
    public List<Fon> getFonListByCategory(@RequestParam(value = "c") String category) {
        return fonRepository.findFonlarByCategory(category);
    }

    private void fonKategoriUpdate() {
        List<Fon> fonlar = fonRepository.findAll();
        RestTemplate rest = new RestTemplate();
        String bugun, fonkod, url, json;
        Fon fon;
        for (int i = 0; i < fonlar.size(); i++) {
            bugun = "2020-05-22";
            fonkod = fonlar.get(i).getKodu();
            url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;
            json = rest.getForObject(url, String.class);
            if (json == null) {
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
            case "day":
                eniyilerList = fonDetayRepository.getEniyilerFonGunluk();
                break;
            case "week":
                eniyilerList = fonDetayRepository.getEniyilerFonHaftalik();
                break;
            case "month":
                eniyilerList = fonDetayRepository.getEniyilerFonAylik();
                break;
            case "sixmonths":
                eniyilerList = fonDetayRepository.getEniyilerFonAltiAylik();
                break;
            case "year":
                eniyilerList = fonDetayRepository.getEniyilerFonYillik();
                break;
        }

        return eniyilerList;
    }

    @GetMapping("/cat")
    @CrossOrigin
    public List getCatList() {
        List a = fonRepository.getArtislarGroupByCategory();

        return a;
    }

    @GetMapping("/iceriaktar")
    @CrossOrigin
    public void iceriAktarByClient() throws ParseException {
        FonAppApplication fn = new FonAppApplication();

        fn.iceriAktarForServisCallAdmin();
    }

    @GetMapping("/icerisharpeaktar")
    @CrossOrigin
    public void iceriSharpeAktarByClient() throws ParseException {
        FonAppApplication fn = new FonAppApplication();


        fn.iceriSharpeAktar();
    }

    @GetMapping("/fonkontrol")
    @CrossOrigin
    public void iceriAktarByClientForSingleFon(@RequestParam(value = "fon") String fon_kod) throws ParseException {
        if (fon_kod.contains("'") || fon_kod.contains("#") || fon_kod.contains("="))
            return;

//        FonAppApplication fn = new FonAppApplication();
//        FonDetay fndetay = fonDetayRepository.getByKod(fon_kod);
//
//        fndetay.setG_tarih(FonDetayServis.tarihHesapla("day", -10));
//        fonDetayRepository.save(fndetay);
//
//        fn.iceriAktarForServisCall(fon_kod);
    }

    @GetMapping("/countfon")
    @CrossOrigin
    public int getGuncellenenFonSayisi() {
        return fonDetayRepository.getGuncellenenFonSayisi(FonDetayServis.tarihHesapla("day", 0));
    }

    @GetMapping("/fonlist")
    @CrossOrigin
    public List<FonDetay> returnFonListNotUpdated() {
        return fonDetayRepository.getFonListNotUpdated(FonDetayServis.tarihHesapla("day", 0));
    }

    @PostMapping("/fonguncelle")
    @CrossOrigin
    public ResponseEntity<?> returnFonListNotUpdated(@RequestBody List<FonDetay> fonList) {
        for(FonDetay fn : fonList) {
            FonDetay fn_new = fonDetayRepository.getByKod(fn.getFon_kod());
            Fon fon = fonRepository.findByKodu(fn.getFon_kod());

            fn_new.setG_tarih(FonDetayServis.tarihHesapla("day", 0));
            fn_new.setGunluk_artis(fn.getGunluk_artis());
            fn_new.setBirim_deger(fn.getBirim_deger());

            fon.setGunluk_artis(fn.getGunluk_artis());
            fon.setFiyat(fn.getBirim_deger());

            fonRepository.save(fon);
            fonDetayRepository.save(fn_new);

            FonAppApplication fonApp = new FonAppApplication();
            fonApp.portfoyHesaplattir(fn.getFon_kod());
        }

        return ResponseEntity.ok().body(new MessageResponse("Well Done!"));
    }

    @GetMapping("/kullanicisayisi")
    @CrossOrigin
    public int getKullaniciSayisi() {
        return userRepository.getKullaniciSayisi();
    }

    @GetMapping("/portfoysayisi")
    @CrossOrigin
    public int getPortfoySayisi() {
        return userRepository.getPortfoySayisi();
    }

    @GetMapping("/girissayisi")
    @CrossOrigin
    public int getGirisSayisi() {
        return userRepository.getgirisSayisi(FonDetayServis.tarihHesaplaTR());
    }

    @GetMapping("/sharpesayisi")
    @CrossOrigin
    public int returnSharpeListNotUpdated() {
        return fonDetayRepository.getCountSharpeListNotUpdated(FonDetayServis.tarihHesapla("day", 0));
    }

    @GetMapping("/nullsayisi")
    @CrossOrigin
    public int returnNullPortfonListSize() {
        return portfonRepository.getNullListNotUpdated().size();
    }

    @GetMapping("/nullparcala")
    @CrossOrigin
    public ResponseEntity<?> deleteListofNullPortfons() {
        List<PortFon> portFonList = portfonRepository.getNullListNotUpdated();

        for(PortFon p : portFonList) {
            portfonRepository.deletePortfonbyFonId(p.getId());
            portfonRepository.deleteById(p.getId());
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Tamamlandı!"));
    }

    @GetMapping("/kullaniciarttir")
    @CrossOrigin
    public void kullaniciArttir() {
        Life life = lifeRepository.getLifeByDate(FonDetayServis.tarihHesapla("day",0));

        if(life == null) {
            life = new Life();
            life.setTarih(FonDetayServis.tarihHesapla("day",0));
            life.setGiris_sayisi(0);
        }

        life.setGiris_sayisi(life.getGiris_sayisi() + 1);
        lifeRepository.save(life);
    }

    @GetMapping("/kullanicidondur")
    @CrossOrigin
    public int kullaniciDondur() {
        return lifeRepository.getSayiByDate(FonDetayServis.tarihHesapla("day",0));
    }

}
