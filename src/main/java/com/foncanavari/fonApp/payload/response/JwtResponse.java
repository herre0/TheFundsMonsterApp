package com.foncanavari.fonApp.payload.response;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.PortFon;
import com.foncanavari.fonApp.model.Portfoy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int id;
    private String username;
    private String adsoyad;
    private List<String> roles;
    private List<Fon> favori_fonlar;
    private List<Portfoy> portfoyler;

    public JwtResponse(String accessToken, int id, String username, String adsoyad, List<String> roles, List<Fon> favori_fonlar, List<Portfoy> portfoyler) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.adsoyad = adsoyad;
        this.roles = roles;
        this.favori_fonlar = favori_fonlar;
        this.portfoyler = portfoyler;
    }
}
