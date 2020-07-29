<script>
    $("#frozen-account-form").validate({
        onkeyup: false,
        rules: {
            mark: {
                maxlength: 30
            }
        },
        messages: {
            mark: {
                maxlength: "最多可以输入{0}个字符"
            }
        },
        focusCleanup: true
    });
    //提交冻结账户操作
    function submitFrozenAccount() {
        let requestData = {
            cardNo:${detail.cardAssociation.primary.cardNo!},
            accountId:${detail.cardAssociation.primary.accountId!},
            customerId:${detail.cardAssociation.primary.customerId!},
        };
        let url = '${contextPath}/account/frozen.action';

        if (!$.validate.form('frozen-account-form')) {
            return;
        }
        $.modal.confirm("注意：账户冻结后，所有功能业务均不可用，确认冻结当前账户吗?", function (sure) {
            if (!sure) {
                return;
            }
            let data = $.common.formToJSON('frozen-account-form');
            $.operate.post(url, $.extend(requestData, data),function (result) {
                if (result.code == '200'){
                    $.tab.refresh();
                }
            });
        });
    }

    //表格隐藏、显示状态切换
    function showOrHideFrozenAccountRecord() {
        let $table = $("#table-div-frozen-account");
        if ($table.is(":hidden")) {
            $table.show();
            $.table.refresh()
        } else {
            $table.hide();
        }
    }

    //初始化表格
    $(() => {
        let options = {
        	id: "frozenAccuntTable",
            url: "${contextPath}/serial/business/page.action",
            sortName: "operate_time",
            modalName: "冻结账户记录"
        };
        $.table.init(options);
    });

</script>