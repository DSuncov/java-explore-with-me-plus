package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsById(Long id);
}
