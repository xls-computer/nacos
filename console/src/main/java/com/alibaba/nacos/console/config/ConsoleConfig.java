/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.console.config;

import com.alibaba.nacos.console.filter.XssFilter;
import com.alibaba.nacos.core.code.ControllerMethodsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.time.ZoneId;

/**
 * Console config.
 *
 * @author yshen
 * @author nkorange
 * @since 1.2.0
 */
@Component
@EnableScheduling
@PropertySource("/application.properties")
public class ConsoleConfig {
    
    @Autowired
    private ControllerMethodsCache methodsCache;
    
    @Value("${nacos.console.ui.enabled:true}")
    private boolean consoleUiEnabled;
    
    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        methodsCache.initClassMethod("com.alibaba.nacos.core.controller");
        methodsCache.initClassMethod("com.alibaba.nacos.naming.controllers");
        methodsCache.initClassMethod("com.alibaba.nacos.config.server.controller");
        methodsCache.initClassMethod("com.alibaba.nacos.console.controller");
    }

    //设置跨域资源共享（由于浏览器为了安全采用的同源策略【协议+域名+端口】，非同源是无法共享cookie、session、发送ajax请求，因为现在很多都是采用的前后端分离，不符合同源策略，所以要配置跨域共享）
    //https://zhuanlan.zhihu.com/p/118381660
    //做法：在跨域访问时，给返回的response加上Access-Control-Allow-Origin：告诉浏览器允许向服务端请求资源的域名
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //凭证允许（跨域返回cookie）
        config.setAllowCredentials(true);
        //content-type允许类型
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        //允许访问的方法
        config.addAllowedMethod("*");
        //允许访问的源站
        config.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //所有路径都进行过滤
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    //方式XSS跨域脚本攻击
    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }

    //当使用JSON格式时，Spring Boot将使用ObjectMapper实例来序列化响应并反序列化请求。
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(ZoneId.systemDefault().toString());
    }
    
    public boolean isConsoleUiEnabled() {
        return consoleUiEnabled;
    }
}
