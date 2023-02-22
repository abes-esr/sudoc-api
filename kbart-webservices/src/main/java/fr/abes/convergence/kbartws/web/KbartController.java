package fr.abes.convergence.kbartws.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class KbartController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World but in controller";
    }
}
