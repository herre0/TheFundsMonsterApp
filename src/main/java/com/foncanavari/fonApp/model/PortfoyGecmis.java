package com.foncanavari.fonApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@Table(name = "portfoy_gecmis")
@NoArgsConstructor
@AllArgsConstructor
public class PortfoyGecmis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String tarih;
    String islem;// EKLENDİ ÇIKARILDI PAY ALIŞ PAY SATIŞ
    String fon_kod;
    int fiyat_hareketi; // + 10000tl gibi buna yazılsa daha anlamlı olur,
    String toplam_kar_tl;
    String toplam_kar_yuzde;
    @JsonIgnore
    int portfoy_id;
}
