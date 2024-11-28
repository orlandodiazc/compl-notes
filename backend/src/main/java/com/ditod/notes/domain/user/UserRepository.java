package com.ditod.notes.domain.user;

import com.ditod.notes.web.user.dto.UserFilteredResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
            SELECT u.id AS id, u.username AS username, u.name AS name, ui.id AS imageId
            FROM User u
            LEFT JOIN u.image ui
            WHERE u.username LIKE LOWER(CONCAT('%', :term, '%'))
              OR LOWER(u.name) LIKE LOWER(CONCAT('%', :term, '%'))
            ORDER BY (
                SELECT MAX(n.updatedAt)
                FROM Note n
                WHERE n.owner = u
            ) DESC
            
            """)
    List<UserFilteredResponse> findFilteredUsers(@Param("term") String searchQuery,
                                                 Pageable pageable);

    <T> Optional<T> findByUsername(String username, Class<T> type);

    Optional<User> findByUsername(String username);
}
