package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.model.Insurance;

/**
 * @author Curtain
 * @date 2018/8/2 11:10
 */
public interface InsuranceService {

    /**
     * 保存保险单信息 同时生成保险回访单
     * @param insurance
     * @return
     */
    Insurance save(Insurance insurance);

    /**
     * 保险信息更新
     * @param insurance
     * @return
     */
    Insurance update(Insurance insurance);
}
