package com.dili.card.rpc.resolver;

import com.dili.card.dto.SerialQueryDto;
import com.dili.card.entity.SerialRecordDo;
import com.dili.card.exception.CardAppBizException;
import com.dili.card.rpc.SerialRecordRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 操作流水rpc解析类
 * @author xuliang
 */
@Component
public class SerialRecordRpcResolver {

    @Resource
    private SerialRecordRpc serialRecordRpc;

    /**
     * 批量存储
     * @param serialRecordDoList
     * @return
     */
    public void batchSave(List<SerialRecordDo> serialRecordDoList) {
        BaseOutput<?> baseOutput = serialRecordRpc.batchSave(serialRecordDoList);
        if (!baseOutput.isSuccess()) {
            throw new CardAppBizException("保存操作流水失败");
        }
    }

    /**
     * 分页查询操作流水记录
     * @param serialQueryDto
     * @return
     */
    public PageOutput<List<SerialRecordDo>> listPage(SerialQueryDto serialQueryDto) {
        return serialRecordRpc.listPage(serialQueryDto);
    }
}
