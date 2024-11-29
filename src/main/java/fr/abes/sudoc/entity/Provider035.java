package fr.abes.sudoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "PROVIDER_035", schema = "BACON")
public class Provider035 implements Serializable {
    @Id
    @Column(name = "IDT_PROVIDER")
    private Integer id;
    @Column(name = "VALEUR_035")
    private String valeur;
}
