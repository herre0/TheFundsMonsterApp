package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.model.LineChart;
import com.foncanavari.fonApp.model.PieChart;
import com.foncanavari.fonApp.repository.FonDetayRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import com.foncanavari.fonApp.servis.FonDetayServis;
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
import java.util.ArrayList;
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

    @GetMapping("/pie")
    @CrossOrigin
    public List<PieChart> getDataforPieChart(@RequestParam(value="pie") String kod) {
        FonDetay fondetay = fonDetayRepository.getByKod(kod);
        if(fondetay == null)
            return null;
        List<PieChart> pie_list = new ArrayList<PieChart>();
        if(!fondetay.getYab_hisse_senedi().equals("0.00")) pie_list.add(new PieChart("Yabancı Hisse Senedi",Double.valueOf(fondetay.getYab_hisse_senedi())));
        if(!fondetay.getHisse_senedi().equals("0.00")) pie_list.add(new PieChart("Hisse Senedi",Double.valueOf(fondetay.getHisse_senedi())));
        if(!fondetay.getDevlet_tahvili().equals("0.00")) pie_list.add(new PieChart("Devlet Tahvili",Double.valueOf(fondetay.getDevlet_tahvili())));
        if(!fondetay.getBanka_bonosu().equals("0.00")) pie_list.add(new PieChart("Banka Bonosu",Double.valueOf(fondetay.getBanka_bonosu())));
        if(!fondetay.getEurobond().equals("0.00")) pie_list.add(new PieChart("Eurobond",Double.valueOf(fondetay.getEurobond())));
        if(!fondetay.getKiymetli_maden().equals("0.00")) pie_list.add(new PieChart("Kıymetli Maden",Double.valueOf(fondetay.getKiymetli_maden())));
        if(!fondetay.getDiger().equals("0.00")) pie_list.add(new PieChart("Diğer",Double.valueOf(fondetay.getDiger())));
        if(!fondetay.getDoviz_odemeli_bono().equals("0.00")) pie_list.add(new PieChart("Döviz Ödemeli Bono",Double.valueOf(fondetay.getDoviz_odemeli_bono())));
        if(!fondetay.getDoviz_odemeli_tahvil().equals("0.00")) pie_list.add(new PieChart("Döviz Ödemeli Tahvil",Double.valueOf(fondetay.getDoviz_odemeli_tahvil())));
        if(!fondetay.getFinansman_bonosu().equals("0.00")) pie_list.add(new PieChart("Finansman Bonosu",Double.valueOf(fondetay.getFinansman_bonosu())));
        if(!fondetay.getFon_katilma_belgesi().equals("0.00")) pie_list.add(new PieChart("Fon Katılma Belgesi",Double.valueOf(fondetay.getFon_katilma_belgesi())));
        if(!fondetay.getGayrimenkul_sertifikasi().equals("0.00")) pie_list.add(new PieChart("Gayrimenkul Sertifikası",Double.valueOf(fondetay.getGayrimenkul_sertifikasi())));
        if(!fondetay.getHazine_bonosu().equals("0.00")) pie_list.add(new PieChart("Hazine Bonosu",Double.valueOf(fondetay.getHazine_bonosu())));
        if(!fondetay.getKamu_dis_borclanma_araci().equals("0.00")) pie_list.add(new PieChart("Kamu Dış Borçlanma Aracı",Double.valueOf(fondetay.getKamu_dis_borclanma_araci())));
        if(!fondetay.getKamu_kira_sertifikası().equals("0.00")) pie_list.add(new PieChart("Kamu Kira Sertifikası",Double.valueOf(fondetay.getKamu_kira_sertifikası())));
        if(!fondetay.getKatilim_hesabi().equals("0.00")) pie_list.add(new PieChart("Katılım Hesabı",Double.valueOf(fondetay.getKatilim_hesabi())));
        if(!fondetay.getOzel_kira_sertifikasi().equals("0.00")) pie_list.add(new PieChart("Özel Sektör Kira Sertifikası",Double.valueOf(fondetay.getOzel_kira_sertifikasi())));
        if(!fondetay.getOzel_sektor_tahvil().equals("0.00")) pie_list.add(new PieChart("Özel Sektör Tahvili",Double.valueOf(fondetay.getOzel_sektor_tahvil())));
        if(!fondetay.getTers_repo().equals("0.00")) pie_list.add(new PieChart("Ters Repo",Double.valueOf(fondetay.getTers_repo())));
        if(!fondetay.getTpp().equals("0.00")) pie_list.add(new PieChart("TPP",Double.valueOf(fondetay.getTpp())));
        if(!fondetay.getTurev_araci().equals("0.00")) pie_list.add(new PieChart("Türev Aracı",Double.valueOf(fondetay.getTurev_araci())));
        if(!fondetay.getVarlik_menkul_kiymet().equals("0.00")) pie_list.add(new PieChart("Varlığa Dayalı Menkul Kıymet",Double.valueOf(fondetay.getVarlik_menkul_kiymet())));
        if(!fondetay.getVadeli_mevduat().equals("0.00")) pie_list.add(new PieChart("Vadeli Mevduat",Double.valueOf(fondetay.getVadeli_mevduat())));
        if(!fondetay.getYab_borclanma_araci().equals("0.00")) pie_list.add(new PieChart("Yabancı Borçlanma Aracı",Double.valueOf(fondetay.getYab_borclanma_araci())));
        if(!fondetay.getYab_menkul_kiymet().equals("0.00")) pie_list.add(new PieChart("Yabanci Menkul Kıymet",Double.valueOf(fondetay.getYab_menkul_kiymet())));
        return pie_list;
    }

    @GetMapping("/line")
    @CrossOrigin
    public List<LineChart> getDataforLineChart(@RequestParam(value="kod") String kod,@RequestParam(value="date") String datetip) {
        return FonDetayServis.LineChartData(datetip,kod);
    }

}
