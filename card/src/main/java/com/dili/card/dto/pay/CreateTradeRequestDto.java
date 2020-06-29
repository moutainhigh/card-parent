package com.dili.card.dto.pay;

/**
 * 创建交易请求dto
 * @author xuliang
 */
public class CreateTradeRequestDto {

    /** 交易类型*/
    private Integer type;
    /** 收款/资金账号*/
    private Long accountId;
    /** 操作金额-分*/
    private Long amount;
    /** 外部流水号*/
    private String serialNo;
    /** 账务周期号*/
    private String cycleNo;
    /** 交易备注*/
    private String description;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCycleNo() {
        return cycleNo;
    }

    public void setCycleNo(String cycleNo) {
        this.cycleNo = cycleNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}