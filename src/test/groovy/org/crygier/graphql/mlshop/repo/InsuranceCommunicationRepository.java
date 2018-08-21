package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.InsuranceCommunication;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Curtain
 * @date 2018/7/27 16:54
 */
@Repository
public interface InsuranceCommunicationRepository extends JpaRepository<InsuranceCommunication,String> {
    List<InsuranceCommunication> findByDistributeTimeBeforeAndStatus(Long distributeTime, CarCommunicationStatusEnum status);

}
