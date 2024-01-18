package fr.abes.sudoc;

import fr.abes.cbs.process.ProcessCBS;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;


@SpringBootApplication
public class SudocWebservicesApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.out.println("sudoc-webservices");
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));   // It will set UTC timezone
        SpringApplication.run(SudocWebservicesApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }

    @Bean
    public ProcessCBS initCbs() {
        return new ProcessCBS();
    }

}
