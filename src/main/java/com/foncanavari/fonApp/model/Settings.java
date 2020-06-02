package com.foncanavari.fonApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "settings")
@NoArgsConstructor
public class Settings {
    @Id
    int id=1;
    String title;
    String footer_text;
    String email;
    String anahtar_kelime;
    String aciklama;
    String yayin_tarihi = "10/01/2020";

}
