package com.dili.card.service.recharge;

import com.dili.card.common.annotation.TradeChannelMark;
import com.dili.card.dto.FundRequestDto;
import com.dili.card.type.TradeChannel;
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
    public void recharge(FundRequestDto requestDto) {

    }
}
