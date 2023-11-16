package fr.abes.convergence.kbartws.repository;

import fr.abes.convergence.kbartws.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByProvider(String provider);
}
