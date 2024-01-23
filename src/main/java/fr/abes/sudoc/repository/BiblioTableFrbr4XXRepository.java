package fr.abes.sudoc.repository;

import fr.abes.sudoc.entity.BiblioTableFrbr4XX;
import fr.abes.sudoc.utils.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiblioTableFrbr4XXRepository extends JpaRepository<BiblioTableFrbr4XX, Integer> {
    @ExecutionTime
    List<BiblioTableFrbr4XX> findAllByTagAndDatas(String tag, String datas);
}
