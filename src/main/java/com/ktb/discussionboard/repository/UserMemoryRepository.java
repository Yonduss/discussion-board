package com.ktb.discussionboard.repository;

import com.ktb.discussionboard.domain.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserMemoryRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long sequence = 1L;

    public UserMemoryRepository() {

        User user1 = new User(
                sequence++,
                "denzi@chainsawman.com",
                "strongman",
                "Pochita",
                "https://pixabay.com/ko/images/download/x-1207816_1920.jpg",
                false
        );

        User user2 = new User(
                sequence++,
                "reze@janedoe.com",
                "strongwoman",
                "Jane",
                "https://pixabay.com/ko/images/download/x-4210340_1920.jpg",
                false
        );

        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence++);
        }

        users.put(user.getId(), user);

        return user;
    }

    public Optional<User> findById(Long userId) {
        User user = users.get(userId);

        if (user == null || user.isDeleted()) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {

        return users.values().stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByEmail(String email) {

        return users.values().stream()
                .anyMatch(user ->
                        !user.isDeleted()
                                && user.getEmail().equals(email));
    }

    public boolean existsByNickname(String nickname) {

        return users.values().stream()
                .anyMatch(user ->
                        !user.isDeleted()
                                && user.getNickname().equals(nickname));
    }

    public void delete(User user) {
        user.setDeleted(true);
    }
}