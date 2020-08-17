package com.dili.card.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.card.dto.CardRequestDto;
import com.dili.card.dto.SerialDto;
import com.dili.card.dto.UserAccountCardResponseDto;
import com.dili.card.dto.UserAccountSingleQueryDto;
import com.dili.card.dto.pay.CreateTradeRequestDto;
import com.dili.card.dto.pay.CreateTradeResponseDto;
import com.dili.card.dto.pay.TradeRequestDto;
import com.dili.card.dto.pay.TradeResponseDto;
import com.dili.card.entity.BusinessRecordDo;
import com.dili.card.exception.CardAppBizException;
import com.dili.card.rpc.CardManageRpc;
import com.dili.card.rpc.resolver.CardManageRpcResolver;
import com.dili.card.rpc.resolver.PayRpcResolver;
import com.dili.card.service.IAccountCycleService;
import com.dili.card.service.IAccountQueryService;
import com.dili.card.service.ICardManageService;
import com.dili.card.service.IReturnCardService;
import com.dili.card.service.ISerialService;
import com.dili.card.type.CardStatus;
import com.dili.card.type.FeeType;
import com.dili.card.type.FundItem;
import com.dili.card.type.OperateType;
import com.dili.card.type.TradeChannel;
import com.dili.card.type.TradeType;
import com.dili.card.validator.AccountValidator;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;

import io.seata.spring.annotation.GlobalTransactional;


/**
 * @description： 卡片退卡换卡等操作service实现
 *
 * @author ：WangBo
 * @time ：2020年4月28日下午5:09:47
 */
@Service("cardManageService")
public class CardManageServiceImpl implements ICardManageService {

    @Autowired
    private CardManageRpcResolver cardManageRpcResolver;
    @Resource
    private CardManageRpc cardManageRpc;
    @Resource
    private ISerialService serialService;
    @Resource
    private PayRpcResolver payRpcResolver;
    @Resource
    protected IAccountQueryService accountQueryService;
    @Autowired
    private IAccountCycleService accountCycleService;
    @Autowired
    private IReturnCardService returnCardService;

    /**
     * @param cardParam
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLostCard(CardRequestDto cardParam) {
        UserAccountSingleQueryDto query = new UserAccountSingleQueryDto();
        query.setCardNo(cardParam.getCardNo());
        UserAccountCardResponseDto accountCard = accountQueryService.getForUnLostCard(query);
        BusinessRecordDo businessRecordDo = serialService.createBusinessRecord(cardParam, accountCard, temp -> {
            temp.setType(OperateType.LOSS_REMOVE.getCode());
        });
        serialService.saveBusinessRecord(businessRecordDo);
        BaseOutput<?> baseOutput = cardManageRpc.unLostCard(cardParam);
        if (!baseOutput.isSuccess()) {
            throw new CardAppBizException(baseOutput.getCode(), baseOutput.getMessage());
        }
        this.saveRemoteSerialRecord(businessRecordDo);
    }

    @Override
	@Transactional(rollbackFor = Exception.class)
	@GlobalTransactional(rollbackFor = Exception.class)
    public void returnCard(CardRequestDto cardParam) {
    	returnCardService.handle(cardParam);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetLoginPwd(CardRequestDto cardParam) {
        //获取卡信息
        UserAccountCardResponseDto accountCard = accountQueryService.getByAccountId(cardParam.getAccountId());
        AccountValidator.validateMatchAccount(cardParam, accountCard);
        //保存本地操作记录
        BusinessRecordDo businessRecordDo = saveLocalSerialRecordNoFundSerial(cardParam, accountCard, OperateType.RESET_PWD);
        //远程重置密码操作
        cardManageRpcResolver.resetLoginPwd(cardParam);
        //记录远程操作记录
        this.saveRemoteSerialRecord(businessRecordDo);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockCard(CardRequestDto cardParam) {
        UserAccountCardResponseDto accountCard = accountQueryService.getByAccountId(cardParam);
        if (!Integer.valueOf(CardStatus.LOCKED.getCode()).equals(accountCard.getCardState())) {
            throw new CardAppBizException("", String.format("该卡为%s状态,不能进行解锁", CardStatus.getName(accountCard.getCardState())));
        }
        BusinessRecordDo businessRecordDo = serialService.createBusinessRecord(cardParam, accountCard, temp -> {
            temp.setType(OperateType.LIFT_LOCKED.getCode());
        });
        serialService.saveBusinessRecord(businessRecordDo);
        BaseOutput<?> baseOutput = cardManageRpc.unLostCard(cardParam);
        if (!baseOutput.isSuccess()) {
            throw new CardAppBizException(baseOutput.getCode(), baseOutput.getMessage());
        }
        this.saveRemoteSerialRecord(businessRecordDo);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public void reportLossCard(CardRequestDto cardParam) {
        UserAccountCardResponseDto userAccount = accountQueryService.getByCardNo(cardParam.getCardNo());
        AccountValidator.validateMatchAccount(cardParam, userAccount);
        BusinessRecordDo businessRecord = serialService.createBusinessRecord(cardParam, userAccount,
                record -> record.setType(OperateType.LOSS_CARD.getCode()));
        serialService.saveBusinessRecord(businessRecord);

        cardManageRpcResolver.reportLossCard(cardParam);

        this.saveRemoteSerialRecord(businessRecord);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public void changeCard(CardRequestDto requestDto) {
        UserAccountCardResponseDto userAccount = accountQueryService.getByCardNo(requestDto.getCardNo());
        AccountValidator.validateMatchAccount(requestDto, userAccount);
        this.validateCanChange(requestDto, userAccount);

        Long serviceFee = requestDto.getServiceFee();
        BusinessRecordDo businessRecord = serialService.createBusinessRecord(requestDto, userAccount, record -> {
            record.setType(OperateType.CHANGE.getCode());
            record.setAmount(serviceFee);
            record.setTradeType(TradeType.FEE.getCode());
            record.setTradeChannel(TradeChannel.CASH.getCode());
            record.setNotes("换卡，工本费转为市场收入");
        });

        CreateTradeRequestDto tradeRequest = CreateTradeRequestDto.createTrade(
                TradeType.FEE.getCode(),
                userAccount.getAccountId(),
                userAccount.getFundAccountId(),
                serviceFee,
                businessRecord.getSerialNo(),
                String.valueOf(businessRecord.getCycleNo()));
        //创建交易
        CreateTradeResponseDto tradeRespDto = payRpcResolver.prePay(tradeRequest);
        String tradeNo = tradeRespDto.getTradeId();

        businessRecord.setTradeNo(tradeNo);
        serialService.saveBusinessRecord(businessRecord);

        accountCycleService.increaseCashBox(businessRecord.getCycleNo(), requestDto.getServiceFee());
        cardManageRpcResolver.changeCard(requestDto);

        TradeRequestDto tradeRequestDto = TradeRequestDto.createTrade(userAccount, tradeNo, TradeChannel.CASH.getCode(), requestDto.getLoginPwd());
        tradeRequestDto.addServiceFeeItem(requestDto.getServiceFee(), FundItem.IC_CARD_COST);
        TradeResponseDto tradeResponseDto = payRpcResolver.trade(tradeRequestDto);

        SerialDto serialDto = serialService.createAccountSerialWithFund(businessRecord, tradeResponseDto, (serialRecord, feeType) -> {
            FundItem fundItem = FundItem.getByCode(feeType);
            if (fundItem != null) {
                serialRecord.setFundItem(fundItem.getCode());
                serialRecord.setFundItemName(fundItem.getName());
            }
            serialRecord.setNotes("补卡，工本费转为市场收入");
        });
        serialService.handleSuccess(serialDto);
    }

    /**
     * 保存本地操作记录
     */
    private BusinessRecordDo saveLocalSerialRecordNoFundSerial(CardRequestDto cardParam, UserAccountCardResponseDto accountCard, OperateType operateType) {
        BusinessRecordDo businessRecord = serialService.createBusinessRecord(cardParam, accountCard, temp -> {
            temp.setType(operateType.getCode());
        });
        serialService.saveBusinessRecord(businessRecord);
        return businessRecord;
    }

    /**
     * saveRemoteSerialRecord
     */
    private void saveRemoteSerialRecord(BusinessRecordDo businessRecord) {
        //成功则修改状态及期初期末金额，存储操作流水
        SerialDto serialDto = serialService.createAccountSerial(businessRecord, null);
        serialService.handleSuccess(serialDto, false);
    }

    /**
     * 校验
     * @author miaoguoxin
     * @date 2020/7/29
     */
    private void validateCanChange(CardRequestDto requestDto, UserAccountCardResponseDto userAccount) {
        if (userAccount.getCardState() != CardStatus.NORMAL.getCode()) {
            throw new CardAppBizException(ResultCode.DATA_ERROR, "该卡不是正常状态，不能进行该操作");
        }
        if (userAccount.getCardNo().equals(requestDto.getNewCardNo())) {
            throw new CardAppBizException(ResultCode.DATA_ERROR, "新老卡号不能相同");
        }
    }
}
