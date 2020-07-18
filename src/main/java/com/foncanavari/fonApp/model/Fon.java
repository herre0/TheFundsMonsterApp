package com.foncanavari.fonApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "fon")
@NoArgsConstructor
@AllArgsConstructor
public class Fon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @Column(name = "kod",length = 3)
    String kodu;
    @Column(name = "ad")
    String adi;
    String category;
    String gunluk_artis;
    String fiyat;
}









