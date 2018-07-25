package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Salesman;
import org.crygier.graphql.mlshop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesmanRepository extends JpaRepository<Salesman,String> {
}
