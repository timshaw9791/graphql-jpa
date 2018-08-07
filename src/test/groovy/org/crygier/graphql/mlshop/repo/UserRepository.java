package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}