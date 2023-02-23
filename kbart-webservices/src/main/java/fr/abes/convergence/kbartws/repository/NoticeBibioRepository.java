package fr.abes.convergence.kbartws.repository;

import fr.abes.convergence.kbartws.entity.NoticeBibio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeBibioRepository extends JpaRepository<NoticeBibio, Integer> {
    Optional<NoticeBibio> findByPpn(String ppn);
}
