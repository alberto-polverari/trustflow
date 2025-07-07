package it.trustflow.auth.repository;

import it.trustflow.auth.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByUsername(String username);
    Optional<Utente> findByCodiceFiscale(String codiceFiscale);
}
