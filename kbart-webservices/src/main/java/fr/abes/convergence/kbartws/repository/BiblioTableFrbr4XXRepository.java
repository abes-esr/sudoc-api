package fr.abes.convergence.kbartws.repository;

import fr.abes.convergence.kbartws.entity.BiblioTableFrbr4XX;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiblioTableFrbr4XXRepository extends JpaRepository<BiblioTableFrbr4XX, Integer> {
    List<String> findPpnByTagAndDatas(String tag, String datas);
}
