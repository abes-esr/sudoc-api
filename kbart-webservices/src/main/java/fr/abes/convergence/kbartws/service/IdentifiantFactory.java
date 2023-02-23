package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.utils.TYPE_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifiantFactory {
    @Autowired
    private IssnService issnService;

    @Autowired
    private IsbnService isbnService;

    public IIdentifiantService getService(TYPE_ID type) {
        return switch (type) {
            case ISBN -> isbnService;
            case ISSN -> issnService;
        };
    }

}
