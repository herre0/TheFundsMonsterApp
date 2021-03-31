package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "fondetay")
@NoArgsConstructor
public class FonDetay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String fon_ad;
    String fon_kod;
    String category;
    String sharpe_ratio=""; // bu iksii daha sonra yapılabilir mesela ?
    String standart_sapma="";
    String gunluk_artis="";
    String haftalik_artis="";
    String aylik_artis="";
    String uc_aylik_artis="";
    String alti_aylik_artis="";
    String yillik_artis="";
    String uc_yillik_artis="";
    String _2017=""; // https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/AES/1/2017-01-01/2017-12-31
    String _2018=""; // https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/AES/1/2018-01-01/2018-12-31
    String _2019=""; // https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/AES/1/2019-01-01/2019-12-31
    String _2020=""; // https://ws.spk.gov.tr/PortfolioValues/api/PortfoyDegerleri/AES/1/2020-01-01/2020-12-31
    String _2021="";//yilbasi diye serve edilen
    String g_tarih="";
    String sharpe_tarih="";
    Boolean sharpe_guncel=false;
    // <- json dan geliyor ->
    String toplam_deger="";
    String birim_deger="";
    String pay_sayisi="";
    String yatirimci_sayisi="";
    // <- Fon yatırım profili ->


    String banka_bonosu="";
    String diger="";
    String devlet_tahvili="";
    String doviz_odemeli_bono="";
    String eurobond="";
    String doviz_odemeli_tahvil="";
    String finansman_bonosu="";
    String fon_katilma_belgesi="";
    String gayrimenkul_sertifikasi="";
    String hazine_bonosu="";
    String hisse_senedi="";
    String kamu_dis_borclanma_araci="";
    String kamu_kira_sertifikası="";
    String katilim_hesabi="";
    String kiymetli_maden="";
    String ozel_kira_sertifikasi="";
    String ozel_sektor_tahvil="";
    String ters_repo="";
    String tpp="";
    String turev_araci="";
    String varlik_menkul_kiymet="";
    String vadeli_mevduat="";
    String yab_borclanma_araci="";
    String yab_hisse_senedi="";
    String yab_menkul_kiymet="";


public FonDetay(String fon_kod, String fon_ad) {
    this.fon_kod = fon_kod;
    this.fon_ad = fon_ad;
}



}
