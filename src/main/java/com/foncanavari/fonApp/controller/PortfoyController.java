package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.*;
import com.foncanavari.fonApp.payload.response.MessageResponse;
import com.foncanavari.fonApp.repository.*;
import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/portfoy")
public class PortfoyController {
    @Autowired
    PortfoyRepository portfoyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PortfonRepository portfonRepository;
    @Autowired
    FonRepository fonRepository;
    @Autowired
    FonDetayRepository fondetayRepository;

    @PostMapping
    @CrossOrigin
    public ResponseEntity<?> PortfoyOlustur(@Valid @RequestBody Portfoy portfoy, @RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);
       // if(user.getPortfoy_sayisi() > 2)
         //   return ResponseEntity.badRequest().body(new MessageResponse("Portföylerin sayısı limiti aştı!"));
        for(Portfoy item : user.getPortfoyler()){
            if(item.getAdi().equals(portfoy.getAdi()))
                return ResponseEntity.badRequest().body(new MessageResponse("Aynı isimle portföy mevcut!"));
        }
        if(portfoy.getAdi().length() > 30)
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy ismi 40 karakterden fazla olamaz!"));

        List<Portfoy> portfoyler = user.getPortfoyler();
        List<PortFon> portfonlar = portfoy.getFonlar();
        Fon fon = new Fon();
        Double portfoy_degeri = 0D;
        for (PortFon p : portfonlar) {
            fon = fonRepository.findByKodu(p.getFon_kod());
            portfoy_degeri += (Double.valueOf(fon.getFiyat()) * p.getAdet());
        }
        portfoy.setPortfoy_degeri(portfoy_degeri);

        for (PortFon p : portfonlar) {
            fon = fonRepository.findByKodu(p.getFon_kod());
            p.setGunluk_getiri_yuzde(Double.valueOf(fon.getGunluk_artis()));
            p.setAgirlik(((p.getAdet() * Double.valueOf(fon.getFiyat())) / portfoy.getPortfoy_degeri()) * 100);
            p.setDegeri(Double.valueOf(fon.getFiyat()) * p.getAdet());
            p.setBirim_fiyati(Double.valueOf(fon.getFiyat()));
            p.setGunluk_getiri_tl(p.getDegeri() - ((((100 - Double.valueOf(fon.getGunluk_artis())) * Double.valueOf(fon.getFiyat())) / 100) * p.getAdet()));
            p.setToplam_getiri_tl(p.getDegeri() - (p.getAdet() * p.getAlis_maliyeti()));
            p.setToplam_getiri_yuzde((Double.valueOf(p.getToplam_getiri_tl()) / p.getDegeri()) * 100);
        }
        portfoy.setFonlar(portfonlar);
        portfoy.setUser_id(user.getId());
        portfoy.setY_tarih(FonDetayServis.tarihHesapla("day", 0));

        Double gunluk_tl = 0D, gunluk_yuzde = 0D, toplam_tl = 0D, toplam_yuzde = 0D, deger = 0D;
        for (PortFon p : portfonlar) {
            gunluk_tl += p.getGunluk_getiri_tl();
            gunluk_yuzde += (p.getGunluk_getiri_yuzde() * p.getAgirlik());
            toplam_tl += p.getToplam_getiri_tl();
            toplam_yuzde += p.getToplam_getiri_yuzde() * p.getAgirlik();
            deger += p.getDegeri();
        }
        portfoy.setGunluk_getiri_tl(gunluk_tl);
        portfoy.setGunluk_getiri_yuzde(gunluk_yuzde / 100);
        portfoy.setToplam_getiri_tl(toplam_tl);
        portfoy.setToplam_getiri_yuzde(toplam_yuzde / 100);
        portfoy.setPortfoy_degeri(deger);


        portfoyler.add(portfoy);

        user.setPortfoyler(portfoyler);
        user.setPortfoy_sayisi(user.getPortfoy_sayisi() + 1);

        portfonRepository.saveAll(portfoy.getFonlar());
        portfoyRepository.save(portfoy);
        userRepository.save(user);

        return ResponseEntity.ok(portfoyler);
    }

    @PutMapping
    @CrossOrigin
    public List<Portfoy> PortfoyDuzenle(@Valid @RequestBody Portfoy portfoy, @RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy_eski = portfoyRepository.findPortfoyByUseridandPname(user.getId(), portfoy.getAdi());

        portfoy_eski.setAdi(portfoy.getAdi());
        portfoy_eski.setFonlar(portfoy.getFonlar());
        portfoy_eski.setG_tarih(FonDetayServis.tarihHesapla("day", 0));
        portfoy_eski.setPortfoy_degeri(portfoy.getPortfoy_degeri());

        portfoyRepository.save(portfoy_eski);
        // user = userRepository.findUserByUsername(username);
        return user.getPortfoyler();
    }

    @CrossOrigin
    @GetMapping("/portfoy")
    public List<PieChart> getPortfoyDagilimiForPieChart(@RequestParam(value = "pie") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);

        List<PortFon> portfonlar = portfoy.getFonlar();
        List<PieChart> pieList = new ArrayList<>();

        for (PortFon p : portfonlar) {
            pieList.add(new PieChart(p.getFon_kod(), p.getAgirlik()));
        }

        return pieList;
    }

    @CrossOrigin
    @GetMapping("/kategori")
    public List<PieChart> getKategoriDagilimiForPieChart(@RequestParam(value = "pie") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);

        List<PortFon> portfonlar = portfoy.getFonlar();
        List<PieChart> pieList = new ArrayList<>();
        Fon fon = new Fon();
        Double hisse=0D, borclanma=0D, degisken=0D, katilim=0D, altin=0D, sepet=0D, para_piyasasi=0D;
        for (PortFon p : portfonlar) {
            fon = fonRepository.findByKodu(p.getFon_kod());
            if(fon.getCategory().equals("Hisse Senedi Fonları")) hisse += p.getAgirlik();
            if(fon.getCategory().equals("Borçlanma Araçları Fonları")) borclanma += p.getAgirlik();
            if(fon.getCategory().equals("Katılım Fonları")) katilim += p.getAgirlik();
            if(fon.getCategory().equals("Kıymetli Madenler Fonları")) altin += p.getAgirlik();
            if(fon.getCategory().equals("Fon Sepeti Fonları")) sepet += p.getAgirlik();
            if(fon.getCategory().equals("Karma ve Değişken Fonlar")) degisken += p.getAgirlik();
            if(fon.getCategory().equals("Para Piyasası Fonları")) para_piyasasi += p.getAgirlik();
        }

        if(hisse != 0D) pieList.add(new PieChart("Hisse Senedi Fonları", hisse));
        if(degisken != 0D) pieList.add(new PieChart("Karma ve Değişken Fonlar", degisken));
        if(borclanma != 0D) pieList.add(new PieChart("Borçlanma Araçları Fonları", borclanma));
        if(katilim != 0D) pieList.add(new PieChart("Katılım Fonları", katilim));
        if(altin != 0D) pieList.add(new PieChart("Kıymetli Madenler Fonları", altin));
        if(sepet != 0D) pieList.add(new PieChart("Fon Sepeti Fonları", sepet));
        if(para_piyasasi != 0D) pieList.add(new PieChart("Para Piyasası Fonları", para_piyasasi));

        return pieList;
    }

    @CrossOrigin
    @DeleteMapping
    public List<Portfoy> deletePortfoyByUsername(@RequestParam(value = "un") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);

        portfonRepository.deleteportfoyfonByPortfoyId(portfoy.getId());
        portfonRepository.deleteuserportfoyByPortfoyId(portfoy.getId());
        portfonRepository.deleteInBatch(portfoy.getFonlar());
        portfoyRepository.deleteById(portfoy.getId());
        user = userRepository.findUserByUsername(username);
        return user.getPortfoyler();
    }

    @CrossOrigin
    @GetMapping("/varlik")
    public List<PieChart> getVarlikDagilimiForPieChart(@RequestParam(value = "pie") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);
        List<PortFon> portfonlar = portfoy.getFonlar();
        List<PieChart> pie_list = new ArrayList<>();
        FonDetay fondetay = new FonDetay();
        Double banka_bonosu = 0D;
        Double diger = 0D;
        Double devlet_tahvili = 0D;
        Double doviz_odemeli_bono = 0D;
        Double eurobond = 0D;
        Double doviz_odemeli_tahvil = 0D;
        Double finansman_bonosu = 0D;
        Double fon_katilma_belgesi = 0D;
        Double gayrimenkul_sertifikasi = 0D;
        Double hazine_bonosu = 0D;
        Double hisse_senedi = 0D;
        Double kamu_dis_borclanma_araci = 0D;
        Double kamu_kira_sertifikası = 0D;
        Double katilim_hesabi = 0D;
        Double kiymetli_maden = 0D;
        Double ozel_kira_sertifikasi = 0D;
        Double ozel_sektor_tahvil = 0D;
        Double ters_repo = 0D;
        Double tpp = 0D;
        Double turev_araci = 0D;
        Double varlik_menkul_kiymet = 0D;
        Double vadeli_mevduat = 0D;
        Double yab_borclanma_araci = 0D;
        Double yab_hisse_senedi = 0D;
        Double yab_menkul_kiymet = 0D;
        for (PortFon p : portfonlar) {
            fondetay = fondetayRepository.getByKod(p.getFon_kod());
            banka_bonosu += (Double.valueOf(fondetay.getBanka_bonosu()) * (p.getAgirlik())) / 100;
            diger += (Double.valueOf(fondetay.getDiger()) * (p.getAgirlik())) / 100;
            devlet_tahvili += (Double.valueOf(fondetay.getDevlet_tahvili()) * (p.getAgirlik())) / 100;
            doviz_odemeli_bono += (Double.valueOf(fondetay.getDoviz_odemeli_bono()) * (p.getAgirlik())) / 100;
            eurobond += (Double.valueOf(fondetay.getEurobond()) * (p.getAgirlik())) / 100;
            doviz_odemeli_tahvil += (Double.valueOf(fondetay.getDoviz_odemeli_tahvil()) * (p.getAgirlik())) / 100;
            finansman_bonosu += (Double.valueOf(fondetay.getFinansman_bonosu()) * (p.getAgirlik())) / 100;
            fon_katilma_belgesi += (Double.valueOf(fondetay.getFon_katilma_belgesi()) * (p.getAgirlik())) / 100;
            gayrimenkul_sertifikasi += (Double.valueOf(fondetay.getGayrimenkul_sertifikasi()) * (p.getAgirlik())) / 100;
            hazine_bonosu += (Double.valueOf(fondetay.getHazine_bonosu()) * (p.getAgirlik())) / 100;
            hisse_senedi += (Double.valueOf(fondetay.getHisse_senedi()) * (p.getAgirlik())) / 100;
            kamu_dis_borclanma_araci += (Double.valueOf(fondetay.getKamu_dis_borclanma_araci()) * (p.getAgirlik())) / 100;
            kamu_kira_sertifikası += (Double.valueOf(fondetay.getKamu_kira_sertifikası()) * (p.getAgirlik())) / 100;
            katilim_hesabi += (Double.valueOf(fondetay.getKatilim_hesabi()) * (p.getAgirlik())) / 100;
            kiymetli_maden += (Double.valueOf(fondetay.getKiymetli_maden()) * (p.getAgirlik())) / 100;
            ozel_kira_sertifikasi += (Double.valueOf(fondetay.getOzel_kira_sertifikasi()) * (p.getAgirlik())) / 100;
            ozel_sektor_tahvil += (Double.valueOf(fondetay.getOzel_sektor_tahvil()) * (p.getAgirlik())) / 100;
            ters_repo += (Double.valueOf(fondetay.getTers_repo()) * (p.getAgirlik())) / 100;
            tpp += (Double.valueOf(fondetay.getTpp()) * (p.getAgirlik())) / 100;
            turev_araci += (Double.valueOf(fondetay.getTurev_araci()) * (p.getAgirlik())) / 100;
            varlik_menkul_kiymet += (Double.valueOf(fondetay.getVarlik_menkul_kiymet()) * (p.getAgirlik())) / 100;
            vadeli_mevduat += (Double.valueOf(fondetay.getVadeli_mevduat()) * (p.getAgirlik())) / 100;
            yab_borclanma_araci += (Double.valueOf(fondetay.getYab_borclanma_araci()) * (p.getAgirlik())) / 100;
            yab_hisse_senedi += (Double.valueOf(fondetay.getYab_hisse_senedi()) * (p.getAgirlik())) / 100;
            yab_menkul_kiymet += (Double.valueOf(fondetay.getYab_menkul_kiymet()) * (p.getAgirlik())) / 100;
        }
        if (yab_hisse_senedi != 0D && yab_hisse_senedi > 0.099) pie_list.add(new PieChart("Yabancı Hisse Senedi", yab_hisse_senedi));
        if (hisse_senedi != 0D && hisse_senedi > 0.099) pie_list.add(new PieChart("Hisse Senedi", hisse_senedi));
        if (devlet_tahvili != 0D && devlet_tahvili > 0.099) pie_list.add(new PieChart("Devlet Tahvili", devlet_tahvili));
        if (banka_bonosu != 0D && banka_bonosu > 0.099) pie_list.add(new PieChart("Banka Bonosu", banka_bonosu));
        if (eurobond != 0D && eurobond > 0.099) pie_list.add(new PieChart("Eurobond", eurobond));
        if (kiymetli_maden != 0D && kiymetli_maden > 0.099) pie_list.add(new PieChart("Kıymetli Maden", kiymetli_maden));
        if (diger != 0D && diger > 0.099) pie_list.add(new PieChart("Diğer", diger));
        if (doviz_odemeli_bono != 0D && doviz_odemeli_bono > 0.099) pie_list.add(new PieChart("Döviz Ödemeli Bono", doviz_odemeli_bono));
        if (doviz_odemeli_tahvil != 0D && doviz_odemeli_tahvil > 0.099) pie_list.add(new PieChart("Döviz Ödemeli Tahvil", doviz_odemeli_tahvil));
        if (finansman_bonosu != 0D && finansman_bonosu > 0.099) pie_list.add(new PieChart("Finansman Bonosu", finansman_bonosu));
        if (fon_katilma_belgesi != 0D && fon_katilma_belgesi > 0.099) pie_list.add(new PieChart("Fon Katılma Belgesi", fon_katilma_belgesi));
        if (gayrimenkul_sertifikasi != 0D && gayrimenkul_sertifikasi > 0.099) pie_list.add(new PieChart("Gayrimenkul Sertifikası", gayrimenkul_sertifikasi));
        if (hazine_bonosu != 0D && hazine_bonosu > 0.099) pie_list.add(new PieChart("Hazine Bonosu", hazine_bonosu));
        if (kamu_dis_borclanma_araci != 0D && kamu_dis_borclanma_araci > 0.099) pie_list.add(new PieChart("Kamu Dış Borçlanma Aracı", kamu_dis_borclanma_araci));
        if (varlik_menkul_kiymet != 0D && varlik_menkul_kiymet > 0.099) pie_list.add(new PieChart("Varlığa Dayalı Menkul Kıymet", varlik_menkul_kiymet));
        if (kamu_kira_sertifikası != 0D && kamu_kira_sertifikası > 0.099) pie_list.add(new PieChart("Kamu Kira Sertifikası", kamu_kira_sertifikası));
        if (tpp != 0D && tpp > 0.099) pie_list.add(new PieChart("TPP", tpp));
        if (katilim_hesabi != 0d && katilim_hesabi > 0.099) pie_list.add(new PieChart("Katılım Hesabı", katilim_hesabi));
        if (ozel_sektor_tahvil != 0D && ozel_sektor_tahvil > 0.099) pie_list.add(new PieChart("Özel Sektör Tahvili", ozel_sektor_tahvil));
        if (ters_repo != 0D && ters_repo > 0.099) pie_list.add(new PieChart("Ters Repo", ters_repo));
        if (ozel_kira_sertifikasi != 0D && ozel_kira_sertifikasi > 0.099) pie_list.add(new PieChart("Özel Sektör Kira Sertifikası", ozel_kira_sertifikasi));
        if (turev_araci != 0D && turev_araci > 0.099) pie_list.add(new PieChart("Türev Aracı", turev_araci));
        if (vadeli_mevduat != 0D && vadeli_mevduat > 0.099) pie_list.add(new PieChart("Vadeli Mevduat", vadeli_mevduat));
        if (yab_borclanma_araci != 0D && yab_borclanma_araci > 0.099) pie_list.add(new PieChart("Yabancı Borçlanma Aracı", yab_borclanma_araci));
        if (yab_menkul_kiymet != 0D && yab_menkul_kiymet > 0.099) pie_list.add(new PieChart("Yabanci Menkul Kıymet", yab_menkul_kiymet));


        return pie_list;
    }

}
