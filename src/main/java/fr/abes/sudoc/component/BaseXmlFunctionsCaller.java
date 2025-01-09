package fr.abes.sudoc.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BaseXmlFunctionsCaller {
    private final JdbcTemplate baseXmlJdbcTemplate;

    public BaseXmlFunctionsCaller(JdbcTemplate baseXmlJdbcTemplate) {
        this.baseXmlJdbcTemplate = baseXmlJdbcTemplate;
    }


    public List<String> issnToPpn(String issn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='ISSN' and cle2='");
        request.append(issn);
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }


    public List<String> isbnToPpn(String isbn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='ISBN' and cle2='");
        request.append(isbn);
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }


    public List<String> doiToPpn(String doi) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='DOI' and cle2='");
        request.append(doi.toLowerCase());
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }

    public List<String> datToPpn(Integer date, String auteur, String titre) throws UncategorizedSQLException {
        if (titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        log.debug("params : date : " + date + " auteur : " + auteur + " titre : " + titre);
        StringBuilder request = new StringBuilder("SELECT DISTINCT a.PPN");
        if (date != null) {
            request.append(" FROM AUTORITES.biblio_table_generale a");
            request.append(" JOIN AUTORITES.bib_100$a b");
            request.append(" ON a.ppn=b.ppn");
            if (auteur != null) {
                request.append(" WHERE  a.typerecord != 'd' and a.typecontrol = 'm' and CONTAINS(a.citation1, '(").append(titre).append(") AND (").append(auteur).append(")')>0 and (substr(b.datas,10,4) = '").append(date).append("' or substr(b.datas,14,4)='").append(date).append("')");
            } else {
                request.append(" WHERE  a.typerecord != 'd' and a.typecontrol = 'm' and CONTAINS(a.citation1, '(").append(titre).append(")')>0 and (substr(b.datas,10,4) = '").append(date).append("' or substr(b.datas,14,4)='").append(date).append("')");
            }
        } else {
            request.append(" FROM biblio_table_generale a");
            if (auteur != null) {
                request.append(" WHERE  a.typerecord != 'd' and a.biblevel='l' and a.typecontrol = 'm' and CONTAINS(a.citation1, '(").append(titre).append(") AND (").append(auteur).append(")')>0");
            } else {
                request.append(" WHERE  a.typerecord != 'd' and a.biblevel='l' and a.typecontrol = 'm' and CONTAINS(a.citation1, '(").append(titre).append(")')>0");
            }
        }
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }
}
