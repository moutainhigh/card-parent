package com.dili.card.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.dili.card.dao.IBusinessRecordDao;
import com.dili.card.dto.*;
import com.dili.card.entity.AccountCycleDo;
import com.dili.card.entity.BusinessRecordDo;
import com.dili.card.entity.SerialRecordDo;
import com.dili.card.exception.CardAppBizException;
import com.dili.card.rpc.resolver.AccountQueryRpcResolver;
import com.dili.card.rpc.resolver.CustomerRpcResolver;
import com.dili.card.rpc.resolver.SerialRecordRpcResolver;
import com.dili.card.rpc.resolver.UidRpcResovler;
import com.dili.card.service.IAccountCycleService;
import com.dili.card.service.ISerialService;
import com.dili.card.type.BizNoType;
import com.dili.card.type.OperateState;
import com.dili.card.type.OperateType;
import com.dili.card.util.DateUtil;
import com.dili.card.util.PageUtils;
import com.dili.customer.sdk.domain.Customer;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作流水service实现类
 * @author xuliang
 */
@Service
public class SerialServiceImpl implements ISerialService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerialServiceImpl.class);

    @Resource
    private UidRpcResovler uidRpcResovler;
    @Resource
    private CustomerRpcResolver customerRpcResolver;
    @Resource
    private IBusinessRecordDao businessRecordDao;
    @Resource
    private IAccountCycleService accountCycleService;
    @Resource
    private SerialRecordRpcResolver serialRecordRpcResolver;
    @Resource
    private AccountQueryRpcResolver accountQueryRpcResolver;

    @Override
    public void buildCommonInfo(CardRequestDto cardParam, BusinessRecordDo businessRecord) {
        //编号、卡号、账户id
        businessRecord.setSerialNo(uidRpcResovler.bizNumber(BizNoType.OPERATE_SERIAL_NO.getCode()));
        businessRecord.setAccountId(cardParam.getAccountId());
        businessRecord.setCardNo(cardParam.getCardNo());
        //客户信息
        Customer customer = customerRpcResolver.getWithNotNull(cardParam.getCustomerId(), cardParam.getFirmId());
        businessRecord.setCustomerId(customer.getId());
        businessRecord.setCustomerNo(customer.getCode());
        businessRecord.setCustomerName(customer.getName());
        //账务周期
        AccountCycleDo accountCycle = accountCycleService.findActiveCycleByUserId(cardParam.getOpId(), cardParam.getOpName(), cardParam.getOpNo());
        businessRecord.setCycleNo(accountCycle.getCycleNo());
        //操作员信息
        businessRecord.setOperatorId(cardParam.getOpId());
        businessRecord.setOperatorNo(cardParam.getOpNo());
        businessRecord.setOperatorName(cardParam.getOpName());
        businessRecord.setFirmId(cardParam.getFirmId());
        //时间、默认状态等数据
        LocalDateTime localDateTime = LocalDateTime.now();
        businessRecord.setState(OperateState.PROCESSING.getCode());
        businessRecord.setOperateTime(localDateTime);
        businessRecord.setModifyTime(localDateTime);
        businessRecord.setVersion(1);
    }

    @Transactional
    @Override
    public void saveBusinessRecord(BusinessRecordDo businessRecord) {
        businessRecordDao.save(businessRecord);
    }

    @Override
    public void copyCommonFields(SerialRecordDo serialRecord, BusinessRecordDo businessRecord) {
        serialRecord.setSerialNo(businessRecord.getSerialNo());
        serialRecord.setAccountId(businessRecord.getAccountId());
        serialRecord.setCardNo(businessRecord.getCardNo());
        serialRecord.setCustomerId(businessRecord.getCustomerId());
        serialRecord.setCustomerNo(businessRecord.getCustomerNo());
        serialRecord.setCustomerName(businessRecord.getCustomerName());
        serialRecord.setOperatorId(businessRecord.getOperatorId());
        serialRecord.setOperatorNo(businessRecord.getOperatorNo());
        serialRecord.setOperatorName(businessRecord.getOperatorName());
        serialRecord.setFirmId(businessRecord.getFirmId());
        serialRecord.setType(businessRecord.getType());
        serialRecord.setTradeType(businessRecord.getTradeType());
        serialRecord.setTradeChannel(businessRecord.getTradeChannel());
        serialRecord.setTradeNo(businessRecord.getTradeNo());
    }

    @Override
    public void handleFailure(SerialDto serialDto) {
        try {
            BusinessRecordDo businessRecord = new BusinessRecordDo();
            businessRecord.setSerialNo(serialDto.getSerialNo());
            businessRecord.setState(OperateState.FAILURE.getCode());
            businessRecordDao.doFailureUpdate(businessRecord);
        } catch (Exception e) {
            LOGGER.error(JSON.toJSONString(serialDto), e);//记录数据方便后期处理
            throw new CardAppBizException("修改办理状态失败");
        }
    }

    @Override
    public void handleSuccess(SerialDto serialDto) {
        handleSuccess(serialDto, true);
    }

    @Override
    public void handleSuccess(SerialDto serialDto, boolean saveSerial) {
        try {
            //修改状态
            BusinessRecordDo businessRecord = new BusinessRecordDo();
            businessRecord.setSerialNo(serialDto.getSerialNo());
            businessRecord.setState(OperateState.SUCCESS.getCode());
            businessRecord.setStartBalance(serialDto.getStartBalance());
            businessRecord.setEndBalance(serialDto.getEndBalance());
            businessRecordDao.doSuccessUpdate(businessRecord);
        } catch (Exception e) {
            LOGGER.error(JSON.toJSONString(serialDto), e);//记录数据方便后期处理
            throw new CardAppBizException("", "修改办理状态失败");
        }
        if (saveSerial) {
            saveSerialRecord(serialDto);
        }
    }

    @Override
    public void saveSerialRecord(SerialDto serialDto) {
        try {
            //保存流水
            serialRecordRpcResolver.batchSave(serialDto);
        } catch (Exception e) {
            LOGGER.error(JSON.toJSONString(serialDto), e);//记录数据方便后期处理
            throw new CardAppBizException("保存操作交易流水失败");
        }
    }

    @Override
    public List<BusinessRecordDo> cycleReprintList(SerialQueryDto serialQueryDto) {
        SerialQueryDto query = new SerialQueryDto();
        query.setFirmId(serialQueryDto.getFirmId());
        query.setOperatorId(serialQueryDto.getOperatorId());
        AccountCycleDo accountCycle = accountCycleService.findActiveCycleByUserId(serialQueryDto.getOperatorId());
        if (accountCycle == null) {
            throw new CardAppBizException("", "未查询到操作员账期");
        }
        query.setCycleNo(accountCycle.getCycleNo());
        query.setSort("operate_time");
        query.setOrder("desc");
        query.setState(OperateState.SUCCESS.getCode());
        query.setOperateTypeList(OperateType.createReprintList());
        query.setLimit(serialQueryDto.getLimit() != null ? serialQueryDto.getLimit() : 20);
        return businessRecordDao.list(query);
    }

    @Override
    public List<BusinessRecordDo> todayChargeList(SerialQueryDto serialQueryDto) {
        SerialQueryDto query = new SerialQueryDto();
        query.setFirmId(serialQueryDto.getFirmId());
        List<Long> accountIdList = new ArrayList<>();
        accountIdList.add(serialQueryDto.getAccountId());
        AccountWithAssociationResponseDto cardAssociation = accountQueryRpcResolver.findByAccountIdWithAssociation(serialQueryDto.getAccountId());
        if (!CollUtil.isEmpty(cardAssociation.getAssociation())) {
            accountIdList.addAll(cardAssociation.getAssociation().stream().map(UserAccountCardResponseDto::getAccountId).collect(Collectors.toList()));
        }
        query.setAccountIdList(accountIdList);
        query.setType(OperateType.ACCOUNT_CHARGE.getCode());
        query.setState(OperateState.SUCCESS.getCode());
        query.setOperateTimeStart(DateUtil.formatDate("yyyy-MM-dd") + " 00:00:00");
        query.setOperateTimeEnd(DateUtil.formatDate("yyyy-MM-dd") + " 23:59:59");
        query.setSort("operate_time");
        query.setOrder("desc");
        return businessRecordDao.list(query);
    }

    @Override
    public List<BusinessRecordDo> queryBusinessRecord(SerialQueryDto serialQueryDto) {
        return businessRecordDao.list(serialQueryDto);
    }

    @Override
    public PageOutput<List<BusinessRecordResponseDto>> queryPage(SerialQueryDto serialQueryDto) {
        Page<BusinessRecordDo> page = PageHelper.startPage(serialQueryDto.getPage(), serialQueryDto.getRows());
        List<BusinessRecordDo> businessRecordDos = this.queryBusinessRecord(serialQueryDto);
        List<BusinessRecordResponseDto> recordResponseDtos = businessRecordDos.stream().map(record -> {
            BusinessRecordResponseDto responseDto = new BusinessRecordResponseDto();
            BeanUtils.copyProperties(record, responseDto);
            return responseDto;
        }).collect(Collectors.toList());
        return PageUtils.convert2PageOutput(page,recordResponseDtos);
    }
}
