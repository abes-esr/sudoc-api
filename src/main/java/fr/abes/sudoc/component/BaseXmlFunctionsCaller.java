package fr.abes.sudoc.component;

import fr.abes.sudoc.entity.NoticesBibio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class BaseXmlFunctionsCaller {

    private final DataSource dataSource;

    private static final String requestIssn = "SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='ISSN' and cle2=?";
    private static final String requestIsbn = "select AUTORITES.ISBN2PPNJSON(?) from dual";
    private static final String requestDoi = "SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='DOI' and cle2=?";

    public BaseXmlFunctionsCaller(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<String> issnToPpn(String issn) {
        List<String> resultList = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(requestIssn)
             ) {
            ps.setString(1, issn);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getString("ppn"));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }


    public String isbnToPpn(String isbn) throws UncategorizedSQLException {
        String result = null;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(requestIsbn)
        ) {
            ps.setString(1, isbn);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("ppn");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return result;
    }


    public List<String> doiToPpn(String doi) throws UncategorizedSQLException {
        List<String> resultList = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(requestDoi)
        ) {
            ps.setString(1, doi.toLowerCase());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getString("ppn"));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }

    public List<String> datToPpn(Integer date, String auteur, String titre) throws UncategorizedSQLException {
        if (titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        StringBuilder request = new StringBuilder("SELECT DISTINCT a.PPN as ppn");
        if (date != null) {
            request.append(" FROM AUTORITES.biblio_table_generale a");
            request.append(" JOIN AUTORITES.bib_100$a b");
            request.append(" ON a.ppn=b.ppn");
            if (auteur != null && !auteur.isEmpty()) {
                request.append(" WHERE  a.typerecord in ('c','n') and a.typecontrol = 'm' and (CONTAINS(a.citation1, '(").append(titre).append(") AND (").append(auteur).append(")',1)>0 and rownum < 10) and (substr(b.datas,10,4) = '").append(date).append("' or substr(b.datas,14,4)='").append(date).append("')");
            } else {
                request.append(" WHERE  a.typerecord in ('c','n') and a.biblevel='l' and a.typecontrol = 'm' and (CONTAINS(a.citation1, '(").append(titre).append(")',1)>0 and rownum < 10) and (substr(b.datas,10,4) = '").append(date).append("' or substr(b.datas,14,4)='").append(date).append("')");
            }
        } else {
            request.append(" FROM biblio_table_generale a");
            if (auteur != null && !auteur.isEmpty()) {
                request.append(" WHERE  a.typerecord in ('c','n') and a.typecontrol = 'm' and (CONTAINS(a.citation1, '(").append(titre).append(") AND (").append(auteur).append(")',1)>0  and rownum < 10)");
            } else {
                request.append(" WHERE  a.typerecord in ('c','n') and a.biblevel='l' and a.typecontrol = 'm' and (CONTAINS(a.citation1, '(").append(titre).append(")',1)>0  and rownum < 10)");
            }
        }

        List<String> resultList = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(request.toString())
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getString("ppn"));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }

    public Optional<NoticesBibio> findByPpn(String ppn){
        //@ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT id, XMLSERIALIZE (CONTENT data_xml as CLOB) as xmlclob FROM NoticesBibio WHERE ppn=?")
        ) {
           ps.setString(1, ppn);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               NoticesBibio noticesBibio = new NoticesBibio();
               noticesBibio.setId(rs.getInt("id"));
               noticesBibio.setPpn(ppn);
               noticesBibio.setDataXml(
                       "<record>\n" +
                               "  <leader>     clm0 22        450 </leader>\n" +
                               "  <controlfield tag=\"001\">233425454</controlfield>\n" +
                               "  <controlfield tag=\"003\">https://www.sudoc.fr/233425454</controlfield>\n" +
                               "  <controlfield tag=\"004\">20240124</controlfield>\n" +
                               "  <controlfield tag=\"005\">20240126172501.000</controlfield>\n" +
                               "  <controlfield tag=\"006\">8000</controlfield>\n" +
                               "  <controlfield tag=\"007\">1999</controlfield>\n" +
                               "  <controlfield tag=\"008\">Oax3</controlfield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">(OCoLC)1233028845</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB88900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB03588900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB03688900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB07388900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB07888900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB18088900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB18188900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB26688900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB26788900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB55388900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB84788900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"035\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">FRCYB085388900440</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"100\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">20240124d2015    m  y0frey50      ba</subfield>\n" +
                               "    <subfield code=\"Y\">0#</subfield>\n" +
                               "    <subfield code=\"A\">2015</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"101\" ind1=\"0\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">fre</subfield>\n" +
                               "    <subfield code=\"2\">639-2</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"102\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">BE</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"104\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">m</subfield>\n" +
                               "    <subfield code=\"b\">y</subfield>\n" +
                               "    <subfield code=\"c\">y</subfield>\n" +
                               "    <subfield code=\"d\">ba</subfield>\n" +
                               "    <subfield code=\"e\">0</subfield>\n" +
                               "    <subfield code=\"f\">fre</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"181\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"6\">z01</subfield>\n" +
                               "    <subfield code=\"c\">txt</subfield>\n" +
                               "    <subfield code=\"2\">rdacontent</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"181\" ind1=\"#\" ind2=\"1\">\n" +
                               "    <subfield code=\"6\">z01</subfield>\n" +
                               "    <subfield code=\"a\">i#</subfield>\n" +
                               "    <subfield code=\"b\">xxxe##</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"182\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"6\">z01</subfield>\n" +
                               "    <subfield code=\"c\">c</subfield>\n" +
                               "    <subfield code=\"2\">rdamedia</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"182\" ind1=\"#\" ind2=\"1\">\n" +
                               "    <subfield code=\"6\">z01</subfield>\n" +
                               "    <subfield code=\"a\">b</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"183\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"6\">z01</subfield>\n" +
                               "    <subfield code=\"a\">ceb</subfield>\n" +
                               "    <subfield code=\"2\">RDAfrCarrier</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"200\" ind1=\"1\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">\u0098L&apos; \u009CArt religieux de la fin du Moyen Âge en France, Émile Mâle (fiche de lecture)</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"210\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">[S.l.]</subfield>\n" +
                               "    <subfield code=\"c\">[s.n.]</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"300\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">Couverture</subfield>\n" +
                               "    <subfield code=\"u\">https://static.cyberlibris.com/books_upload/136pix/9782341000505.jpg</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"304\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">Titre provenant de la page de titre du document numérique</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"307\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">La pagination de l&apos;édition imprimée correspondante est de 12 p</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"337\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">Configuration requise : navigateur internet</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"371\" ind1=\"0\" ind2=\"#\">\n" +
                               "    <subfield code=\"a\">L&apos;accès complet à la ressource est réservé aux usagers des établissements qui en ont fait l&apos;acquisition</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"452\" ind1=\"#\" ind2=\"#\">\n" +
                               "    <subfield code=\"t\">\u0098L&apos; \u009CArt religieux de la fin du Moyen Âge en France, Émile Mâle (fiche de lecture)</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"801\" ind1=\"#\" ind2=\"3\">\n" +
                               "    <subfield code=\"a\">FR</subfield>\n" +
                               "    <subfield code=\"b\">Abes</subfield>\n" +
                               "    <subfield code=\"c\">20240126</subfield>\n" +
                               "    <subfield code=\"g\">AFNOR</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"801\" ind1=\"#\" ind2=\"3\">\n" +
                               "    <subfield code=\"a\">FR</subfield>\n" +
                               "    <subfield code=\"b\">Abes</subfield>\n" +
                               "    <subfield code=\"c\">20220926</subfield>\n" +
                               "    <subfield code=\"g\">AFNOR</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"801\" ind1=\"#\" ind2=\"3\">\n" +
                               "    <subfield code=\"a\">FR</subfield>\n" +
                               "    <subfield code=\"b\">BIU Montpellier</subfield>\n" +
                               "    <subfield code=\"c\">20210112</subfield>\n" +
                               "  </datafield>\n" +
                               "  <datafield tag=\"801\" ind1=\"#\" ind2=\"3\">\n" +
                               "    <subfield code=\"a\">FR</subfield>\n" +
                               "    <subfield code=\"b\">CBL</subfield>\n" +
                               "    <subfield code=\"c\">20210102</subfield>\n" +
                               "  </datafield>\n" +
                               "</record>\n"
               );
               return Optional.of(noticesBibio);
           }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return Optional.empty();
    }
}
