package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.ResultWsDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IsbnService extends AbstractService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IsbnService(BaseXmlFunctionsCaller caller, NoticeService noticeService, ProviderService providerService) {
        super(noticeService, providerService);
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String isbn) {
        //regexp permettant de vérifier
        //ISBN 10 et 13
        //avec ou sans tiret
        //avec prise en compte du caractère de controle X en fin
        return isbn != null && isbn.matches("^(?:ISBN(?:-1[03])?:?\\s*)?(?=[-0-9X\\s]{10,17}$)(?:97[89][- ]?)?([0-9]{1,5})[- ]?([0-9]+)[- ]?([0-9]+)[- ]?([0-9X])$");
    }

    @Override
    public List<PpnWithTypeWebDto> getPpnFromIdentifiant(String isbn, Optional<ElementDto> provider) throws IOException, IllegalPpnException {
        try{
            List<PpnWithTypeWebDto> resultat = new ArrayList<>();
            String result = caller.isbnToPpn(isbn);
            List<String> ppns = Utilitaire.parseJson(result);
            for (String ppn : ppns) {
                log.debug("onlineIdentifier n° {} <-> ppn n° {}", isbn, ppn);
                resultat.add(feedResultatWithNotice(provider, ppn));
            }
            return resultat;
        } catch (UncategorizedSQLException ex) {
            if (ex.getMessage().contains("no ppn matched")) {
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
            }
            throw new IOException("Incident technique lors de l'accès à la base de données");
        } catch (JsonProcessingException | ZoneNotFoundException ex) {
            throw new IOException("Impossible de récupérer les ppn correspondant à cet identifiant");
        }
    }
}
