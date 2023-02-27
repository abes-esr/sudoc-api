package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IssnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String issn) {
        if (issn != null && issn.length() >= 8) {
            if (issn.split("")[4].equals("-")){
                issn = issn.replace("-", "");
            }
            return issn.matches("(^\\d{8}$)|(^\\d{7}[xX]$)");
        }
        return false;
    }

    @Override
    public List<String> getPpnFromIdentifiant(String issn) {
        return caller.issnToPpn(issn);
    }

}
