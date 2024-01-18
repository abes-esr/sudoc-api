package fr.abes.sudoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BIBLIO_TABLE_FRBR_4XX", schema = "AUTORITES")
@Getter
@Setter
public class BiblioTableFrbr4XX {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PPN")
    private String ppn;

    @Column(name = "POSFIELD")
    private String posField;

    @Column(name = "POSSUBFIELD")
    private String posSubField;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "DATAS", length = 4000)
    private String datas;
}
