package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "kullanici")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String adsoyad;
    @Column(name = "username", unique = true)
    String username;
    String pass;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany
    List<Fon> favori_fonlar = null;

    @OneToMany(cascade = {CascadeType.ALL})
    List<Portfoy> portfoyler = null;
    int portfoy_sayisi = 0; // todo 2-3 portfoy hakki

    String y_tarih; // todo bu tarz daha detay bilgiler eklenmeli
    String son_giris; // todo son giris tarihi yapsanaa
    int giris_sayisi=0;

    public User(String username, String adsoyad, String pass){
        this.username = username;
        this.adsoyad = adsoyad;
        this.pass = pass;
    }


}
