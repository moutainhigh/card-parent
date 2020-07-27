package com.dili.card.dto;

import com.alibaba.fastjson.JSONObject;
import com.dili.card.validator.ConstantValidator;
import com.dili.card.validator.FundValidator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 用于资金操作相关dto
 * @author xuliang
 */
public class FundRequestDto extends CardRequestDto {

    /** 交易渠道*/
    @NotNull(message = "交易渠道不能为空", groups = FundValidator.Trade.class)
    private Integer tradeChannel;
    /** 金额*/
    @NotNull(message = "金额不能为空", groups = ConstantValidator.Update.class)
    @Min(value = 1, message = "最少1分", groups = ConstantValidator.Update.class)
    private Long amount;
    /** 交易密码*/
    @NotBlank(message = "交易密码不能为空", groups = FundValidator.Trade.class)
    private String tradePwd;
    /** 手续费*/
    @Min(value = 1, message = "手续费最少1分", groups = ConstantValidator.Update.class)
    private Long serviceCost;
    /**备注*/
    private String mark;
    /**额外字段，由各业务逻辑自行决定*/
    private JSONObject extra;
    /** 委托合同编号 */
    private String contractNo;
    /** 委托人ID*/
    private Long consignorId;

    public Integer getTradeChannel() {
        return tradeChannel;
    }

    public void setTradeChannel(Integer tradeChannel) {
        this.tradeChannel = tradeChannel;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getTradePwd() {
        return tradePwd;
    }

    public void setTradePwd(String tradePwd) {
        this.tradePwd = tradePwd;
    }

    public Long getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(Long serviceCost) {
        this.serviceCost = serviceCost;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public JSONObject getExtra() {
        return extra;
    }

    public void setExtra(JSONObject extra) {
        this.extra = extra;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getConsignorId() {
        return consignorId;
    }

    public void setConsignorId(Long consignorId) {
        this.consignorId = consignorId;
    }
}
