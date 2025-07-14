package it.trustflow.auth.repository;

import it.trustflow.auth.entity.Utente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsernameReturnsEmpty() {
        Optional<Utente> result = userRepository.findByUsername("notfound");
        assertThat(result).isEmpty();
    }
}
