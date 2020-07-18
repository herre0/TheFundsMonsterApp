package com.foncanavari.fonApp.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.PortFon;
import com.foncanavari.fonApp.model.Portfoy;
import com.foncanavari.fonApp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private int id;

    private String adsoyad;

    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private List<Fon> favori_fonlar;

    private List<Portfoy> portfoyler;

    public UserDetailsImpl(int id, String adsoyad, String username, String password,
                           Collection<? extends GrantedAuthority> authorities, List<Fon> favori_fonlar, List<Portfoy> portfoyler) {
        this.id = id;
        this.adsoyad = adsoyad;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.favori_fonlar = favori_fonlar;
        this.portfoyler = portfoyler;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getAdsoyad(),
                user.getUsername(),
                user.getPass(),
                authorities,
                user.getFavori_fonlar(),
                user.getPortfoyler());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }


}
