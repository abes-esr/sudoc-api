package fr.abes.sudoc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Entity
@Data
@Table(name = "PROVIDER", schema = "BACON")
public class Provider {
    @Id
    @Column(name = "IDT_PROVIDER")
    private BigInteger idtProvider;

    @Column(name = "PROVIDER")
    private String provider;

    @Column(name = "NOM_CONTACT")
    private String nomContact;

    @Column(name = "PRENOM_CONTACT")
    private String prenomContact;

    @Column(name = "MAIL_CONTACT")
    private String mailContact;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    public Integer getIdtProvider() {
        return Integer.valueOf(String.valueOf(this.idtProvider));
    }
}
