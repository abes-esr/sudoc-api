package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IssnService extends AbstractService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IssnService(BaseXmlFunctionsCaller caller, NoticeService noticeService, ProviderService providerService) {
        super(noticeService, providerService);
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String issn) {
        return issn != null && issn.matches("^[0-9]{4}-?[0-9]{3}[0-9xX]$");
    }

    @Override
    public List<PpnWithTypeWebDto> getPpnFromIdentifiant(String issn, Optional<ElementDto> provider) throws IOException, IllegalPpnException {
        try{
            List<PpnWithTypeWebDto> resultat = new ArrayList<>();
            String result = caller.isbnToPpn(issn);
            List<String> ppns = Utilitaire.parseJson(result);
            for (String ppn : ppns) {
                log.debug("printIdentifier n° {} <-> ppn n° {}", issn, ppn);
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
