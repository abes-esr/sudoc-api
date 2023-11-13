package fr.abes.convergence.kbartws.repository;

import fr.abes.convergence.kbartws.entity.BiblioTableFrbr4XX;
import fr.abes.convergence.kbartws.utils.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiblioTableFrbr4XXRepository extends JpaRepository<BiblioTableFrbr4XX, Integer> {
    List<BiblioTableFrbr4XX> findAllByTagAndDatas(String tag, String datas);
}
