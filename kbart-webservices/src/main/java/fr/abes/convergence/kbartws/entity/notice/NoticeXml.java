package fr.abes.convergence.kbartws.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.convergence.kbartws.exception.ZoneNotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Représente une notice au format d'export UnimarcXML
 */
@NoArgsConstructor
@Getter
@Setter
@JacksonXmlRootElement(localName = "record")
public class NoticeXml {

    @JacksonXmlProperty(localName = "leader")
    private String leader;

    @JacksonXmlProperty(localName = "controlfield")
    private List<Controlfield> controlfields = new ArrayList<>();

    @JacksonXmlProperty(localName = "datafield")
    private List<Datafield> datafields = new ArrayList<>();

    @Override
    public String toString() {
        return "Notice {leader=" + leader + ", ppn=" + getPpn() + "}";
    }



    /**
     * Indique si la notice est en état supprimée
     * @return
     */
    public boolean isDeleted() {
        return leader.charAt(5) == 'd';
    }

    public boolean isNoticeElectronique() {
        return getTypeDocument().startsWith("O");
    }

    /**
     * Retourne le type de document de la notice en se basant sur la zone 008
     *
     * @return les x caractères du code correspondant au type de document
     */
    public String getTypeDocument() {
        Optional<Controlfield> typeDocument = controlfields.stream().filter(cf -> cf.getTag().equals("008")).findFirst();
        return typeDocument.map(Controlfield::getValue).orElse(null);
    }



    public String getPpn() {
        Optional<Controlfield> ppn = controlfields.stream().filter(cf -> cf.getTag().equals("001")).findFirst();
        return ppn.map(Controlfield::getValue).orElse(null);
    }
}
