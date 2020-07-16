package com.dili.card.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.card.common.handler.IControllerHandler;
import com.dili.card.dto.FundContractQueryDto;
import com.dili.card.dto.FundContractRequestDto;
import com.dili.card.service.IContractService;
import com.dili.card.validator.ConstantValidator;
import com.dili.ss.domain.BaseOutput;

/**
 * 合同管理
 */
@Controller
@RequestMapping(value = "/contract")
public class ContractManagementController implements IControllerHandler {
    @Autowired
    private IContractService iContractService;

    /**
     * 列表页面
     */
    @GetMapping("/list.html")
    public String listView() {
        return "contract/list";
    }
    
//    /**
//     * 添加页面 第一步
//     */
//    @GetMapping("/addFirst.html")
//    public String addFirstHtml() {
//        return "contract/addFirst";
//    }
//    
//    /**
//     * 添加页面 第二步
//     */
//    @GetMapping("/addSec.html")
//    public String addSecondHtml() {
//        return "contract/addSec";
//    }

    /**
     * 详情合同
     */
    @GetMapping("/detail.html")
    public String detail(Long id, ModelMap modelMap) {
        modelMap.put("detail", iContractService.detail(id));
        return "contract/detail";
    }

    /**
     * 解除页面
     */
    @GetMapping("/remove.html")
    public String removeToPage(Long id, ModelMap modelMap) {
        modelMap.put("detail", iContractService.removeToPage(id));
        return "contract/remove";
    }
    
    /**
     * 新增合同
     */
    @PostMapping("/save.action")
    @ResponseBody
    public BaseOutput<Boolean> save(@RequestBody @Validated FundContractRequestDto fundContractRequest) {
        iContractService.save(fundContractRequest);
        return BaseOutput.success();
    }

    /**
     * 列表合同
     */
    @PostMapping("/page.action")
    @ResponseBody
    public Map<String, Object> page(@Validated(ConstantValidator.Page.class) FundContractQueryDto contractQueryDto) {
        return successPage(iContractService.page(contractQueryDto));
    }

    /**
     * 解除合同
     */
    @PostMapping("/remove.action")
    @ResponseBody
    public BaseOutput<Boolean> remove(@RequestBody FundContractRequestDto fundContractRequest) {
        iContractService.remove(fundContractRequest);
        return BaseOutput.success();
    }
}
