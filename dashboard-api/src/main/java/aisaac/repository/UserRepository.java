package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
