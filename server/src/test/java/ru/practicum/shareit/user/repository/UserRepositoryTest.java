package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void verifyBootstrappingByPersistingAnUser() {
        User user = makeUser("Viktor", "viktor@mail.ru");

        assertEquals(user.getId(), 0);
        User result = userRepository.save(user);

        long id = (long) em.getId(result);

        assertEquals(user.getId(), id);
    }

    @Test
    void verifyExistsUserByEmailAndIdIsNot() {

        User user = makeUser("Viktor", "viktor@mail.ru");
        User user1 = makeUser("Michail", "misha@mail.ru");

        User result = userRepository.save(user);
        userRepository.save(user1);

        long id = (long) em.getId(result);

        boolean result1 = userRepository.existsUserByEmailAndIdIsNot("misha@mail.ru", id);
        boolean result2 = userRepository.existsUserByEmailAndIdIsNot("viktor@mail.ru", id);
        boolean result3 = userRepository.existsUserByEmailAndIdIsNot("trutru@mail.ru", id);

        assertTrue(result1);
        assertFalse(result2);
        assertFalse(result3);
    }

    @Test
    void verifyExistsUserById() {
        User user = makeUser("Viktor", "viktor@mail.ru");
        User user1 = makeUser("Michail", "misha@mail.ru");

        User result1 = userRepository.save(user);
        long id1 = (long) em.getId(result1);

        User result2 = userRepository.save(user1);
        long id2 = (long) em.getId(result2);

        assertTrue(userRepository.existsUserById(id1));
        assertTrue(userRepository.existsUserById(id2));
        assertFalse(userRepository.existsUserById(99L));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}
