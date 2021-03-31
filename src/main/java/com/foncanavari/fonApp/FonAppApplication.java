package com.foncanavari.fonApp;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class FonAppApplication {

    static FonDetayServis  fonDetayServis;
    public static void main(String[] args) throws ParseException {
        ApplicationContext applicationContext = SpringApplication.run(FonAppApplication.class, args);
        fonDetayServis = applicationContext.getBean(FonDetayServis.class);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","{}");

        //fonDetayServis.iceriYeniEklenenFonlarıAktar();

//        final ScheduledFuture<?> taskHandle1 = scheduler.scheduleAtFixedRate(
//                new Runnable() {
//                    public void run() {
//                        try {
//                            if (fonDetayServis.kacaklariYakala()) {
//                                System.out.println("iceri aktariliyor .. 1");
//                                fonDetayServis.iceriAktar();
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }, 15, 15, TimeUnit.MINUTES);



    }

    public void iceriAktarForServisCall(String fon_kod) throws ParseException {
        fonDetayServis.iceriAktarSingleFon(fon_kod);
    }

    public void iceriAktarForServisCallAdmin() throws ParseException {
        fonDetayServis.iceriAktar();
    }

    public void portfoyHesaplattir(String fon_kod) {
        fonDetayServis.portfoyHesapla(fon_kod);
    }

    public void iceriSharpeAktar() throws ParseException{
        fonDetayServis.iceriSapmaveSharpeAktar();
    }

}
