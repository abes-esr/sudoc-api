package fr.abes.convergence.kbartws.web;

import fr.abes.convergence.kbartws.entity.RowKbart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class KbartController {

    @GetMapping("/online_identifier_2_ppn")
    public String onlineIdentifier2Ppn() {
        //test :
        RowKbart rowKbart = new RowKbart("9789004401501","monograph");

        // recuperation du champ online_identifier de rowKbart
        String onlineIdentifier = rowKbart.getOnline_identifier();

        // recuperation du champ publication_type de rowKbart
        String publicationType = rowKbart.getPublication_type();

        //SI publication_type == "monograph" ALORS isbn
        // Epurer l'online_identifier (isbn peut être codé sur 10 ou 13 caractères, et peut éventuellement contenir des -)
        if(publicationType.equals("monograph")) {
            onlineIdentifier = onlineIdentifier.replaceAll("-", "");
            if (!onlineIdentifier.matches("[^\\d|X|x|-]")){
                System.out.println("ERREUR"); //TODO gerer le cas où le isbn est pas bon
            }

        }

        //SI publication_type == "serial" ALORS issn
        // Epurer l'online_identifier (issn est codé sur 8 positions et peut contenir un - entre les positions 4 et 5)

        //appeler issn2ppn / isbn2ppn (retour = 0 ou N ppn)

        // si >0 ppn alors
        // recupere via un service les noticeXML
        // filtrer pour enlever les notices supp
        // verifier que chaque notice soit de type electronique (O en premier caractere de la 008)
        // (pour la lisibilité peut etre faire une fonction qui prend en parametre une notice et qui renvoi true ou false si elle est de type elec)
        // Si une notice est pas elec. throw une erreur.

        // renvoyer tout les ppns restant.
        return "Hello World but in controller";
    }
}
