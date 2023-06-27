package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsUserByEmailAndIdNot(String email, long userId);

    boolean existsUserById(Long userId);

}
