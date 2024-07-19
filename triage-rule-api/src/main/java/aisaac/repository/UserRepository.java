package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aisaac.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

	@Query("SELECT r.userId FROM User r WHERE r.displayName LIKE %:name%")
	List<Long> getUserIdsOnLikeSearch(@Param("name") String name);
}
