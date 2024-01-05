package fr.abes.sudoc.service;

import fr.abes.sudoc.utils.TYPE_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifiantFactory {
    @Autowired
    private IssnService issnService;

    @Autowired
    private IsbnService isbnService;

    @Autowired
    private DoiService doiService;

    public IIdentifiantService getService(TYPE_ID type) {
        return switch (type) {
            case ISBN -> isbnService;
            case ISSN -> issnService;
        };
    }

    public IIdentifiantService getDoiService() {
        return doiService;
    }

}
