package org.crygier.graphql.repo;

import org.crygier.graphql.model.entity.Department;
import org.crygier.graphql.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}