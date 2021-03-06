package com.dili.card.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.card.validator.ConstantValidator;

/**
 * 资金委托合同
 * @author bob
 */
public class FundContractQueryDto extends BaseDto{
	private static final long serialVersionUID = 1L;
	/** 委托人账号ID */
	@NotNull(message = "委托人账号不能为空", groups = ConstantValidator.Query.class)
	@Min(value = 1, message = "委托人账号最小为1", groups = ConstantValidator.Query.class)
	private Long consignorAccountId;
	/** 合同编号 */
	private String contractNo; 
	/** 状态(委托中、已解除、已到期) */
	private Integer state; 
	/** 委托人卡信息 */
	private String cardNo;
	/** 委托人id */
	private Long consignorCustomerId;
	/** 委托人编号 */
	private String consignorCustomerCode;
	/** 被委托人手机号 */
	private String consigneeMobile;
	/** 创建开始时间 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createStartTime;
	/** 创建结束时间 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createEndTime;
	/** 距离到期天数 */
	private Integer days;
	/** 距离到期时间 */
	private String expirationTime;
	/** 市场编号*/
	private Long firmId;
	
	 /**
     * setter for 市场编号
     */
	public void setFirmId(Long firmId) {
		this.firmId = firmId;
	}

    /**
     * getter for 市场编号 
     */
	public Long getFirmId() {
		return firmId;
	}
	
    /**
     * setter for 委托人账号ID
     */
	public void setConsignorAccountId(Long consignorAccountId) {
		this.consignorAccountId = consignorAccountId;
	}

    /**
     * getter for 委托人账号ID
     */
	public Long getConsignorAccountId() {
		return consignorAccountId;
	}

    /**
     * setter for 状态(委托中、已解除、已到期)
     */
	public void setState(Integer state) {
		this.state = state;
	}

    /**
     * getter for 状态(委托中、已解除、已到期)
     */
	public Integer getState() {
		return state;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public LocalDateTime getCreateStartTime() {
		return createStartTime;
	}

	public void setCreateStartTime(LocalDateTime createStartTime) {
		this.createStartTime = createStartTime;
	}

	public LocalDateTime getCreateEndTime() {
		return createEndTime;
	}

	public void setCreateEndTime(LocalDateTime createEndTime) {
		this.createEndTime = createEndTime;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getConsigneeMobile() {
		return consigneeMobile;
	}

	public void setConsigneeMobile(String consigneeMobile) {
		this.consigneeMobile = consigneeMobile;
	}

	public String getConsignorCustomerCode() {
		return consignorCustomerCode;
	}

	public void setConsignorCustomerCode(String consignorCustomerCode) {
		this.consignorCustomerCode = consignorCustomerCode;
	}

	public Long getConsignorCustomerId() {
		return consignorCustomerId;
	}

	public void setConsignorCustomerId(Long consignorCustomerId) {
		this.consignorCustomerId = consignorCustomerId;
	}
}
