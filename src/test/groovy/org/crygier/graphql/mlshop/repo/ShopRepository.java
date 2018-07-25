package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Customer;
import org.crygier.graphql.mlshop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop,String> {
}
