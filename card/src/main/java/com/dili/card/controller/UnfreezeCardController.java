package com.dili.card.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dili.card.dto.FundOpRequestDto;

/**
 * @description：
 *          解冻资金
 * @author ：WangBo
 * @time ：2020年6月29日上午10:38:25
 */
@Controller
@RequestMapping("/unfreeze")
public class UnfreezeCardController {

	/**
	*
	* @author miaoguoxin
	* @date 2020/6/29
	*/
	@RequestMapping(value = "/confirm.action", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelAndView confirm(FundOpRequestDto freezeInfo){
		return null;
	}
}