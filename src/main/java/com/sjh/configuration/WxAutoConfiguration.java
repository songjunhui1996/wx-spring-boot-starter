package com.sjh.configuration;





import com.sjh.pay.WxAppletPay;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 使用spi方式注入java bean
 * 微信相关API接口注入，开启此配置文件，必须保证yml文件中包含键为wx.miniapp.configs.appId的值
 *
 * @author 宋俊辉
 * @since 2021年11月26日
 */
@Configuration
@ConditionalOnProperty(prefix = "wx.miniapp.configs",name = "appId")
public class WxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WxAppletPay wxPayApi() {
        return new WxAppletPay();
    }


}
