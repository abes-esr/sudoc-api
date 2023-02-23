package fr.abes.convergence.kbartws.repository;

import fr.abes.convergence.kbartws.entity.NoticesBibio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticesBibioRepository extends JpaRepository<NoticesBibio, Integer> {
    Optional<NoticesBibio> findByPpn(String ppn);
}
