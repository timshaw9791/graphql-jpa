package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.InsuranceCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2018/7/27 16:54
 */
@Repository
public interface InsuranceCommunicationRepository extends JpaRepository<InsuranceCommunication,String> {
}
