package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Salesman;

/**
 * @author Curtain
 * @date 2018/9/4 9:24
 */
public interface SalesmanService {

    /**
     * 查找
     * @param id
     * @return
     */
    Salesman findOne(String id);
}
