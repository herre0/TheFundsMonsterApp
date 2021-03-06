package com.foncanavari.fonApp.servis;

import com.foncanavari.fonApp.model.*;
import com.foncanavari.fonApp.repository.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.MathContext;
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
    @Autowired
    PortfoyRepository portfoyRepository;
    @Autowired
    PortfonRepository portfonRepository;
    @Autowired
    PortfoyPerformansRepository portfoyPerformansRepository;


    public Boolean kacaklariYakala() {
        List<FonDetay> fonds = fonDetayRepository.findAll();
        int herre = 0;
        for (int i = 0; i < fonds.size(); i++) {
            if (!fonDetayRepository.getUpdatedDate(fonds.get(i).getFon_kod()).equals(tarihHesapla("day", 0)))
                herre++;

            if (herre > 0)
                return true;
        }
        return false;
    }

    public void iceriAktar() throws ParseException {
                List<FonDetay> fondetaylar = fonDetayRepository.findAll();
                List<Fon> fonlar = fonRepository.findAll();
                RestTemplate rest = new RestTemplate();
                String bugun, fonkod, url, json;
                for (int i = 0; i < fonlar.size(); i++) {
                    fonkod = fonlar.get(i).getKodu();

                    if (fonDetayRepository.getUpdatedDate(fonkod).equals(tarihHesapla("day", 0))) {
                        continue;
                    }
                    bugun = tarihHesapla("day", -2);
                    url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;

                    json = rest.getForObject(url, String.class);
                    if (json == null || json.contains("html"))
                        continue;


                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    Double fiyat = new JsonParser().parse(json).getAsJsonObject().get("BirimPayDegeri").getAsDouble();
                    if (fiyat == 0D)
                        continue;
                    JsonObject json_obje = new JsonParser().parse(json).getAsJsonObject();
                    FonDetay fonDetay = null;
                    try {
                        fonDetay = atamaYap(json_obje);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Fon fon = fonRepository.findByKodu(fonDetay.getFon_kod());
                    fon.setFiyat(fonDetay.getBirim_deger());
                    fon.setGunluk_artis(fonDetay.getGunluk_artis());

                    fonRepository.save(fon);
                    fonDetayRepository.save(fonDetay);
                    portfoyHesapla(fon.getKodu());
                    System.out.println(i + ". " + fon.getKodu() + " guncellendi !!");
                }
    }

    public void portfoyHesapla(String fon_kod) {
        Fon fon = fonRepository.findByKodu(fon_kod);

        List<PortFon> portfonlar = portfonRepository.findAllByFonkod(fon_kod);
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
            portfoyPerformans.setPortfoy_degeri(String.valueOf(portfoy.getPortfoy_degeri()).substring(0, String.valueOf(portfoy.getPortfoy_degeri()).indexOf(".") + 3));

            portfoyPerformansRepository.save(portfoyPerformans);
            portfoyRepository.updatePortfoy(portfoy.getPortfoy_degeri(), portfoy.getGunluk_getiri_tl(), portfoy.getGunluk_getiri_yuzde(), portfoy.getToplam_getiri_tl(), portfoy.getToplam_getiri_yuzde(), portfoy.getId());
        }


    }

    // gecmisten bir  gun ile simdiki zaman aras?? fiyat fark??n?? dondurur.
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

        Double yeniFiyat0 = new JsonParser().parse(r).getAsJsonObject().get("BirimPayDegeri").getAsDouble();
        Double eskiFiyat0 = new JsonParser().parse(k).getAsJsonObject().get("BirimPayDegeri").getAsDouble();

        BigDecimal yeniFiyat = new BigDecimal(yeniFiyat0);
        BigDecimal eskiFiyat = new BigDecimal(eskiFiyat0);

        if (eskiFiyat0.equals(0D) || yeniFiyat0.equals(eskiFiyat0))
            return "0.00";

        String yuzde = (((yeniFiyat.subtract(eskiFiyat)).divide(eskiFiyat, MathContext.DECIMAL32)).multiply(new BigDecimal("100"))) + "000";//todo double da E basamakl?? olunca b??lmeyi do??ru yapm??yo
        if (yuzde.equals("0.0000"))
            return "0.00";

        return yuzde.substring(0, yuzde.indexOf(".") + 3);//todo buras?? 5 ti 3 yapt??m canl?? i??in
    }

    public static String birgunGeri(String date) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.DATE, -1);

        return dateFormat.format(calendar.getTime());
    }

    public static boolean returnGunFarki(String date) throws ParseException {
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Date bugun = new SimpleDateFormat("yyyy-MM-dd").parse(tarihHesapla("day", -35));



        return date1.before(bugun);

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

    public static String tarihSaatHesapla() {
        DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        return dateformat.format(cal.getTime());
    }

    public static String tarihHesaplaTR() {
        DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        return dateformat.format(cal.getTime());
    }

    public FonDetay atamaYap(JsonObject json) throws ParseException {
        FonDetay fon = fonDetayRepository.getByKod(json.get("FonKodu").getAsString()) == null ? new FonDetay(json.get("FonKodu").getAsString(), json.get("FonUnvani").getAsString()) : fonDetayRepository.getByKod(json.get("FonKodu").getAsString());
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
        fon.setKamu_kira_sertifikas??(json.get("KamuKiraSertifikasi").getAsString());
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
        //fon.setStandart_sapma(standartSapmaHesapla(fon.getFon_kod()));
        // -- Hesaplamalar --
        String gunluk_artis = fiyatArtisHesapla("", tarihHesapla("day", -1), fon.getFon_kod());
        String haftalik_artis = fiyatArtisHesapla("", tarihHesapla("day", -7), fon.getFon_kod());
        String aylik_artis = fiyatArtisHesapla("", tarihHesapla("month", -1), fon.getFon_kod());
        String uc_aylik_artis = fiyatArtisHesapla("", tarihHesapla("month", -3), fon.getFon_kod());
        String alti_aylik_artis = fiyatArtisHesapla("", tarihHesapla("month", -6), fon.getFon_kod());
        String yillik_artis = fiyatArtisHesapla("", tarihHesapla("year", -1), fon.getFon_kod());
        String uc_yillik_artis = fiyatArtisHesapla("", tarihHesapla("year", -3), fon.getFon_kod());
        String _2020_artis = fiyatArtisHesapla("2020-12-31", "2020-01-01", fon.getFon_kod());
        String _2021_artis = fiyatArtisHesapla("", "2021-01-01", fon.getFon_kod());
        fon.setGunluk_artis(gunluk_artis);
        if (!haftalik_artis.equals("0.00")) fon.setHaftalik_artis(haftalik_artis);
        if (!aylik_artis.equals("0.00")) fon.setAylik_artis(aylik_artis);
        if (!uc_aylik_artis.equals("0.00")) fon.setUc_aylik_artis(uc_aylik_artis);
        if (!alti_aylik_artis.equals("0.00")) fon.setAlti_aylik_artis(alti_aylik_artis);
        if (!yillik_artis.equals("0.00")) fon.setYillik_artis(yillik_artis);
        if (!uc_yillik_artis.equals("0.00")) fon.setUc_yillik_artis(uc_yillik_artis);
        if (StringUtils.isEmpty(fon.get_2017()))
            fon.set_2017(fiyatArtisHesapla("2017-12-31", "2017-01-01", fon.getFon_kod()));
        if (StringUtils.isEmpty(fon.get_2018()))
            fon.set_2018(fiyatArtisHesapla("2018-12-31", "2018-01-01", fon.getFon_kod()));
        if (StringUtils.isEmpty(fon.get_2019()))
            fon.set_2019(fiyatArtisHesapla("2019-12-31", "2019-01-01", fon.getFon_kod()));
        if (!_2020_artis.equals("0.00")) fon.set_2020(_2020_artis);
        if (!_2021_artis.equals("0.00")) fon.set_2021(_2021_artis);


        return fon;
    }

    public static List<LineChart> LineChartData(String datetip, String fonkod) {
        RestTemplate rest = new RestTemplate();
        List<LineChart> chartList = new ArrayList<LineChart>();
        String bugun = tarihHesapla("day", 0);
        String gecmis_tarih = "";
        switch (datetip) {
            case "year":
                gecmis_tarih = tarihHesapla("year", -1);
                break;
            case "month":
                gecmis_tarih = tarihHesapla("month", -1);
                break;
            case "week":
                gecmis_tarih = tarihHesapla("day", -7);
                break;
            case "sixmonths":
                gecmis_tarih = tarihHesapla("month", -6);
                break;
            case "threemonths":
                gecmis_tarih = tarihHesapla("month", -3);
                break;
            case "threeyears":
                gecmis_tarih = tarihHesapla("year", -3);
                break;
        }
        String url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + gecmis_tarih + "/" + bugun;
        String json = rest.getForObject(url, String.class);
        JsonArray veri = new JsonParser().parse(json).getAsJsonArray();

        for (int i = 0; i < veri.size(); i++) {
            chartList.add(new LineChart(veri.get(i).getAsJsonObject().get("Tarih").getAsString().substring(0, 10), veri.get(i).getAsJsonObject().get("BirimPayDegeri").getAsDouble()));
        }

        return chartList;
    }

    public static List<PieChart> PieChartDetay(FonDetay fondetay) {
        List<PieChart> pie_list = new ArrayList<PieChart>();
        if (!fondetay.getYab_hisse_senedi().equals("0.00"))
            pie_list.add(new PieChart("Yabanc?? Hisse Senedi", Double.valueOf(fondetay.getYab_hisse_senedi())));
        if (!fondetay.getHisse_senedi().equals("0.00"))
            pie_list.add(new PieChart("Hisse Senedi", Double.valueOf(fondetay.getHisse_senedi())));
        if (!fondetay.getDevlet_tahvili().equals("0.00"))
            pie_list.add(new PieChart("Devlet Tahvili", Double.valueOf(fondetay.getDevlet_tahvili())));
        if (!fondetay.getBanka_bonosu().equals("0.00"))
            pie_list.add(new PieChart("Banka Bonosu", Double.valueOf(fondetay.getBanka_bonosu())));
        if (!fondetay.getEurobond().equals("0.00"))
            pie_list.add(new PieChart("Eurobond", Double.valueOf(fondetay.getEurobond())));
        if (!fondetay.getKiymetli_maden().equals("0.00"))
            pie_list.add(new PieChart("K??ymetli Maden", Double.valueOf(fondetay.getKiymetli_maden())));
        if (!fondetay.getDiger().equals("0.00"))
            pie_list.add(new PieChart("Di??er", Double.valueOf(fondetay.getDiger())));
        if (!fondetay.getDoviz_odemeli_bono().equals("0.00"))
            pie_list.add(new PieChart("D??viz ??demeli Bono", Double.valueOf(fondetay.getDoviz_odemeli_bono())));
        if (!fondetay.getDoviz_odemeli_tahvil().equals("0.00"))
            pie_list.add(new PieChart("D??viz ??demeli Tahvil", Double.valueOf(fondetay.getDoviz_odemeli_tahvil())));
        if (!fondetay.getFinansman_bonosu().equals("0.00"))
            pie_list.add(new PieChart("Finansman Bonosu", Double.valueOf(fondetay.getFinansman_bonosu())));
        if (!fondetay.getFon_katilma_belgesi().equals("0.00"))
            pie_list.add(new PieChart("Fon Kat??lma Belgesi", Double.valueOf(fondetay.getFon_katilma_belgesi())));
        if (!fondetay.getGayrimenkul_sertifikasi().equals("0.00"))
            pie_list.add(new PieChart("Gayrimenkul Sertifikas??", Double.valueOf(fondetay.getGayrimenkul_sertifikasi())));
        if (!fondetay.getHazine_bonosu().equals("0.00"))
            pie_list.add(new PieChart("Hazine Bonosu", Double.valueOf(fondetay.getHazine_bonosu())));
        if (!fondetay.getKamu_dis_borclanma_araci().equals("0.00"))
            pie_list.add(new PieChart("Kamu D???? Bor??lanma Arac??", Double.valueOf(fondetay.getKamu_dis_borclanma_araci())));
        if (!fondetay.getKamu_kira_sertifikas??().equals("0.00"))
            pie_list.add(new PieChart("Kamu Kira Sertifikas??", Double.valueOf(fondetay.getKamu_kira_sertifikas??())));
        if (!fondetay.getKatilim_hesabi().equals("0.00"))
            pie_list.add(new PieChart("Kat??l??m Hesab??", Double.valueOf(fondetay.getKatilim_hesabi())));
        if (!fondetay.getOzel_kira_sertifikasi().equals("0.00"))
            pie_list.add(new PieChart("??zel Sekt??r Kira Sertifikas??", Double.valueOf(fondetay.getOzel_kira_sertifikasi())));
        if (!fondetay.getOzel_sektor_tahvil().equals("0.00"))
            pie_list.add(new PieChart("??zel Sekt??r Tahvili", Double.valueOf(fondetay.getOzel_sektor_tahvil())));
        if (!fondetay.getTers_repo().equals("0.00"))
            pie_list.add(new PieChart("Ters Repo", Double.valueOf(fondetay.getTers_repo())));
        if (!fondetay.getTpp().equals("0.00")) pie_list.add(new PieChart("TPP", Double.valueOf(fondetay.getTpp())));
        if (!fondetay.getTurev_araci().equals("0.00"))
            pie_list.add(new PieChart("T??rev Arac??", Double.valueOf(fondetay.getTurev_araci())));
        if (!fondetay.getVarlik_menkul_kiymet().equals("0.00"))
            pie_list.add(new PieChart("Varl????a Dayal?? Menkul K??ymet", Double.valueOf(fondetay.getVarlik_menkul_kiymet())));
        if (!fondetay.getVadeli_mevduat().equals("0.00"))
            pie_list.add(new PieChart("Vadeli Mevduat", Double.valueOf(fondetay.getVadeli_mevduat())));
        if (!fondetay.getYab_borclanma_araci().equals("0.00"))
            pie_list.add(new PieChart("Yabanc?? Bor??lanma Arac??", Double.valueOf(fondetay.getYab_borclanma_araci())));
        if (!fondetay.getYab_menkul_kiymet().equals("0.00"))
            pie_list.add(new PieChart("Yabanci Menkul K??ymet", Double.valueOf(fondetay.getYab_menkul_kiymet())));

        return pie_list;
    }

    public void iceriAktarSingleFon(String fonkod) throws ParseException {

                RestTemplate rest = new RestTemplate();
                String bugun, url, json;

                if (fonDetayRepository.getUpdatedDate(fonkod).equals(tarihHesapla("day", 0))) {
                    return;
                }
                bugun = tarihHesapla("day", 0);
                url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/" + fonkod + "/1/" + bugun + "/" + bugun;

                json = rest.getForObject(url, String.class);
                if (json == null)
                    return;

                json = json.replace("[", "");
                json = json.replace("]", "");
                Double fiyat = new JsonParser().parse(json).getAsJsonObject().get("BirimPayDegeri").getAsDouble();
                if (fiyat == 0D)
                    return;
                JsonObject json_obje = new JsonParser().parse(json).getAsJsonObject();
                FonDetay fonDetay = new FonDetay();
                try {
                    fonDetay = atamaYap(json_obje);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Fon fon = fonRepository.findByKodu(fonDetay.getFon_kod());
                fon.setFiyat(fonDetay.getBirim_deger());
                fon.setGunluk_artis(fonDetay.getGunluk_artis());

                fonRepository.save(fon);
                fonDetayRepository.save(fonDetay);
                portfoyHesapla(fon.getKodu());

    }

    public void iceriSapmaveSharpeAktar() throws ParseException {
        List<FonDetay> fonlar = fonDetayRepository.findAll();
        String artis_yuzdesi, fonkod, s_sapma, sharpe;
        Double ort, toplam, farkinkareleri;
        int size;
        for (FonDetay fondetay : fonlar) {
            toplam = 0D;
            farkinkareleri = 0D;
            List<Double> fiyatlar = new ArrayList<Double>();
            fonkod = fondetay.getFon_kod();
            if (!returnGunFarki(fondetay.getSharpe_tarih()))
                continue;

            for (int i = 1; i < 47; i++) {
                artis_yuzdesi = fiyatArtisHesapla(tarihHesapla("day", -((i) - 1)), tarihHesapla("day", -(i)), fonkod);
                if (!artis_yuzdesi.equals("0.00") && !StringUtils.isEmpty(artis_yuzdesi))
                    fiyatlar.add(Double.valueOf(artis_yuzdesi));

                if (fiyatlar.size() >= 30)
                    break;
            }
            size = fiyatlar.size();
            for (int i = 0; i < size; i++) {
                toplam += fiyatlar.get(i);
            }
            if (toplam == 0)
                continue;
            ort = toplam / size;
            for (int i = 0; i < size; i++) {
                farkinkareleri += Math.pow(fiyatlar.get(i) - ort, 2);
            }
            farkinkareleri = Double.valueOf((String.valueOf(farkinkareleri) + "000").substring(0, 6));

            s_sapma = String.valueOf(Math.sqrt(farkinkareleri / (size - 1)) + "00").substring(0, 4);
            if (s_sapma.equals("0.00"))
                sharpe = "0.00";
            else
                sharpe = String.valueOf((ort - 0.018) / Double.valueOf(s_sapma) + "00");

            fondetay.setStandart_sapma(s_sapma);
            fondetay.setSharpe_ratio(sharpe.substring(0, sharpe.indexOf(".") + 3));
            fondetay.setSharpe_guncel(true);
            fondetay.setSharpe_tarih(tarihHesapla("day", 0));
            fonDetayRepository.save(fondetay);
            System.out.println(fondetay.getFon_kod() + " guncellendi!!" + " s.sapma: " + s_sapma + " sharpe:" + fondetay.getSharpe_ratio());
        }
    }

    public void iceriYeniEklenenFonlar??Aktar() throws ParseException {
        RestTemplate rest = new RestTemplate();
        List<FonDetay> fondetayList = new ArrayList();
        List<String> fonisimList = new ArrayList();
        List<Fon> list = new ArrayList();
        String bugun = tarihHesapla("day", 0);

        String url = "https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/1/" + bugun + "/" + bugun;
        String url2 = "httpS://ws.spk.gov.tr/PortfolioValues/api/Funds/1";
        String json = rest.getForObject(url, String.class);
        String json2 = rest.getForObject(url2, String.class);

        JsonArray veri = new JsonParser().parse(json).getAsJsonArray();
        JsonArray veri2 = new JsonParser().parse(json2).getAsJsonArray();

        for (int i = 0; i < veri2.size(); i++) {
            if (veri2.get(i).getAsJsonObject().get("Tipi").getAsString().equals("F"))
                fonisimList.add(veri2.get(i).getAsJsonObject().get("Kodu").getAsString());
        }

        String fon_kod, fon_turu, fon_ad;
        for (int i = 0; i < veri.size(); i++) {
            fon_kod = veri.get(i).getAsJsonObject().get("FonKodu").getAsString();
            fon_turu = veri.get(i).getAsJsonObject().get("FonTuru").getAsString();
            fon_ad = veri.get(i).getAsJsonObject().get("FonUnvani").getAsString();

            if (!StringUtils.isEmpty(fonRepository.findByKodu(fon_kod)))
                continue;
            else {
                if (fonisimList.contains(fon_kod) && !fon_ad.contains("KORUMA AMA??LI") &&
                        !fon_ad.contains("SERBEST FON") &
                                !fon_ad.contains("SERBEST FONU") &&
                        !fon_ad.contains("KUMBARA") &&
                        !fon_ad.contains("??ZEL FON") &&
                        !fon_ad.contains("??ZEL FONU") &&
                        !fon_ad.contains("PARA P??YASASI")) {
                    list.add(new Fon(fon_kod, fon_ad));
                    //fondetayList.add(atamaYap(veri.get(i).getAsJsonObject()));
                }
            }

        }

        System.out.println(list);
    }

//    MINE              FONBL
// sua 0.08 - 0.41 || 0.16 - 0.50
// aes 2.51 - 0.23 || 3.66 - -0.07
// AFT 1.53 - 0.41 || 2.14 - 0.10
// TCD 1.43 - 0.24 || 1.90 - 0.15
}
