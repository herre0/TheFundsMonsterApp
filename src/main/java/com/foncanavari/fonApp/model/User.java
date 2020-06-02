package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "kullanici")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    int portfoy_sayisi = 0;//en fazla 3 hak
    @Column(name = "username", unique = true)
    String email;
    String pass;
    @OneToMany
    List<Fon> favori_fonlar=null;
    String user_type="USER"; // sonradan admin kullanıcı olusturamaz gibi vs.. ?


}
