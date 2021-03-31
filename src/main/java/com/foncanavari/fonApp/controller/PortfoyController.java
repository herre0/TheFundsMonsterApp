package com.foncanavari.fonApp.controller;

import com.foncanavari.fonApp.model.*;
import com.foncanavari.fonApp.payload.response.MessageResponse;
import com.foncanavari.fonApp.repository.*;
import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.foncanavari.fonApp.servis.FonDetayServis.tarihHesapla;


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
    @Autowired
    PortfoyPerformansRepository portfoyPerformansRepository;
    @Autowired
    PortfoyGecmisRepository portfoyGecmisRepository;


    @PostMapping
    @CrossOrigin
    public ResponseEntity<?> PortfoyOlustur(@Valid @RequestBody Portfoy portfoy, @RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);
        // if(user.getPortfoy_sayisi() > 2)
        //   return ResponseEntity.badRequest().body(new MessageResponse("Portföylerin sayısı limiti aştı!"));
        for (Portfoy item : user.getPortfoyler()) {
            if (item.getAdi().toUpperCase().equals(portfoy.getAdi().toUpperCase()))
                return ResponseEntity.badRequest().body(new MessageResponse("Aynı isimle portföy mevcut!"));
        }

        for (PortFon item : portfoy.getFonlar()) {
            if (item.getAlis_maliyeti() == null || item.getAdet() == 0)
                return ResponseEntity.badRequest().body(new MessageResponse("Tüm alanları doldurun!"));
            if (item.getAlis_maliyeti().toString().contains(","))
                return ResponseEntity.badRequest().body(new MessageResponse("Ondalıklı sayı girerken \".\" kullanılmalıdır!"));
            if (String.valueOf(item.getAdet()).contains(".") || String.valueOf(item.getAdet()).contains(","))
                return ResponseEntity.badRequest().body(new MessageResponse("Pay sayısı tam sayı olmalıdır!"));
         }

        if (portfoy.getAdi() != null && portfoy.getAdi().length() > 30)
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı 40 karakterden fazla olamaz!"));

        if (StringUtils.isEmpty(portfoy.getAdi()))
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı boş olamaz!"));

        if (portfoy.getAdi().contains(".") ||
                portfoy.getAdi().toUpperCase().contains("SCRIPT") ||
                portfoy.getAdi().contains("#") ||
                portfoy.getAdi().contains("(") ||
                portfoy.getAdi().contains(">") ||
                portfoy.getAdi().contains("'") ||
                portfoy.getAdi().contains("<"))
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı özel karakterler içeremez!"));

        portfoy.setAdi(portfoy.getAdi().trim());
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
            p.setToplam_getiri_tl((Double.valueOf(fon.getFiyat()) - p.getAlis_maliyeti()) * p.getAdet());
            p.setToplam_getiri_yuzde(((Double.valueOf(p.getBirim_fiyati()) - p.getAlis_maliyeti()) / p.getAlis_maliyeti()) * 100);
        }
        portfoy.setFonlar(portfonlar);
        portfoy.setUser_id(user.getId());
        portfoy.setY_tarih(tarihHesapla("day", 0));

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

        PortfoyGecmis pgecmis;
        Portfoy portfoy_sonhali = portfoyRepository.findPortfoyByUseridandPname(user.getId(), portfoy.getAdi());
        for (PortFon p_yeni : portfoy_sonhali.getFonlar()) {
            pgecmis = new PortfoyGecmis();
            pgecmis.setFon_kod(p_yeni.getFon_kod());
            pgecmis.setIslem("EKLENDİ");
            pgecmis.setPortfoy_id(portfoy_sonhali.getId());
            pgecmis.setTarih(FonDetayServis.tarihHesapla("day", 0));

            for (PortFon poc : portfoy_sonhali.getFonlar()) {
                if (poc.getFon_kod().equals(p_yeni.getFon_kod())) {
                    pgecmis.setFiyat_hareketi(Integer.valueOf(poc.getDegeri().toString().substring(0, poc.getDegeri().toString().indexOf("."))));
                    pgecmis.setToplam_kar_tl("0");
                    pgecmis.setToplam_kar_yuzde("0.00");
                    break;
                }
            }
            portfoyGecmisRepository.save(pgecmis);
        }

        return ResponseEntity.ok(portfoyler);
    }

    @PostMapping("/guncelle")
    @CrossOrigin
    public ResponseEntity<?> PortfoyDuzenle(@Valid @RequestBody Portfoy portfoy, @RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);

        for (PortFon item : portfoy.getFonlar()) {
            if (item.getAlis_maliyeti() == null || item.getAdet() == 0)
                return ResponseEntity.badRequest().body(new MessageResponse("Tüm alanları doldurun!"));
        }

        if (portfoy.getAdi() != null && portfoy.getAdi().length() > 30)
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı 40 karakterden fazla olamaz!"));

        if (StringUtils.isEmpty(portfoy.getAdi()))
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı boş olamaz!"));

        if (portfoy.getAdi().contains(".") ||
                portfoy.getAdi().toUpperCase().contains("SCRIPT") ||
                portfoy.getAdi().contains("#") ||
                portfoy.getAdi().contains("(") ||
                portfoy.getAdi().contains(">") ||
                portfoy.getAdi().contains("'") ||
                portfoy.getAdi().contains("<"))
            return ResponseEntity.badRequest().body(new MessageResponse("Portföy adı özel karakterler içeremez!"));

        Portfoy portfoy_eski = portfoyRepository.findPortfoyByUseridandPname(user.getId(), portfoy.getAdi());
        if(portfoy_eski == null)
            return ResponseEntity.badRequest().body(new MessageResponse("Bir Hata Oluştu!"));

        FonDetayServis fn = new FonDetayServis();
        List<PortFon> portfonsofPortfoyGecici;

        Boolean listede_var = false;
        for (PortFon p_yeni : portfoy.getFonlar()) {
            for (PortFon p_eski : portfoy_eski.getFonlar()) {
                if (p_yeni.getFon_kod().equals(p_eski.getFon_kod())) {
                    listede_var = true;
                    break;
                }
            }

            if (!listede_var) {
                System.out.println(p_yeni.getFon_kod() + " " + p_yeni.getAdet() + " " + p_yeni.getAlis_maliyeti() + " eklendi!!");
                portfonRepository.save(p_yeni);
                portfoy_eski.getFonlar().add(p_yeni);
                portfoyRepository.save(portfoy_eski);
                portfoyHesapla(p_yeni.getFon_kod(), p_yeni.getId());
                portfoy_eski = portfoyRepository.findPortfoyByUseridandPname(user.getId(), portfoy.getAdi());


                PortfoyGecmis pgecmis = new PortfoyGecmis();
                pgecmis.setFon_kod(p_yeni.getFon_kod());
                pgecmis.setIslem("EKLENDİ");
                pgecmis.setPortfoy_id(portfoy_eski.getId());
                pgecmis.setTarih(FonDetayServis.tarihHesapla("day", 0));

                for (PortFon poc : portfoy_eski.getFonlar()) {
                    if (poc.getFon_kod().equals(p_yeni.getFon_kod())) {
                        pgecmis.setFiyat_hareketi(Integer.valueOf(poc.getDegeri().toString().substring(0, poc.getDegeri().toString().indexOf("."))));
                        pgecmis.setToplam_kar_tl("0");
                        pgecmis.setToplam_kar_yuzde("0.00");
                        break;
                    }
                }

                portfoyGecmisRepository.save(pgecmis);
            }

            listede_var = false;
        }
        int yeni_lot_sayisi = 0;
        Double yeni_fiyat = 0D;
        portfoy_eski = portfoyRepository.findPortfoyByUseridandPname(user.getId(), portfoy.getAdi());

        for (int i = 0; i < portfoy_eski.getFonlar().size(); i++) {
            PortFon p_eski = portfoy_eski.getFonlar().get(i);
            for (PortFon p_yeni : portfoy.getFonlar()) {
                if (p_eski.getFon_kod().equals(p_yeni.getFon_kod())) {
                    listede_var = true;
                    if (p_eski.getAdet() != p_yeni.getAdet())
                        yeni_lot_sayisi = p_yeni.getAdet();

                    if (!p_eski.getAlis_maliyeti().equals(p_yeni.getAlis_maliyeti()))
                        yeni_fiyat = p_yeni.getAlis_maliyeti();

                    break;
                }
            }

            if (!listede_var) {
                System.out.println(p_eski.getFon_kod() + " " + " cikarildi!!");
                PortfoyGecmis pgecmis = new PortfoyGecmis();
                pgecmis.setFon_kod(p_eski.getFon_kod());
                pgecmis.setIslem("ÇIKARILDI");
                pgecmis.setPortfoy_id(portfoy_eski.getId());
                pgecmis.setTarih(FonDetayServis.tarihHesapla("day", 0));
                pgecmis.setFiyat_hareketi(-1 * Integer.valueOf(p_eski.getDegeri().toString().substring(0, p_eski.getDegeri().toString().indexOf("."))));
                pgecmis.setToplam_kar_tl(p_eski.getToplam_getiri_tl().toString());
                pgecmis.setToplam_kar_yuzde(p_eski.getToplam_getiri_yuzde().toString().substring(0, p_eski.getToplam_getiri_yuzde().toString().indexOf(".") + 3));
                portfoyGecmisRepository.save(pgecmis);


                portfoy_eski.getFonlar().remove(p_eski);
                i--;
                portfoyRepository.save(portfoy_eski);

                for (PortFon poc : portfoy_eski.getFonlar()) {
                    portfoyHesapla(poc.getFon_kod(), poc.getId());
                }
            }

            if (yeni_lot_sayisi != 0) {
                System.out.println(p_eski.getFon_kod() + " yeni lot sayısı = " + yeni_lot_sayisi + "");
                int lot_degisimi = p_eski.getAdet() - yeni_lot_sayisi;
                p_eski.setAdet(yeni_lot_sayisi);
                portfonRepository.save(p_eski);
                PortfoyGecmis pgecmis = new PortfoyGecmis();
                pgecmis.setFon_kod(p_eski.getFon_kod());
                pgecmis.setPortfoy_id(portfoy_eski.getId());
                pgecmis.setTarih(FonDetayServis.tarihHesapla("day", 0));

                if (lot_degisimi < 0) {
                    pgecmis.setIslem("PAY ALIŞ");
                    pgecmis.setFiyat_hareketi(Integer.valueOf((int) ((yeni_fiyat == 0D ? (p_eski.getAlis_maliyeti() * -lot_degisimi) : (-lot_degisimi * yeni_fiyat)))));
                    pgecmis.setToplam_kar_tl("0");
                    pgecmis.setToplam_kar_yuzde("0.00");
                } else {
                    pgecmis.setIslem("PAY SATIŞ");
                    pgecmis.setFiyat_hareketi(Integer.valueOf((int) (p_eski.getBirim_fiyati() * lot_degisimi * (-1))));
                    pgecmis.setToplam_kar_tl(String.valueOf((p_eski.getBirim_fiyati() - p_eski.getAlis_maliyeti()) * lot_degisimi));
                    pgecmis.setToplam_kar_yuzde(p_eski.getToplam_getiri_yuzde().toString().substring(0, p_eski.getToplam_getiri_yuzde().toString().indexOf(".") + 3));
                }
                portfoyGecmisRepository.save(pgecmis);
            }

            if (yeni_fiyat != 0D) {
                System.out.println(p_eski.getFon_kod() + " yeni fiyat = " + yeni_fiyat + "");
                p_eski.setAlis_maliyeti(yeni_fiyat);
                portfonRepository.save(p_eski);
            }

            if (yeni_fiyat != 0D || yeni_lot_sayisi != 0)
                for (PortFon poc : portfoy_eski.getFonlar()) {
                    portfoyHesapla(poc.getFon_kod(), poc.getId());
                }

            listede_var = false;
            yeni_lot_sayisi = 0;
            yeni_fiyat = 0D;
        }


        portfoy_eski.setAdi(portfoy.getAdi());
        portfoy_eski.setG_tarih(tarihHesapla("day", 0));

        portfoyRepository.save(portfoy_eski);
        user = userRepository.findUserByUsername(username);

        return ResponseEntity.ok(user.getPortfoyler());
    }

    public void portfoyHesapla(String fon_kod, int id) {

                Fon fon = fonRepository.findByKodu(fon_kod);

                List<PortFon> portfonlar = portfonRepository.findAllByID(id);
                Portfoy portfoy;
                Double portfoy_degeri;
                Double gunluk_tl, gunluk_yuzde, toplam_tl, toplam_yuzde, deger;
                Fon fon_gecici;
                Fon fon_gecici2;
                for (PortFon p : portfonlar) {
                    portfoy = portfoyRepository.findPortfoyByPortfonId(p.getId());
                    portfoy_degeri = 0D;
                    if (portfoy == null)
                        continue;

                    for (PortFon port : portfoy.getFonlar()) {
                        fon_gecici = fonRepository.findByKodu(port.getFon_kod());

                        if (fon_gecici.getKodu().equals(fon.getKodu()))
                            portfoy_degeri += (Double.valueOf(fon.getFiyat()) * port.getAdet());
                        else
                            portfoy_degeri += (port.getDegeri());
                    }
                    portfoy.setPortfoy_degeri(portfoy_degeri);

                    for (PortFon portfon : portfoy.getFonlar()) {
                        fon_gecici2 = fonRepository.findByKodu(portfon.getFon_kod());
                        if (fon_gecici2.getKodu().equals(fon.getKodu()))
                            portfon.setAgirlik(((portfon.getAdet() * Double.valueOf(fon.getFiyat())) / portfoy.getPortfoy_degeri()) * 100);
                        else
                            portfon.setAgirlik(((portfon.getAdet() * Double.valueOf(fon_gecici2.getFiyat())) / portfoy.getPortfoy_degeri()) * 100);
                    }

                    p.setBirim_fiyati(Double.valueOf(fon.getFiyat()));
                    p.setDegeri(Double.valueOf(fon.getFiyat()) * p.getAdet());
                    p.setGunluk_getiri_yuzde(Double.valueOf(fon.getGunluk_artis()));
                    p.setGunluk_getiri_tl(p.getDegeri() - ((((100 - Double.valueOf(fon.getGunluk_artis())) * Double.valueOf(fon.getFiyat())) / 100) * p.getAdet()));
                    p.setToplam_getiri_tl(p.getDegeri() - (p.getAdet() * p.getAlis_maliyeti()));
                    p.setToplam_getiri_yuzde(((Double.valueOf(p.getBirim_fiyati()) - p.getAlis_maliyeti()) / p.getAlis_maliyeti()) * 100);

                    portfonRepository.save(p);
                    portfoy = portfoyRepository.findPortfoyByPortfonId(p.getId());//burda set edildiginden tekrardan deger hesaplaniyor assagida

                    gunluk_tl = 0D;
                    gunluk_yuzde = 0D;
                    toplam_tl = 0D;
                    toplam_yuzde = 0D;
                    deger = 0D;
                    for (PortFon pic : portfoy.getFonlar()) {
                        gunluk_tl += pic.getGunluk_getiri_tl();
                        gunluk_yuzde += (pic.getGunluk_getiri_yuzde() * pic.getAgirlik());
                        toplam_tl += pic.getToplam_getiri_tl();
                        toplam_yuzde += pic.getToplam_getiri_yuzde() * pic.getAgirlik();
                        deger += pic.getDegeri();
                    }
                    portfoy.setGunluk_getiri_tl(gunluk_tl);
                    portfoy.setGunluk_getiri_yuzde(gunluk_yuzde / 100);
                    portfoy.setToplam_getiri_tl(toplam_tl);
                    portfoy.setToplam_getiri_yuzde(toplam_yuzde / 100);
                    portfoy.setPortfoy_degeri(deger);

                    PortfoyPerformans portfoyPerformans = portfoyPerformansRepository.getOneByTarihAndId(tarihHesapla("day", 0), portfoy.getId());
                    if (portfoyPerformans == null) {
                        portfoyPerformans = new PortfoyPerformans();
                        portfoyPerformans.setTarih(tarihHesapla("day", 0));
                        portfoyPerformans.setPortfoy_id(portfoy.getId());
                    }
                    portfoyPerformans.setPortfoy_degeri(String.valueOf(portfoy.getPortfoy_degeri()));

                    if (StringUtils.isEmpty(portfoy.getPortfoy_degeri()) || StringUtils.isEmpty(portfoy.getGunluk_getiri_tl()) ||
                            StringUtils.isEmpty(portfoy.getToplam_getiri_yuzde()) || StringUtils.isEmpty(portfoy.getToplam_getiri_tl()))
                        continue;

                    portfoyPerformansRepository.save(portfoyPerformans);
                    portfoyRepository.updatePortfoy(portfoy.getPortfoy_degeri(), portfoy.getGunluk_getiri_tl(), portfoy.getGunluk_getiri_yuzde(), portfoy.getToplam_getiri_tl(), portfoy.getToplam_getiri_yuzde(), portfoy.getId());
                }


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
    @GetMapping("/poy")
    public List<Portfoy> getPortfoyListByUser(@RequestParam(value = "u") String username) {
        User user = userRepository.findUserByUsername(username);
        return user.getPortfoyler();
    }

    @CrossOrigin
    @GetMapping("/kategori")
    public List<PieChart> getKategoriDagilimiForPieChart(@RequestParam(value = "pie") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);

        List<PortFon> portfonlar = portfoy.getFonlar();
        List<PieChart> pieList = new ArrayList<>();
        Fon fon = new Fon();
        Double hisse = 0D, borclanma = 0D, degisken = 0D, katilim = 0D, altin = 0D, sepet = 0D, para_piyasasi = 0D;
        for (PortFon p : portfonlar) {
            fon = fonRepository.findByKodu(p.getFon_kod());
            if (fon.getCategory().equals("Hisse Senedi Fonları")) hisse += p.getAgirlik();
            if (fon.getCategory().equals("Borçlanma Araçları Fonları")) borclanma += p.getAgirlik();
            if (fon.getCategory().equals("Katılım Fonları")) katilim += p.getAgirlik();
            if (fon.getCategory().equals("Kıymetli Madenler Fonları")) altin += p.getAgirlik();
            if (fon.getCategory().equals("Fon Sepeti Fonları")) sepet += p.getAgirlik();
            if (fon.getCategory().equals("Karma ve Değişken Fonlar")) degisken += p.getAgirlik();
            if (fon.getCategory().equals("Para Piyasası Fonları")) para_piyasasi += p.getAgirlik();
        }

        if (hisse != 0D) pieList.add(new PieChart("Hisse Senedi Fonları", hisse));
        if (degisken != 0D) pieList.add(new PieChart("Karma ve Değişken Fonlar", degisken));
        if (borclanma != 0D) pieList.add(new PieChart("Borçlanma Araçları Fonları", borclanma));
        if (katilim != 0D) pieList.add(new PieChart("Katılım Fonları", katilim));
        if (altin != 0D) pieList.add(new PieChart("Kıymetli Madenler Fonları", altin));
        if (sepet != 0D) pieList.add(new PieChart("Fon Sepeti Fonları", sepet));
        if (para_piyasasi != 0D) pieList.add(new PieChart("Para Piyasası Fonları", para_piyasasi));

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
        user.setPortfoy_sayisi(user.getPortfoy_sayisi() - 1);
        userRepository.save(user);

        return user.getPortfoyler();
    }

    @CrossOrigin
    @GetMapping("/lineperf")
    public List<LineChart> getPortfoyPerformansForLineChart(@RequestParam(value = "pid") int portfoy_id) {
        List<LineChart> lineChartList = new ArrayList<>();
        List<PortfoyPerformans> performansList = portfoyPerformansRepository.findAllByPortfoyId(portfoy_id);

        for (int i = performansList.size() - 1; i >= 0; i--) {
            lineChartList.add(new LineChart(performansList.get(i).getTarih(), Double.valueOf(performansList.get(i).getPortfoy_degeri())));
        }

        return lineChartList;
    }

    @CrossOrigin
    @GetMapping("/gecmis")
    public List<PortfoyGecmis> getPortfoyGecmisList(@RequestParam(value = "un") String username, @RequestParam(value = "pname") String pname) {
        User user = userRepository.findUserByUsername(username);
        Portfoy portfoy = portfoyRepository.findPortfoyByUseridandPname(user.getId(), pname);

        return portfoyGecmisRepository.findGecmisListByPortfoyId(portfoy.getId());
    }

    @CrossOrigin
    @PostMapping("/isimlilist")
    public List<PortFonJson> getPortfoyPerformansForLineChart(@RequestBody List<PortFonJson> portfonlar) {
        for (PortFonJson portfon : portfonlar) {
            portfon.setFon_ad(fonRepository.findByKodu(portfon.getFon_kod()).getAdi());
        }

        return portfonlar;
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
        if (yab_hisse_senedi != 0D && yab_hisse_senedi > 0.099)
            pie_list.add(new PieChart("Yabancı Hisse Senedi", yab_hisse_senedi));
        if (hisse_senedi != 0D && hisse_senedi > 0.099) pie_list.add(new PieChart("Hisse Senedi", hisse_senedi));
        if (devlet_tahvili != 0D && devlet_tahvili > 0.099)
            pie_list.add(new PieChart("Devlet Tahvili", devlet_tahvili));
        if (banka_bonosu != 0D && banka_bonosu > 0.099) pie_list.add(new PieChart("Banka Bonosu", banka_bonosu));
        if (eurobond != 0D && eurobond > 0.099) pie_list.add(new PieChart("Eurobond", eurobond));
        if (kiymetli_maden != 0D && kiymetli_maden > 0.099)
            pie_list.add(new PieChart("Kıymetli Maden", kiymetli_maden));
        if (diger != 0D && diger > 0.099) pie_list.add(new PieChart("Diğer", diger));
        if (doviz_odemeli_bono != 0D && doviz_odemeli_bono > 0.099)
            pie_list.add(new PieChart("Döviz Ödemeli Bono", doviz_odemeli_bono));
        if (doviz_odemeli_tahvil != 0D && doviz_odemeli_tahvil > 0.099)
            pie_list.add(new PieChart("Döviz Ödemeli Tahvil", doviz_odemeli_tahvil));
        if (finansman_bonosu != 0D && finansman_bonosu > 0.099)
            pie_list.add(new PieChart("Finansman Bonosu", finansman_bonosu));
        if (fon_katilma_belgesi != 0D && fon_katilma_belgesi > 0.099)
            pie_list.add(new PieChart("Fon Katılma Belgesi", fon_katilma_belgesi));
        if (gayrimenkul_sertifikasi != 0D && gayrimenkul_sertifikasi > 0.099)
            pie_list.add(new PieChart("Gayrimenkul Sertifikası", gayrimenkul_sertifikasi));
        if (hazine_bonosu != 0D && hazine_bonosu > 0.099) pie_list.add(new PieChart("Hazine Bonosu", hazine_bonosu));
        if (kamu_dis_borclanma_araci != 0D && kamu_dis_borclanma_araci > 0.099)
            pie_list.add(new PieChart("Kamu Dış Borçlanma Aracı", kamu_dis_borclanma_araci));
        if (varlik_menkul_kiymet != 0D && varlik_menkul_kiymet > 0.099)
            pie_list.add(new PieChart("Varlığa Dayalı Menkul Kıymet", varlik_menkul_kiymet));
        if (kamu_kira_sertifikası != 0D && kamu_kira_sertifikası > 0.099)
            pie_list.add(new PieChart("Kamu Kira Sertifikası", kamu_kira_sertifikası));
        if (tpp != 0D && tpp > 0.099) pie_list.add(new PieChart("TPP", tpp));
        if (katilim_hesabi != 0d && katilim_hesabi > 0.099)
            pie_list.add(new PieChart("Katılım Hesabı", katilim_hesabi));
        if (ozel_sektor_tahvil != 0D && ozel_sektor_tahvil > 0.099)
            pie_list.add(new PieChart("Özel Sektör Tahvili", ozel_sektor_tahvil));
        if (ters_repo != 0D && ters_repo > 0.099) pie_list.add(new PieChart("Ters Repo", ters_repo));
        if (ozel_kira_sertifikasi != 0D && ozel_kira_sertifikasi > 0.099)
            pie_list.add(new PieChart("Özel Sektör Kira Sertifikası", ozel_kira_sertifikasi));
        if (turev_araci != 0D && turev_araci > 0.099) pie_list.add(new PieChart("Türev Aracı", turev_araci));
        if (vadeli_mevduat != 0D && vadeli_mevduat > 0.099)
            pie_list.add(new PieChart("Vadeli Mevduat", vadeli_mevduat));
        if (yab_borclanma_araci != 0D && yab_borclanma_araci > 0.099)
            pie_list.add(new PieChart("Yabancı Borçlanma Aracı", yab_borclanma_araci));
        if (yab_menkul_kiymet != 0D && yab_menkul_kiymet > 0.099)
            pie_list.add(new PieChart("Yabanci Menkul Kıymet", yab_menkul_kiymet));


        return pie_list;
    }

}
