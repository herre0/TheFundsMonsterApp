package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.*;
import com.foncanavari.fonApp.repository.FonDetayRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import com.foncanavari.fonApp.repository.UserRepository;
import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/detay")
public class FonDetayController {
    @Autowired
    FonDetayRepository fonDetayRepository;
    @Autowired
    FonRepository fonRepository;
    @Autowired
    UserRepository userRepository;


    @GetMapping("/{kod}")
    @CrossOrigin
    public FonDetay getDataByKod(@PathVariable String kod) {
        return fonDetayRepository.getByKod(kod);
    }

    @GetMapping("/alem")
    @CrossOrigin
    public List<FonDetay> getDetayList() {
        return fonDetayRepository.getDetaylarForKarsilastir();
    }

    @GetMapping("/search")
    @CrossOrigin
    public List<FonDetay> getDetayListSearch(@RequestParam(value = "s") String search) {
        return fonDetayRepository.findAllLike(search);
    }

    @GetMapping
    @CrossOrigin
    public List<Fon> getFonListSearch(@RequestParam(value = "s") String search) {
        return fonRepository.findAllLike(search);
    }

    @GetMapping("/category")
    @CrossOrigin
    public List<FonDetay> getDetayListByCategory(@RequestParam(value = "c") String search) {
        return fonDetayRepository.findByCategory(search);
    }

    @GetMapping("/fav")
    @CrossOrigin
    public List<FonDetay> getFavoriFonDetayListByUser(@RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);
        List<Fon> favori_fonlar = user.getFavori_fonlar();
        List<FonDetay> favori_fondetaylar = new ArrayList<>();

        for (int i = 0; i < favori_fonlar.size(); i++) {
            favori_fondetaylar.add(fonDetayRepository.getByKod(favori_fonlar.get(i).getKodu()));
        }

        return favori_fondetaylar;
    }

    @GetMapping("/pie")
    @CrossOrigin
    public List<PieChart> getDataforPieChart(@RequestParam(value = "pie") String kod) {
        FonDetay fondetay = fonDetayRepository.getByKod(kod);
        if (fondetay == null)
            return null;

        return FonDetayServis.PieChartDetay(fondetay);
    }

    @GetMapping("/line")
    @CrossOrigin
    public List<LineChart> getDataforLineChart(@RequestParam(value = "kod") String kod, @RequestParam(value = "date") String datetip) {
        return FonDetayServis.LineChartData(datetip, kod);
    }

    @GetMapping("/sort")
    @CrossOrigin
    public List<FonDetay> getSortedData(@RequestParam(value = "col") String column, @RequestParam(value = "order") String order, @RequestParam(value = "cat") String category, @RequestParam(value = "us") String username) {
        List<FonDetay> sortedDetayList = new ArrayList<>();
        if (category.equals("")) {
            if (column.equals("2017") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2017TUM();
            else if (column.equals("2017") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2017TUM();
            else if (column.equals("2018") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2018TUM();
            else if (column.equals("2018") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2018TUM();
            else if (column.equals("2019") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2019TUM();
            else if (column.equals("2019") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2019TUM();
            else if (column.equals("2020") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2020TUM();
            else if (column.equals("2020") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2020TUM();
            else if (column.equals("son1ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofAylikArtisTUM();
            else if (column.equals("son1ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofAylikArtisTUM();
            else if (column.equals("sapma") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSapmaTUM();
            else if (column.equals("sapma") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSapmaTUM();
            else if (column.equals("sharpe") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSharpeTUM();
            else if (column.equals("sharpe") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSharpeTUM();
            else if (column.equals("son1hafta") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofHaftaTUM();
            else if (column.equals("son1hafta") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofHaftaTUM();
            else if (column.equals("son3ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3AyTUM();
            else if (column.equals("son3ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3AyTUM();
            else if (column.equals("son6ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof6AyTUM();
            else if (column.equals("son6ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof6AyTUM();
            else if (column.equals("yilbasi") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofYilbasiTUM();
            else if (column.equals("yilbasi") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofYilbasiTUM();
            else if (column.equals("son1yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof1YilTUM();
            else if (column.equals("son1yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof1YilTUM();
            else if (column.equals("son3yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3YilTUM();
            else if (column.equals("son3yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3YilTUM();
            else
                sortedDetayList = fonDetayRepository.getAscSortedListofAylikArtisTUM();
        } else if (category.equals("Favori Fonlarım")) {
            User user = userRepository.findUserByUsername(username);
            List<Fon> fonlar = user.getFavori_fonlar();
            List<String> fon_kodlar = new ArrayList<>();
            for (Fon fon : fonlar)
                fon_kodlar.add(fon.getKodu());

            if (column.equals("2017") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2017FAV(fon_kodlar);
            else if (column.equals("2017") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2017FAV(fon_kodlar);
            else if (column.equals("2018") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2018FAV(fon_kodlar);
            else if (column.equals("2018") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2018FAV(fon_kodlar);
            else if (column.equals("2019") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2019FAV(fon_kodlar);
            else if (column.equals("2019") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2019FAV(fon_kodlar);
            else if (column.equals("2020") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2020FAV(fon_kodlar);
            else if (column.equals("2020") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2020FAV(fon_kodlar);
            else if (column.equals("son1ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofAylikArtisFAV(fon_kodlar);
            else if (column.equals("son1ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofAylikArtisFAV(fon_kodlar);
            else if (column.equals("sapma") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSapmaFAV(fon_kodlar);
            else if (column.equals("sapma") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSapmaFAV(fon_kodlar);
            else if (column.equals("sharpe") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSharpeFAV(fon_kodlar);
            else if (column.equals("sharpe") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSharpeFAV(fon_kodlar);
            else if (column.equals("son1hafta") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofHaftaFAV(fon_kodlar);
            else if (column.equals("son1hafta") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofHaftaFAV(fon_kodlar);
            else if (column.equals("son3ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3AyFAV(fon_kodlar);
            else if (column.equals("son3ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3AyFAV(fon_kodlar);
            else if (column.equals("son6ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof6AyFAV(fon_kodlar);
            else if (column.equals("son6ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof6AyFAV(fon_kodlar);
            else if (column.equals("yilbasi") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofYilbasiFAV(fon_kodlar);
            else if (column.equals("yilbasi") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofYilbasiFAV(fon_kodlar);
            else if (column.equals("son1yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof1YilFAV(fon_kodlar);
            else if (column.equals("son1yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof1YilFAV(fon_kodlar);
            else if (column.equals("son3yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3YilFAV(fon_kodlar);
            else if (column.equals("son3yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3YilFAV(fon_kodlar);

        } else {
            if (column.equals("2017") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2017(category);
            else if (column.equals("2017") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2017(category);
            else if (column.equals("2018") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2018(category);
            else if (column.equals("2018") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2018(category);
            else if (column.equals("2019") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2019(category);
            else if (column.equals("2019") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2019(category);
            else if (column.equals("2020") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof2020(category);
            else if (column.equals("2020") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof2020(category);
            else if (column.equals("son1ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofAylikArtis(category);
            else if (column.equals("son1ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofAylikArtis(category);
            else if (column.equals("sapma") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSapma(category);
            else if (column.equals("sapma") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSapma(category);
            else if (column.equals("sharpe") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofSharpe(category);
            else if (column.equals("sharpe") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofSharpe(category);
            else if (column.equals("son1hafta") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofHafta(category);
            else if (column.equals("son1hafta") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofHafta(category);
            else if (column.equals("son3ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3Ay(category);
            else if (column.equals("son3ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3Ay(category);
            else if (column.equals("son6ay") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof6Ay(category);
            else if (column.equals("son6ay") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof6Ay(category);
            else if (column.equals("yilbasi") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListofYilbasi(category);
            else if (column.equals("yilbasi") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListofYilbasi(category);
            else if (column.equals("son1yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof1Yil(category);
            else if (column.equals("son1yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof1Yil(category);
            else if (column.equals("son3yil") && order.equals("desc"))
                sortedDetayList = fonDetayRepository.getDescSortedListof3Yil(category);
            else if (column.equals("son3yil") && order.equals("asc"))
                sortedDetayList = fonDetayRepository.getAscSortedListof3Yil(category);
        }


        return sortedDetayList;
    }


}
