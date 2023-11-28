package fr.abes.convergence.kbartws.entity.notice;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import fr.abes.convergence.kbartws.exception.ZoneNotFoundException;
import fr.abes.convergence.kbartws.utils.TYPE_DOCUMENT;
import fr.abes.convergence.kbartws.utils.TYPE_SUPPORT;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
     *
     * @return
     */
    public boolean isDeleted() {
        return leader.charAt(5) == 'd';
    }

    public boolean isNoticeElectronique() {
        return get008().startsWith("O");
    }

    public boolean isNoticeImprimee() {
        return get008().startsWith("A");
    }

    /**
     * Retourne le type de document de la notice en se basant sur la zone 008
     *
     * @return les x caractères du code correspondant au type de document
     */
    public String get008() {
        Optional<Controlfield> typeDocument = controlfields.stream().filter(cf -> cf.getTag().equals("008")).findFirst();
        return typeDocument.map(Controlfield::getValue).orElse(null);
    }

    public TYPE_DOCUMENT getTypeDocument() {
        return switch(get008().substring(1,2)) {
            case "a" -> TYPE_DOCUMENT.MONOGRAPHIE;
            case "b" -> TYPE_DOCUMENT.PERIODIQUE;
            case "d" -> TYPE_DOCUMENT.COLLECTION;
            case "r" -> TYPE_DOCUMENT.RECUEIL;
            case "s" -> TYPE_DOCUMENT.EXTRAIT;
            case "e" -> TYPE_DOCUMENT.SERIE;
            default -> TYPE_DOCUMENT.AUTRE;
        };
    }
    /**
     * Retourne le type de support de la notice en se basant sur le premier caractère de la 008
     *
     * @return le type de support sous forme d'enum
     */
    public TYPE_SUPPORT getTypeSupport() throws ZoneNotFoundException {
        if (get008() == null) {
            throw new ZoneNotFoundException("Zone 008 Absente de la notice");
        }
        return switch (get008().substring(0, 1)) {
            case "A" -> TYPE_SUPPORT.IMPRIME;
            case "O" -> TYPE_SUPPORT.ELECTRONIQUE;
            default -> TYPE_SUPPORT.AUTRE;
        };
    }

    public String getPpn() {
        Optional<Controlfield> ppn = controlfields.stream().filter(cf -> cf.getTag().equals("001")).findFirst();
        return ppn.map(Controlfield::getValue).orElse(null);
    }

    public List<String> get4XXDollar0(String zone) {
        List<String> ppns = new ArrayList<>();
        List<Datafield> listeZone = this.datafields.stream().filter(datafield -> datafield.getTag().equals(zone)).collect(Collectors.toList());
        if (!listeZone.isEmpty()) {
            for (Datafield datafield : listeZone) {
                List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals("0")).collect(Collectors.toList());
                subFields.forEach(subField -> {
                    ppns.add(subField.getValue());
                });
            }
        }
        return ppns;
    }

    /**
     * Méthode vérifiant si un provider est présent en début d'une 035 $a
     * @param provider provider à vérifier
     * @return true si le provider est présent en début d'une 035$a, false sinon
     */
    public boolean checkProviderIn035a(String provider) {
        List<Datafield> listeZone = this.datafields.stream().filter(datafield -> datafield.getTag().equals("035")).collect(Collectors.toList());
        if (!listeZone.isEmpty()) {
            for (Datafield datafield : listeZone) {
                List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals("a")).collect(Collectors.toList());
                if (subFields.stream().anyMatch(sf -> sf.getValue().toLowerCase().startsWith(provider.toLowerCase()))) return true;
            }
        }
        return false;
    }

    /**
     * Méthode permettant de vérifier si un provider est contenu dans une zone
     * @param provider : le provider à vérifier
     * @param zone : la zone à chercher dans la notice
     * @param sousZone : la sous zone à chercher dans la zone
     * @return true si le provider est contenu dans la zone / sous zone passée en paramètre
     */
    public boolean checkProviderInZone(String provider, String zone, String sousZone) {
        List<Datafield> listeZone = this.datafields.stream().filter(datafield -> datafield.getTag().equals(zone)).collect(Collectors.toList());
        if (!listeZone.isEmpty()) {
            for (Datafield datafield : listeZone) {
                List<SubField> subFields = datafield.getSubFields().stream().filter(subField -> subField.getCode().equals(sousZone)).collect(Collectors.toList());
                if (subFields.stream().anyMatch(sf -> sf.getValue().toLowerCase().contains(provider.toLowerCase(Locale.ROOT)))) return true;
            }
        }
        return false;
    }
}
