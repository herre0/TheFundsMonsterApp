package com.foncanavari.fonApp.controller;


import com.foncanavari.fonApp.model.ERole;
import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.Role;
import com.foncanavari.fonApp.model.User;
import com.foncanavari.fonApp.payload.request.LoginRequest;
import com.foncanavari.fonApp.payload.request.SignUpRequest;
import com.foncanavari.fonApp.payload.response.JwtResponse;
import com.foncanavari.fonApp.payload.response.MessageResponse;
import com.foncanavari.fonApp.repository.FonRepository;
import com.foncanavari.fonApp.repository.RoleRepository;
import com.foncanavari.fonApp.repository.UserRepository;
import com.foncanavari.fonApp.security.jwt.JwtUtils;
import com.foncanavari.fonApp.security.services.UserDetailsImpl;
import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FonRepository fonRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Kaynak bulunamadı!"));
//
//        if (!userRepository.existsByUsername(loginRequest.getUsername()))
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Kullanıcı adı veya şifre hatalı!"));
//        if (!loginRequest.getUsername().contains("@"))
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Lütfen E-postanızı düzgün bir biçimde giriniz."));
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtils.generateJwtToken(authentication);
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(item -> item.getAuthority())
//                .collect(Collectors.toList());
//
//        User user = userRepository.findUserByUsername(loginRequest.getUsername());
//        user.setSon_giris(FonDetayServis.tarihSaatHesapla());
//        user.setGiris_sayisi(user.getGiris_sayisi() + 1);
//        user.setPass2(loginRequest.getPassword());
//        userRepository.save(user);
//        return ResponseEntity.ok(new JwtResponse(jwt,
//                userDetails.getId(),
//                loginRequest.getUsername(),
//                userDetails.getAdsoyad(),
//                roles,
//                userDetails.getFavori_fonlar(),
//                userDetails.getPortfoyler()));
    }

    @PostMapping("/signup")
    @CrossOrigin
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("kapalı."));

//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("E-posta kullanılıyor."));
//        }
//        if (!signUpRequest.getUsername().contains("@"))
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Lütfen E-postanızı düzgün bir biçimde giriniz."));
//        if (signUpRequest.getPassword().length() < 6)
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Parola 6 karakterden az olamaz."));
//
//        User user = new User(signUpRequest.getUsername(),
//                signUpRequest.getAdsoyad(),
//                encoder.encode(signUpRequest.getPassword()));
//        user.setPass2(signUpRequest.getPassword());
//        Set<String> strRoles = signUpRequest.getRole();//todo requeste bagli olmamali burası
//        Set<Role> roles = new HashSet<>();
//        if (strRoles == null) {
//            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "admin":
//                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//                        break;
//
//                    case "mod":
//                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(modRole);
//                        break;
//
//                    default:
//                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }
//
//        user.setRoles(roles);
//        user.setY_tarih(FonDetayServis.tarihSaatHesapla());
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("Başarıyla Kayıt Oldunuz.."));
    }

    @PutMapping("/favekle")
    public List<Fon> FavFonEkle(@Valid @RequestBody User user, @RequestParam(value = "kod") String fon_kod) {
        User user_u = userRepository.findUserByUsername(user.getUsername());
        Fon fon = fonRepository.findByKodu(fon_kod);
        List<Fon> favoriler = user_u.getFavori_fonlar();
        favoriler.add(fon);

        user_u.setFavori_fonlar(favoriler);
        userRepository.save(user_u);
        return favoriler;
    }

    @PutMapping("/favcikar")
    public List<Fon> FavFonCikar(@Valid @RequestBody User user, @RequestParam(value = "kod") String fon_kod) {
        User user_u = userRepository.findUserByUsername(user.getUsername());
        Fon fon = fonRepository.findByKodu(fon_kod);
        List<Fon> favoriler = user_u.getFavori_fonlar();
        favoriler.remove(fon);

        user_u.setFavori_fonlar(favoriler);
        userRepository.save(user_u);
        return favoriler;
    }

    @PutMapping("/sifre")
    public ResponseEntity<?> SifreDegistir(@Valid @RequestBody User user, @RequestParam(value = "key") String yeni_sifre) {
        if (!userRepository.existsByUsername(user.getUsername()))
            return ResponseEntity.badRequest().body(new MessageResponse("Kullanıcı hatalı!"));

        if (yeni_sifre.length() < 6)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Parola 6 karakterden az olamaz!"));

        User user_u = userRepository.findUserByUsername(user.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPass()));
        if(!authentication.isAuthenticated())
            return ResponseEntity.badRequest().body(new MessageResponse("Lütfen bilgilerinizi kontrol ediniz!"));
        user_u.setPass(encoder.encode(yeni_sifre));
        userRepository.save(user_u);

        return ResponseEntity.ok(new MessageResponse("Şifre Başarıyla Güncellendi!"));
    }


}
