package com.nivalsoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSON;
import com.nivalsoul.config.ESConfig;
import com.nivalsoul.dao.Dao;


public class DoInit implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//防止重复执行。
        if(event.getApplicationContext().getParent() == null){
        	try {
        		// 取到上下文中配置文件中的信息
        		ESConfig config = event.getApplicationContext().getBean(ESConfig.class);
        		Logger logger = LoggerFactory.getLogger(DoInit.class.getName());
        		logger.info("****first time init...****");
        		logger.info(JSON.toJSON(config).toString());
        		Dao.init(config);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
	}
}
