package org.crygier.graphql.mlshop.service;

import org.crygier.graphql.mlshop.bean.BargainSetting;
import org.crygier.graphql.mlshop.model.BargainRecord;

/**
 * @author Curtain
 * @date 2018/8/27 8:23
 */
public interface BargainService {

    /**
     * 保存砍价设置
     * @param bargainSetting
     * @return
     */
    BargainSetting saveBargainSetting(BargainSetting bargainSetting);

    /**
     * 修改砍价设置
     * @param bargainSetting
     * @return
     */
    BargainSetting updateBargainSetting(BargainSetting bargainSetting);

    /**
     * 查找砍价设置
     * @return
     */
    BargainSetting findBargainSetting();

    /**
     * 生成砍价记录
     * @param orderId
     * @return
     */
    BargainRecord generate(String orderId);

    /**
     * 砍价
     * @param phone
     * @return
     */
    BargainRecord bargain(String phone,String orderId);

    /**
     * 查找砍价记录
     * @param orderId
     * @return
     */
    BargainRecord findBargainRecord(String orderId);

}
