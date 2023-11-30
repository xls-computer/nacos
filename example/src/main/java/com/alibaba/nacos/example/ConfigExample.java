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

package com.alibaba.nacos.example;

import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * Config service example.
 *  配置的上传和获取
 * @author Nacos
 */
public class ConfigExample {

    public static void main(String[] args) throws NacosException, InterruptedException {
        String serverAddr = "localhost";
        String dataId = "test";
        String group = "DEFAULT_GROUP";
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        //首次获取配置信息，为null
        String content = configService.getConfig(dataId, group, 5000);
        System.out.println("[config content] " + content);
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("receive:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });

        //发布配置信息
        boolean isPublishOk = configService.publishConfig(dataId, group, "content");
        System.out.println("[publish result] " + isPublishOk);

        Thread.sleep(3000);
        //获取配置信息
        content = configService.getConfig(dataId, group, 5000);
        System.out.println("[config content]: " + content);

        //删除配置信息
        boolean isRemoveOk = configService.removeConfig(dataId, group);
        System.out.println("[delete result]: " + isRemoveOk);
        Thread.sleep(3000);

        //再次获取配置信息
        content = configService.getConfig(dataId, group, 5000);
        System.out.println("[config content]: " + content);
        Thread.sleep(300000);

    }
}
