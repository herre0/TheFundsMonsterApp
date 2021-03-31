package com.foncanavari.fonApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortFonJson {
    int id;
    String fon_kod;
    String fon_ad;
    Double agirlik;
    Double degeri;
    Double alis_maliyeti;
    Double birim_fiyati;
    int adet;
    Double gunluk_getiri_tl;
    Double gunluk_getiri_yuzde;
    Double toplam_getiri_tl;
    Double toplam_getiri_yuzde;
}
