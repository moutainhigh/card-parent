package com.dili.card.service.recharge;

import com.dili.card.common.annotation.TradeChannelMark;
import com.dili.card.dto.FundRequestDto;
import com.dili.card.type.FundItem;
import com.dili.card.type.TradeChannel;
import com.dili.card.type.TradeType;
import com.dili.card.util.CurrencyUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * 网银充值（提现失败的时候进行逆向操作）
 * @Auther: miaoguoxin
 * @Date: 2020/7/2 10:33
 */
@Service
@TradeChannelMark(TradeChannel.E_BANK)
public class EBankRechargeService extends AbstractRechargeManager {


    @Override
    public String buildBusinessRecordNote(FundRequestDto requestDto) {
        Long serviceCost = requestDto.getServiceCost();
        String yuan = CurrencyUtils.toYuanWithStripTrailingZeros(serviceCost == null ? 0L : serviceCost);
        return String.format("网银充值，手续费：%s元", yuan);
    }

    @Override
    public String buildSerialRecordNote(FundRequestDto requestDto) {
        return this.buildBusinessRecordNote(requestDto);
    }

    @Override
    public FundItem getPrincipalFundItem(FundRequestDto fundRequestDto) {
        return FundItem.EBANK_CHARGE;
    }


    @Override
    public FundItem getServiceCostItem(FundRequestDto fundRequestDto) {
        return FundItem.EBANK_SERVICE;
    }

    @Override
    public boolean canAddEmptyFundItem(FundRequestDto fundRequestDto) {
        return NumberUtils.toLong(fundRequestDto.getServiceCost() + "") <= 0L;
    }

    @Override
    public TradeType getTradeType(FundRequestDto fundRequestDto) {
        return TradeType.EBANK_RECHARGE;
    }

}
