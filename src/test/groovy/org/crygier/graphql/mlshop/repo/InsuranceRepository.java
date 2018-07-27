package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Curtain
 * @date 2018/7/27 16:26
 */
@Repository
public interface InsuranceRepository  extends JpaRepository<Insurance,String>{
}
