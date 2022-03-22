package com.sjh.pay;


import com.sjh.util.WXPayCallBack;
import com.sjh.util.WXPayRequest;
import com.sjh.util.WXPayUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序支付相关接口
 *
 * @author 宋俊辉
 * @date 2022年3月22日
 */
@Slf4j
public class WxAppletPay {


    private static final String APPLET_UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 微信小程序下单
     *
     * @param totalFee    支付金额
     * @param outTradeNo  自定义参数，微信回调业务方法时会携带该参数
     * @param goodsInfo   商品信息
     * @param openId      支付用户的openid
     * @param appId       appid
     * @param key         微信支付key
     * @param callBackUrl 支付成功之后的回调地址
     * @return
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Long totalFee, String outTradeNo, String goodsInfo, String openId, String appId, String key, String mchId, String callBackUrl) throws Exception {
        String reqBody = WXPayUtil.mapToXml(buildParam(totalFee, goodsInfo, outTradeNo, WXPayUtil.generateNonceStr(), openId, appId, mchId, callBackUrl, key));
        log.info("下单请求参数XML:{}", reqBody);
        String respXml = WXPayRequest.requestOnceWithOutCert(APPLET_UNIFIED_ORDER_URL, reqBody, mchId);
        log.info("下单请求结果XML:{}", respXml);
        try {
            return WXPayUtil.processResponseXml(respXml, key);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 微信支付回调
     *
     * @param wxPayCallBack
     * @return 按照微信文档格式返回
     */
    public String callBack(WXPayCallBack<Boolean> wxPayCallBack) {
        Map<String, String> result = new HashMap<>(2);
        try {
            //执行逻辑
            if (wxPayCallBack.apply()) {
                result.put("return_code", "SUCCESS");
            } else {
                result.put("return_code", "FAIL");
                result.put("return_msg", "未知错误");
            }
        } catch (Exception e) {
            result.put("return_code", "FAIL");
            result.put("return_msg", "未知错误");
        } finally {
            try {
                return WXPayUtil.mapToXml(result);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    /**
     * 生成小程序支付请求参数
     *
     * @param totalFee   支付金额
     * @param outTradeNo 业务订单号
     * @param nonceStr   随机数
     * @return
     */
    private Map<String, String> buildParam(Long totalFee, String goodsInfo, String outTradeNo, String nonceStr, String openId, String appId, String mchId, String callBackUrl, String key) {
        HashMap orderItem = new HashMap(11);
        orderItem.put("appid", appId);
        orderItem.put("mch_id", mchId);
        orderItem.put("nonce_str", nonceStr);
        //商品描述
        orderItem.put("body", goodsInfo);
        //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一。
        orderItem.put("out_trade_no", outTradeNo);
        //订单总金额，单位为分
        orderItem.put("total_fee", totalFee.toString());
        //支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        try {
            orderItem.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            log.info("获取服务器ip失败", e.getMessage(), "使用默认ip");
            orderItem.put("spbill_create_ip", "127.0.0.1");
        }
        orderItem.put("notify_url", callBackUrl);
        //小程序取值如下：JSAPI。
        orderItem.put("trade_type", "JSAPI");
        //trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。
        orderItem.put("openid", openId);
        try {
            orderItem.put("sign", WXPayUtil.generateSignature(orderItem, key));
        } catch (Exception e) {
            log.error("生成sign失败");
        }
        return orderItem;
    }


}
