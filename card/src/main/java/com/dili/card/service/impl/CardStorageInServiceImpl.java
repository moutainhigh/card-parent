package com.dili.card.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.card.common.constant.ServiceName;
import com.dili.card.dao.IStorageInDao;
import com.dili.card.dto.BatchCardAddStorageDto;
import com.dili.card.dto.CardStorageOutQueryDto;
import com.dili.card.entity.StorageInDo;
import com.dili.card.rpc.CardStorageRpc;
import com.dili.card.rpc.resolver.GenericRpcResolver;
import com.dili.card.service.ICardStorageInService;
import com.dili.card.util.PageUtils;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @description： 卡片入库相关功能实现
 * 
 * @author ：WangBo
 * @time ：2020年7月17日上午10:21:03
 */
@Service
public class CardStorageInServiceImpl implements ICardStorageInService {
	@Autowired
	private CardStorageRpc cardStorageRpc;
	@Autowired
	private IStorageInDao storageInDao;

	@Override
	public void batchCardStorageIn(StorageInDo storageIn) {
		// 按号段入库
		BatchCardAddStorageDto batchInfo = new BatchCardAddStorageDto();
		batchInfo.setCardType(storageIn.getCardType());
		batchInfo.setCreator(storageIn.getCreator());
		batchInfo.setCreatorId(storageIn.getCreatorId());
		batchInfo.setStartCardNo(storageIn.getStartCardNo());
		batchInfo.setEndCardNo(storageIn.getEndCardNo());
		batchInfo.setFirmId(storageIn.getFirmId());
		batchInfo.setFirmName(storageIn.getFirmName());
		batchInfo.setNotes(storageIn.getNotes());
		GenericRpcResolver.resolver(cardStorageRpc.batchAddCard(batchInfo), ServiceName.ACCOUNT_SERVICE);

		// 保存入库记录
		storageIn.setCreateTime(LocalDateTime.now());
		storageIn.setModifyTime(LocalDateTime.now());
		storageInDao.save(storageIn);
	}

	@Override
	public PageOutput<List<StorageInDo>> list(CardStorageOutQueryDto queryParam) {
		Page<Object> startPage = PageHelper.startPage(queryParam.getPage(), queryParam.getRows());
		List<StorageInDo> list = storageInDao.selectList(queryParam);
		return PageUtils.convert2PageOutput(startPage, list);
	}

}