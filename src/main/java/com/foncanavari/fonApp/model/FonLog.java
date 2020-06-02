package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "fonlog")
@NoArgsConstructor
public class FonLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    int fon_id;
    int fon_sayisi;
    int g_tarih;
    Boolean updated;

}
