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
        return false;
    }

    @Override
    public List<String> getPpnFromIdentifiant(String issn) {
        return caller.issnToPpn(issn);
    }

}
