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

    public static void main(String[] args) throws ParseException {
        ApplicationContext applicationContext = SpringApplication.run(FonAppApplication.class, args);
        FonDetayServis fonDetayServis = applicationContext.getBean(FonDetayServis.class);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","{}");

       //fonDetayServis.iceriSapmaveSharpeAktar();

        final ScheduledFuture<?> taskHandle1 = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            if (fonDetayServis.kacaklariYakala()) {
                                System.out.println("iceri aktariliyor .. 1");
                                fonDetayServis.iceriAktar();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 30, 30, TimeUnit.HOURS);

        final ScheduledFuture<?> taskHandle2 = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                            if (fonDetayServis.kacaklariYakala()) {
                                System.out.println("iceri aktariliyor .. 2");
                                fonDetayServis.iceriAktar();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 30, 30, TimeUnit.HOURS);



    }



}
