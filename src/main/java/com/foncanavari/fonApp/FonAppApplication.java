package com.foncanavari.fonApp;

import com.foncanavari.fonApp.servis.FonDetayServis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

        final ScheduledFuture<?> taskHandle = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {
                           fonDetayServis.iceriAktar();
                        }catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 12, 12, TimeUnit.HOURS);
    }

}