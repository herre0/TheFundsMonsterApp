package com.foncanavari.fonApp.servis;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.model.LineChart;
import com.foncanavari.fonApp.repository.FonDetayRepository;
import com.foncanavari.fonApp.repository.FonRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.sound.sampled.Line;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class FonDetayServis {
    @Autowired
    FonDetayRepository fonDetayRepository;
    @Autowired
    FonRepository fonRepository;

    public Boolean kacaklariYakala() {
        List<FonDetay> fonds = fonDetayRepository.findAll();
        int herre = 0;
        for (int i = 0; i < fonds.size(); i++) {
            if (!fonDetayRepository.getUpdatedDate(fonds.get(i).getFon_kod()).equals(tarihHesapla("day", 0)))
                herre++;

            if (herre > 1)
                return true;
        }
        return false;
    }

    public void iceriAktar() throws ParseException {
        List<FonDetay> fondetaylar = fonDetayRepository.findAll();
        List<Fon> fonlar = fonRepository.findAll();
        RestTemplate rest = new RestTemplate();
        String bugun, fonkod, url, json;
        int count;
        FonDetay fn_eklenirse = new FonDetay();
        FonDetay fonDetay;
        for (int i = 0; i < fonlar.size(); i++) {
            fonkod = fonlar.get(i).getKodu();
            /*if (StringUtils.isEmpty(fonDetayRepository.getByKod(fonkod))) {
                fn_eklenirse.setFon_ad(fonlar.get(i).getAdi());
                fn_eklenirse.setFon_kod(fonlar.get(i).getKodu());
                fn_eklenirse.setCategory(fonlar.get(i).getCategory());
                fonDetayRepository.save(fn_eklenirse);
            }*/
            if (fonDetayRepository.getUpdatedDate(fonkod).equals(tarihHesapla("day", 0))) {
                continue;
            }
            bugun = tarihHesapla("day", 0);
            url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;
            json = rest.getForObject(url, String.class);
            if(json == null)
                continue;
            /*count = 0;
            while (true) { gunluk veri bossa gec ugrasma zaten gunluk ?
                count++;
                if (json == null) {
                    bugun = birgunGeri(bugun);
                    url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;
                    json = rest.getForObject(url, String.class);
                }
                if (json != null)
                    break;
                if (count > 3)
                    break;
            }
            if (count > 3) continue;*/
            json = json.replace("[", "");
            json = json.replace("]", "");
            JsonObject json_obje = new JsonParser().parse(json).getAsJsonObject();
            fonDetay = atamaYap(json_obje);
            Fon fon = fonRepository.findByKodu(fonDetay.getFon_kod());
            fon.setFiyat(fonDetay.getBirim_deger());
            fon.setGunluk_artis(fonDetay.getGunluk_artis());
            fonRepository.save(fon);
            fonDetayRepository.save(fonDetay);
           // System.out.println(i + ". " + fon.getKodu() + " basariyla guncellendi !!");
        }
    }

    // gecmisten bir  gun ile simdiki zaman arası fiyat farkını dondurur.
    public static String fiyatArtisHesapla(String yakintarih, String gecmistarih, String fonkod) throws ParseException {
        RestTemplate rest = new RestTemplate();
        String currentDate;
        if (yakintarih == "")
            currentDate = tarihHesapla("day", 0);
        else
            currentDate = yakintarih;
        String simdikiurl = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + currentDate + "/" + currentDate;
        String oncekiurl = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + gecmistarih + "/" + gecmistarih;
        String r, k;
        int i = 0;

        r = rest.getForObject(simdikiurl, String.class);
        k = rest.getForObject(oncekiurl, String.class);
        while (true) {
            i++;
            if (r == null) {
                currentDate = birgunGeri(currentDate);
                simdikiurl = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + currentDate + "/" + currentDate;
                r = rest.getForObject(simdikiurl, String.class);
            }
            if (k == null) {
                gecmistarih = birgunGeri(gecmistarih);
                oncekiurl = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + gecmistarih + "/" + gecmistarih;
                k = rest.getForObject(oncekiurl, String.class);
            }
            if (k != null && r != null)
                break;
            if (i > 5)
                if (r == null || k == null)
                    return "";
        }
        r = r.replace("[", "");
        r = r.replace("]", "");
        k = k.replace("[", "");
        k = k.replace("]", "");

        Double yeniFiyat = new JsonParser().parse(r).getAsJsonObject().get("BirimPayDegeri").getAsDouble();
        Double eskiFiyat = new JsonParser().parse(k).getAsJsonObject().get("BirimPayDegeri").getAsDouble();

        String yuzde = ((yeniFiyat - eskiFiyat) / eskiFiyat) * 100 + "000";
        if (yuzde.equals("0.0000") || yuzde.equals("In") || yuzde.equals("ln"))
            return "";
        return yuzde.substring(0, yuzde.indexOf(".") + 3);
    }

    public static String birgunGeri(String date) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.DATE, -1);

        return dateFormat.format(calendar.getTime());
    }

    public static String tarihHesapla(String dateTip, int index) {
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        switch (dateTip) {
            case "day":
                cal.add(Calendar.DATE, index);
                break;
            case "month":
                cal.add(Calendar.MONTH, index);
                break;
            case "year":
                cal.add(Calendar.YEAR, index);
                break;
        }

        return dateformat.format(cal.getTime());
    }

    public FonDetay atamaYap(JsonObject json) throws ParseException {
        FonDetay fon = fonDetayRepository.getByKod(json.get("FonKodu").getAsString());
        fon.setToplam_deger(json.get("ToplamDeger").getAsString());
        fon.setBirim_deger(json.get("BirimPayDegeri").getAsString());
        fon.setPay_sayisi(json.get("DolasimdakiPaySayisi").getAsString());
        fon.setYatirimci_sayisi(json.get("YatirimciSayisi").getAsString()); // gelen bilgisi yanlis
        fon.setBanka_bonosu(json.get("BankaBonosu").getAsString());
        fon.setDiger(json.get("Diger").getAsString());
        fon.setDevlet_tahvili(json.get("DevletTahvili").getAsString());
        fon.setDoviz_odemeli_bono(json.get("DovizOdemeliBono").getAsString());
        fon.setDoviz_odemeli_tahvil(json.get("DovizOdemeliTahvil").getAsString());
        fon.setEurobond(json.get("Eurobond").getAsString());
        fon.setFinansman_bonosu(json.get("FinansmanBonosu").getAsString());
        fon.setFon_katilma_belgesi(json.get("FonKatilmaBelgesi").getAsString());
        fon.setGayrimenkul_sertifikasi(json.get("GayrimenkulSertifikasi").getAsString());
        fon.setHazine_bonosu(json.get("HazineBonosu").getAsString());
        fon.setHisse_senedi(json.get("HisseSenedi").getAsString());
        fon.setKamu_dis_borclanma_araci(json.get("KamuDisBorclanmaAraci").getAsString());
        fon.setKamu_kira_sertifikası(json.get("KamuKiraSertifikasi").getAsString());
        fon.setKatilim_hesabi(json.get("KatilimHesabi").getAsString());
        fon.setKiymetli_maden(json.get("KiymetliMaden").getAsString());
        fon.setOzel_kira_sertifikasi(json.get("OzelSektorKiraSertifikasi").getAsString());
        fon.setOzel_sektor_tahvil(json.get("OzelSektorTahvili").getAsString());
        fon.setTers_repo(json.get("TersRepo").getAsString());
        fon.setTpp(json.get("TPP").getAsString());
        fon.setTurev_araci(json.get("TurevAraci").getAsString());
        fon.setVarlik_menkul_kiymet(json.get("VarligaDayaliMenkulKiymet").getAsString());
        fon.setVadeli_mevduat(json.get("VadeliMevduat").getAsString());
        fon.setYab_borclanma_araci(json.get("YabanciBorclanmaAraci").getAsString());
        fon.setYab_hisse_senedi(json.get("YabanciHisseSenedi").getAsString());
        fon.setYab_menkul_kiymet(json.get("YabanciMenkulKiymet").getAsString());
        fon.setG_tarih(tarihHesapla("day", 0));
        // -- Hesaplamalar --
        String gunluk_artis = fiyatArtisHesapla("", tarihHesapla("day", -1), fon.getFon_kod());
        String haftalik_artis = fiyatArtisHesapla("", tarihHesapla("day", -7), fon.getFon_kod());
        String aylik_artis = fiyatArtisHesapla("", tarihHesapla("month", -1), fon.getFon_kod());
        String alti_aylik_artis = fiyatArtisHesapla("", tarihHesapla("month", -6), fon.getFon_kod());
        String yillik_artis = fiyatArtisHesapla("", tarihHesapla("year", -1), fon.getFon_kod());
        String uc_yillik_artis = fiyatArtisHesapla("", tarihHesapla("year", -3), fon.getFon_kod());
        String _2020_artis = fiyatArtisHesapla("", "2020-01-01", fon.getFon_kod());
        if (!gunluk_artis.equals("")) fon.setGunluk_artis(gunluk_artis);
        if (!haftalik_artis.equals("")) fon.setHaftalik_artis(haftalik_artis);
        if (!aylik_artis.equals("")) fon.setAylik_artis(aylik_artis);
        if (!alti_aylik_artis.equals("")) fon.setAlti_aylik_artis(alti_aylik_artis);
        if (!yillik_artis.equals("")) fon.setYillik_artis(yillik_artis);
        if (!uc_yillik_artis.equals("")) fon.setUc_yillik_artis(uc_yillik_artis);
        if (StringUtils.isEmpty(fon.get_2017()))
            fon.set_2017(fiyatArtisHesapla("2017-12-31", "2017-01-01", fon.getFon_kod()));
        if (StringUtils.isEmpty(fon.get_2018()))
            fon.set_2018(fiyatArtisHesapla("2018-12-31", "2018-01-01", fon.getFon_kod()));
        if (StringUtils.isEmpty(fon.get_2019()))
            fon.set_2019(fiyatArtisHesapla("2019-12-31", "2019-01-01", fon.getFon_kod()));
        if (!_2020_artis.equals("")) fon.set_2020(_2020_artis);


        return fon;
    }

    public static List<LineChart> LineChartData(String datetip, String fonkod) {
        RestTemplate rest = new RestTemplate();
        List<LineChart> chartList = new ArrayList<LineChart>();
        String bugun = tarihHesapla("day", 0);
        String gecmis_tarih;
        if (datetip.equals("year")) gecmis_tarih = tarihHesapla("year", -1);
        else gecmis_tarih = tarihHesapla("month", -1);

        String url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + gecmis_tarih + "/" + bugun;
        String json = rest.getForObject(url, String.class);
        //json = json.replace("[", "");
        //json = json.replace("]", "");

        //JsonArray degerler = new JsonParser().parse(json).getAsJsonObject().get("BirimPayDegeri").getAsJsonArray();
        //JsonArray tarihler = new JsonParser().parse(json).getAsJsonObject().get("BirimPayDegeri").getAsJsonArray();
        JsonArray veri = new JsonParser().parse(json).getAsJsonArray();

        for (int i = 0; i < veri.size(); i++) {
            chartList.add(new LineChart(veri.get(i).getAsJsonObject().get("Tarih").getAsString().substring(0, 10), veri.get(i).getAsJsonObject().get("BirimPayDegeri").getAsDouble()));
        }

        return chartList;

    }

    // todo 2. versiyonda isleme alınacaktır!
    public void sapmavesharpeHesapla(String fonkod) throws ParseException {
        fonkod = "IPV";
        int count = 0; // IPV 0.73 IPV 0.327 verdi yuzde/100gun
        RestTemplate rest = new RestTemplate();
        List<Double> fiyatlar = new ArrayList<Double>();
        String json;
      /*  for (int i = 1; i<= 100;i++) {
            String tarih = tarihHesapla("day", -i);
            String url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + tarih + "/" + tarih;
            json = rest.getForObject(url, String.class);
            count = 0;
            while (true) {
                count++;
                if (json == null) {
                    tarih = birgunGeri(tarih);
                    url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + tarih + "/" + tarih;
                    json = rest.getForObject(url, String.class);
                }
                if (json != null)
                    break;
                if (count > 3)
                    break;
            }
            if(count > 3) continue;
            json = json.replace("[", "");
            json = json.replace("]", "");
            JsonObject json_obje = new JsonParser().parse(json).getAsJsonObject();
            fiyatlar.add(json_obje.get("BirimPayDegeri").getAsDouble());

        }*///todo burası onceden yapilmisti, fiyat bazli hesaplıyor halbuki assadaki gibi yuzde bazli olmalıydı silinebilir.
        String artis_yuzdesi;
        for (int i = 0; i < 100; i++) {
            artis_yuzdesi = fiyatArtisHesapla(tarihHesapla("day", -(i)), tarihHesapla("day", -((i) - 1)), fonkod);
            if (artis_yuzdesi != "")
                fiyatlar.add(Double.valueOf(artis_yuzdesi));
        } // todo bu haliyle son 100 gunun standart sapmasını veriyor. 360 gun icin 5 dk suruyor fon basina
        // todo boyle olunca son 100 gunu alıp felan yapılabilir

        Double ort, toplam = 0D, farkinkareleri = 0D;
        int size = fiyatlar.size();
        for (int i = 0; i < size; i++) {
            toplam += fiyatlar.get(i);
        }
        ort = toplam / size;
        for (int i = 0; i < size; i++) {
            farkinkareleri += Math.pow(fiyatlar.get(i) - ort, 2);
        }
        String s_sapma = String.valueOf(farkinkareleri / (size - 1)).substring(0, 5);
        System.out.println(s_sapma);
    }


}
