package org.crygier.graphql.mlshop.repo;

        import org.crygier.graphql.mlshop.model.CarSource;
        import org.crygier.graphql.mlshop.model.Customer;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.stereotype.Repository;

@Repository
public interface  CustomerRepository extends JpaRepository<Customer,String> {
}
