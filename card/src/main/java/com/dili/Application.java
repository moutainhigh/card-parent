package com.dili;

import com.dili.ss.dto.DTOScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 由MyBatis Generator工具自动生成
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.dili.card.dao", "com.dili.ss.dao"})
@ComponentScan(basePackages={"com.dili.ss","com.dili.card", "com.dili.uap.sdk"})
@RestfulScan({"com.dili.uap.sdk.rpc"})
@DTOScan(value={"com.dili.ss", "com.dili.card.domain"})
@EnableDiscoveryClient
@EnableFeignClients
public class Application extends SpringBootServletInitializer {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
