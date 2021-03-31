package com.foncanavari.fonApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@Table(name = "portfoy_performans")
@NoArgsConstructor
@AllArgsConstructor
public class PortfoyPerformans {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    int id;
    String tarih;
    String portfoy_degeri;
    @JsonIgnore
    int portfoy_id;
}
