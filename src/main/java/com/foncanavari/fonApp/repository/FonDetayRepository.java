package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.model.PortFon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Repository
public interface FonDetayRepository extends JpaRepository<FonDetay, Integer> {

    @Query(value = "select count(*) from fondetay where g_tarih = ?1", nativeQuery = true)
    int getGuncellenenFonSayisi(String tarih);

    @Query(value = "select * from fondetay where g_tarih <> ?1 limit 10", nativeQuery = true)
    List<FonDetay> getFonListNotUpdated(String tarih);

    @Query(value = "select count(*) from fondetay where CAST(?1 as date) - CAST(sharpe_tarih as date) > 35 limit 10", nativeQuery = true)
    int getCountSharpeListNotUpdated(String tarih);

    @Query(value = "select * from fondetay where fon_kod = ?1", nativeQuery = true)
    FonDetay getByKod(String kod);

    @Query(value = "select * from fondetay where fon_kod like %?1% or fon_ad like %?1%", nativeQuery = true)
    List<FonDetay> findAllLike(String search);

    @Query(value = "select * from fondetay where category = ?1", nativeQuery = true)
    List<FonDetay> findByCategory(String category);

    @Query(value = "select g_tarih from fondetay where fon_kod = ?1", nativeQuery = true)
    String getUpdatedDate(String kod);

    @Query(value = "select max(CAST(gunluk_artis as float)) as gunluk_artis, category,(select fon_kod from fondetay where gunluk_artis =  max(CAST(fn.gunluk_artis as float)) and category=fn.category limit 1) fon_kod from fondetay fn group by category", nativeQuery = true)
    List<?> getEniyilerFonGunluk();

    @Query(value = "select max(CAST(haftalik_artis as float)) as haftalik_artis, category,(select fon_kod from fondetay where haftalik_artis =  max(CAST(fn.haftalik_artis as float)) and category=fn.category limit 1) fon_kod from fondetay fn group by category", nativeQuery = true)
    List<?> getEniyilerFonHaftalik();

    @Query(value = "select max(CAST(aylik_artis as float)) as aylik_artis, category,(select fon_kod from fondetay where aylik_artis =  max(CAST(fn.aylik_artis as float)) and category=fn.category limit 1) fon_kod from fondetay fn group by category", nativeQuery = true)
    List<?> getEniyilerFonAylik();

    @Query(value = "select max(CAST(alti_aylik_artis as float)) as alti_aylik_artis, category,(select fon_kod from fondetay where alti_aylik_artis =  max(CAST(fn.alti_aylik_artis as float)) and category=fn.category limit 1) fon_kod from fondetay fn group by category", nativeQuery = true)
    List<?> getEniyilerFonAltiAylik();

    @Query(value = "select max(CAST(yillik_artis as float)) as yillik_artis, category,(select fon_kod from fondetay where yillik_artis =  max(CAST(fn.yillik_artis as float)) and category=fn.category limit 1) fon_kod from fondetay fn group by category", nativeQuery = true)
    List<?> getEniyilerFonYillik();

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2017 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2017(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2017 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2017(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2018 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2018(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2018 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2018(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2019 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2019(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2019 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2019(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2020 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2020(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2020 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2020(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofAylikArtis(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofAylikArtis(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(standart_sapma as float) desc, CAST(sharpe_ratio as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofSapma(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(standart_sapma as float) asc, CAST(sharpe_ratio as float) desc", nativeQuery = true)
    List<FonDetay> getAscSortedListofSapma(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(sharpe_ratio as float) desc, CAST(standart_sapma as float) asc", nativeQuery = true)
    List<FonDetay> getDescSortedListofSharpe(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(sharpe_ratio as float) asc, CAST(standart_sapma as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofSharpe(String category);

    @Query(value = "select * from fondetay order by CAST(_2017 as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof2017TUM();

    @Query(value = "select * from fondetay order by CAST(_2017 as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof2017TUM();

    @Query(value = "select * from fondetay order by CAST(_2018 as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof2018TUM();

    @Query(value = "select * from fondetay order by CAST(_2018 as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof2018TUM();

    @Query(value = "select * from fondetay order by CAST(_2019 as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof2019TUM();

    @Query(value = "select * from fondetay order by CAST(_2019 as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof2019TUM();

    @Query(value = "select * from fondetay order by CAST(_2020 as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof2020TUM();

    @Query(value = "select * from fondetay order by CAST(_2020 as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof2020TUM();

    @Query(value = "select * from fondetay order by CAST(aylik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListofAylikArtisTUM();

    @Query(value = "select * from fondetay order by CAST(aylik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListofAylikArtisTUM();

    @Query(value = "select * from fondetay order by CAST(standart_sapma as float) desc, CAST(sharpe_ratio as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListofSapmaTUM();

    @Query(value = "select * from fondetay order by CAST(standart_sapma as float) asc, CAST(sharpe_ratio as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListofSapmaTUM();

    @Query(value = "select * from fondetay order by CAST(sharpe_ratio as float) desc, CAST(standart_sapma as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListofSharpeTUM();

    @Query(value = "select * from fondetay order by CAST(sharpe_ratio as float) asc, CAST(standart_sapma as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListofSharpeTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2017 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2017FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2017 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2017FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2018 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2018FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2018 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2018FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2019 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2019FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2019 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2019FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2020 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof2020FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2020 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof2020FAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofAylikArtisFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofAylikArtisFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(standart_sapma as float) desc, CAST(sharpe_ratio as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofSapmaFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(standart_sapma as float) asc, CAST(sharpe_ratio as float) desc", nativeQuery = true)
    List<FonDetay> getAscSortedListofSapmaFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(sharpe_ratio as float) desc, CAST(standart_sapma as float) asc", nativeQuery = true)
    List<FonDetay> getDescSortedListofSharpeFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(sharpe_ratio as float) asc, CAST(standart_sapma as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofSharpeFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay order by CAST(aylik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDetaylarForKarsilastir();

    @Query(value = "select * from fondetay where length(sharpe_ratio) < 4", nativeQuery = true)
    List<FonDetay> herre();

    @Query(value = "select * from fondetay where category = ?1 order by CAST(haftalik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofHafta(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(haftalik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofHafta(String category);

    @Query(value = "select * from fondetay order by CAST(haftalik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListofHaftaTUM();

    @Query(value = "select * from fondetay order by CAST(haftalik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListofHaftaTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(haftalik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofHaftaFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(haftalik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofHaftaFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(uc_aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof3Ay(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(uc_aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof3Ay(String category);

    @Query(value = "select * from fondetay order by CAST(uc_aylik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof3AyTUM();

    @Query(value = "select * from fondetay order by CAST(uc_aylik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof3AyTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(uc_aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof3AyFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(uc_aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof3AyFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(alti_aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof6Ay(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(alti_aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof6Ay(String category);

    @Query(value = "select * from fondetay order by CAST(alti_aylik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof6AyTUM();

    @Query(value = "select * from fondetay order by CAST(alti_aylik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof6AyTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(alti_aylik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof6AyFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(alti_aylik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof6AyFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2021 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofYilbasi(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(_2021 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofYilbasi(String category);

    @Query(value = "select * from fondetay order by CAST(_2021 as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListofYilbasiTUM();

    @Query(value = "select * from fondetay order by CAST(_2021 as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListofYilbasiTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2021 as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListofYilbasiFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(_2021 as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListofYilbasiFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(yillik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof1Yil(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(yillik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof1Yil(String category);

    @Query(value = "select * from fondetay order by CAST(yillik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof1YilTUM();

    @Query(value = "select * from fondetay order by CAST(yillik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof1YilTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(yillik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof1YilFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(yillik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof1YilFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(uc_yillik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof3Yil(String category);

    @Query(value = "select * from fondetay where category = ?1 order by CAST(uc_yillik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof3Yil(String category);

    @Query(value = "select * from fondetay order by CAST(uc_yillik_artis as float) desc limit 20", nativeQuery = true)
    List<FonDetay> getDescSortedListof3YilTUM();

    @Query(value = "select * from fondetay order by CAST(uc_yillik_artis as float) asc limit 20", nativeQuery = true)
    List<FonDetay> getAscSortedListof3YilTUM();

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(uc_yillik_artis as float) desc", nativeQuery = true)
    List<FonDetay> getDescSortedListof3YilFAV(List<String> fon_kodlar);

    @Query(value = "select * from fondetay where fon_kod in ?1 order by CAST(uc_yillik_artis as float) asc", nativeQuery = true)
    List<FonDetay> getAscSortedListof3YilFAV(List<String> fon_kodlar);
}
