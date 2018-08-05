package org.crygier.graphql.mlshop.repo;

import org.crygier.graphql.mlshop.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Curtain
 * @date 2018/8/1 16:46
 */
public interface FeedbackRepository extends JpaRepository<Feedback,String> {
}
