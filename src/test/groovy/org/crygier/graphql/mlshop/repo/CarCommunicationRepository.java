package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.CarCommunication;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationStatusEnum;
import org.crygier.graphql.mlshop.model.enums.CarCommunicationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2018/7/26 9:34
 */
@Repository
public interface CarCommunicationRepository extends JpaRepository<CarCommunication, String> {

    Long countByUpdatetimeBetweenAndType(Long startTime, Long endTime,CarCommunicationTypeEnum carType);

    Long countByUpdatetimeBetweenAndStatus(Long startTime, Long endTime,CarCommunicationStatusEnum status);


}
