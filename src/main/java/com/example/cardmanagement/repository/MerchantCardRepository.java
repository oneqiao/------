package com.example.cardmanagement.repository;

import com.example.cardmanagement.entity.MerchantCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * 商户卡号数据访问层
 * 继承JpaRepository实现基本CRUD操作
 * 继承JpaSpecificationExecutor实现复杂查询
 */
public interface MerchantCardRepository extends JpaRepository<MerchantCard, Long>, JpaSpecificationExecutor<MerchantCard> {
    
    /**
     * 根据商户ID查询卡片列表
     * @param merchantId 商户ID
     * @return List<MerchantCard> 卡片列表
     */
    List<MerchantCard> findByMerchantId(Long merchantId);
    
    /**
     * 根据卡号查询卡片
     * @param cardNo 卡号
     * @return MerchantCard 卡片对象
     */
    MerchantCard findByCardNo(String cardNo);
    
    /**
     * 根据商户ID和卡状态查询卡片数量
     * @param merchantId 商户ID
     * @param cardStatus 卡状态
     * @return 卡片数量
     */
    long countByMerchantIdAndCardStatus(Long merchantId, Integer cardStatus);
}