package fr.abes.convergence.kbartws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;


@SpringBootApplication
public class KbartWebservicesApplication implements CommandLineRunner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));   // It will set UTC timezone
        SpringApplication.run(KbartWebservicesApplication.class, args);
    }
    @Override
    public void run(String... args) {
    }

}
