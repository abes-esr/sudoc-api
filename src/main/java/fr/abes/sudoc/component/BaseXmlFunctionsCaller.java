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

    private static final String requestIssn = "SELECT DISTINCT ppn FROM AUTORITES.biblio_table_fouretout WHERE cle1='ISSN' AND cle2=?";
    private static final String requestIsbn = "SELECT AUTORITES.ISBN2PPNJSON(?) AS ppn FROM dual";
    private static final String requestDoi = "SELECT DISTINCT ppn FROM AUTORITES.biblio_table_fouretout WHERE cle1='DOI' AND cle2=?";

    public BaseXmlFunctionsCaller(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<String> issnToPpn(String issn) throws UncategorizedSQLException, SQLException {
        List<String> resultList = new ArrayList<>();

        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(requestIssn);

        ps.setString(1, issn);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            resultList.add(rs.getString("ppn"));
        }

        return resultList;
    }


    public String isbnToPpn(String isbn) throws UncategorizedSQLException, SQLException {
        String result = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(requestIsbn);

        ps.setString(1, isbn);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            result = rs.getString("ppn");
        }

        return result;
    }


    public List<String> doiToPpn(String doi) throws UncategorizedSQLException, SQLException {
        List<String> resultList = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(requestDoi);
        ps.setString(1, doi.toLowerCase());

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            resultList.add(rs.getString("ppn"));
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

    public Optional<NoticesBibio> findByPpn(String ppn) {
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
                noticesBibio.setDataXml(rs.getClob("xmlclob"));
                return Optional.of(noticesBibio);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return Optional.empty();
    }
}
