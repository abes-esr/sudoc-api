package fr.abes.sudoc.component;

import fr.abes.sudoc.utils.ExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLRecoverableException;
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


    public String baconProvider035(Integer provider) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.BACON_PROVIDER_035_JSON(");
        request.append(provider);
        request.append(") as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }


    public List<String> doiToPpn(String doi) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("select a.ppn from autorites.biblio_table_FRBR_0xx a where  ");
        request.append("upper(SUBSTR(a.datas,1,50)) = '");
        request.append(doi.toUpperCase());
        request.append("' and a.tag='017$a' ");
        request.append("and a.id in (select /*+ no_index(b BIBLIO_TABLE_FRBR_0XX_IDX_DATA) */ id from autorites.biblio_table_FRBR_0xx b where ");
        request.append("a.id=b.id and ");
        request.append("b.tag='017$2' and SUBSTR(b.datas,1,50)='DOI' and a.POSFIELD=b.POSFIELD  and b.POSSUBFIELD='2')");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }
}
