package fr.abes.convergence.kbartws.web;

import fr.abes.convergence.kbartws.entity.RowKbart;
import fr.abes.convergence.kbartws.service.Isbn2ppnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class KbartController {
    @Autowired
    Isbn2ppnService isbn2ppnService;

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
            //TODO getService.isbn2ppn(this.normalizeOnlineIdentifier(onlineIdentifier))
            //isbn2ppnService.getNoticeXml(this.normalizeOnlineIdentifier(onlineIdentifier));
        }else if(publicationType.equals("serial")) {

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

    /**
     * Supprimera tous les caractères différents de 0-9, X, x. Supprimera le - d'un issn.
     * @param onlineIdentifier identifiant reçu en entrée, isbn ou issn
     * @return un identifiant ne pouvant comporter que des chiffres, X, x
     */
    private String normalizeOnlineIdentifier(String onlineIdentifier){
        return onlineIdentifier.replaceAll("[^\\d|X|x]", "");
    }
}
