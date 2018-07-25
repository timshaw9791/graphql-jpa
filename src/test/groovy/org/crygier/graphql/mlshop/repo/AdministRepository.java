package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Administ;
import org.crygier.graphql.mlshop.model.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministRepository extends JpaRepository<Administ,String> {
}
