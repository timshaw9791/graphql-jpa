package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.BargainRecord;
import org.crygier.graphql.mlshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Curtain
 * @date 2018/8/27 9:10
 */
public interface BargainRecordRepository extends JpaRepository<BargainRecord,String> {

    Optional<BargainRecord> findByOrder(Order order);

}
