package fr.abes.sudoc.repository;

import fr.abes.sudoc.entity.Provider035;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Provider035Repository extends ReactiveCrudRepository<Provider035, Integer> {
}
