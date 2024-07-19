package aisaac.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
