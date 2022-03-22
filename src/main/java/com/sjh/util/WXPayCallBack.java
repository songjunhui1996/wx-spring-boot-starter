package com.sjh.util;

/**
 * 微信支付回调接口，使用函数式编程，分离业务逻辑
 * 不接受任何参数，执行业务逻辑之后直接返回结果
 * 业务逻辑结果以boolean类型返回，表示业务逻辑是否执行成功
 * @author 宋俊辉
 * @date 2021年12月10日
 */
@FunctionalInterface
public interface WXPayCallBack<Boolean> {

    /**
     * 执行回调业务逻辑
     * @return
     */
    Boolean apply();
}
