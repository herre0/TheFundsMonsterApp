package com.foncanavari.fonApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@Table(name = "life")
@NoArgsConstructor
@AllArgsConstructor
public class Life {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String tarih="";
    int giris_sayisi=0;

}
