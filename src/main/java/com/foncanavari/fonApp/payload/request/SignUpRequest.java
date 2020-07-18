package com.foncanavari.fonApp.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class SignUpRequest {


    private String username;


    private String adsoyad;

    private Set<String> role;

    private String password;
}
