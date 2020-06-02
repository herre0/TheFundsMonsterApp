package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "userlog")
@NoArgsConstructor
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    int user_id;
    Date y_tarih;
    Date son_login;
    Double saat_aktif;//sistemde ne kdr vakit gecirmis
    String ip_adress;
    String tarayici;
    String isletim_sistemi;

}
