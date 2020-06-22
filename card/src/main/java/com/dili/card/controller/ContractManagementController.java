package com.dili.card.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dili.card.dto.FundContractQueryDto;
import com.dili.card.dto.FundContractRequestDto;
import com.dili.card.dto.FundContractResponseDto;
import com.dili.card.service.IContractService;
import com.dili.ss.domain.BaseOutput;

/**
 * 合同管理
 */
@RestController
@RequestMapping(value = "/contract")
public class ContractManagementController {
	@Autowired
	private IContractService iContractService;
	
	/**
	 * 新增合同
	 */
	@PostMapping("/save")
	public BaseOutput<Boolean> save(@RequestBody FundContractRequestDto fundContractRequest) {
		iContractService.save(fundContractRequest);
		return BaseOutput.success();
	}
	
	/**
	 * 列表合同
	 */
	@PostMapping("/list")
	public BaseOutput<List<FundContractResponseDto>> list(@RequestBody FundContractQueryDto contractQueryDto) {
		return BaseOutput.successData(iContractService.list(contractQueryDto));
	}
	
	/**
	 * 详情合同
	 */
	@PostMapping("/detail")
	public BaseOutput<FundContractResponseDto> detail(@RequestBody FundContractRequestDto fundContractRequest) {
		return BaseOutput.successData(iContractService.detail(fundContractRequest));
	}
	
	/**
	 * 解除合同
	 */
	@PostMapping("/remove")
	public BaseOutput<Boolean> remove(@RequestBody FundContractRequestDto fundContractRequest) {
		iContractService.remove(fundContractRequest);
		return BaseOutput.success();
	}
}
