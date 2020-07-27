package com.dili.card.service;

import com.dili.card.dto.CardStorageDto;
import com.dili.card.dto.CardStorageOutQueryDto;
import com.dili.card.dto.CardStorageOutRequestDto;
import com.dili.card.dto.CardStorageOutResponseDto;
import com.dili.ss.domain.PageOutput;

import java.util.List;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/7/1 16:19
 */
public interface ICardStorageService {

    /**
    * 保存卡申领记录
    * @author miaoguoxin
    * @date 2020/7/3
    */
    void saveOutRecord(CardStorageOutRequestDto requestDto);

    /**
    * 根据主键id查询单个
    * @author miaoguoxin
    * @date 2020/7/2
    */
    CardStorageOutResponseDto getById(Long id);

    /**
    * 卡申领记录分页查询
    * @author miaoguoxin
    * @date 2020/7/1
    */
    PageOutput<List<CardStorageOutResponseDto>> getPage(CardStorageOutQueryDto queryDto);

    /**
    * 多条件查询
    * @author miaoguoxin
    * @date 2020/7/1
    */
    List<CardStorageOutResponseDto> getByCondition(CardStorageOutQueryDto queryDto);
    
    
    /**
     * 卡片仓库列表
     * @param id
     * @return
     */
    PageOutput<List<CardStorageDto>> cardStorageList(CardStorageDto queryParam);

    /**
     * 作废卡片
     * @param cardNo
     * @param remark 备注（可不填）
     */
    void voidCard(String cardNo, String remark);
}
