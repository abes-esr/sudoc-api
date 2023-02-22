package fr.abes.convergence.kbartws.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Service
public class Isbn2ppnService {
    public Document getNoticeXml(String isbnNumber) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //TODO l'idée est de récuperer la notice, code ci-dessous a revoir
        Document doc = builder.parse(new File("https://www.sudoc.fr/services/isbn2ppn/" + isbnNumber));
        doc.getDocumentElement().normalize();
        return doc;
    }
}
