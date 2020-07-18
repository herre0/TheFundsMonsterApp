package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "traffic")
@NoArgsConstructor
public class Traffic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String tarih;
    String ip_adress;
    int giris_sayisi=0;
}
