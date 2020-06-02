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
    @OneToMany
    List<Fon> fonlar;
    int user_id;
    Double deger;


}
