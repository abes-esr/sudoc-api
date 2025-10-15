package fr.abes.sudoc.repository;

import fr.abes.sudoc.entity.BiblioTableFrbr4XX;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiblioTableFrbr4XXRepository extends JpaRepository<BiblioTableFrbr4XX, Integer> {

    List<BiblioTableFrbr4XX> findAllByTagAndDatas(String tag, String datas);
}
