package com.foncanavari.fonApp.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "portfoy")
@NoArgsConstructor
public class Portfoy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String adi;
    @OneToMany(cascade = {CascadeType.ALL}, fetch=FetchType.EAGER)
    List<PortFon> fonlar;
    int user_id;
    Double portfoy_degeri;
    Double gunluk_getiri_tl;
    Double gunluk_getiri_yuzde;
    Double toplam_getiri_tl;
    Double toplam_getiri_yuzde;
    String g_tarih;
    String y_tarih;

}
