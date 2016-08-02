package com.nivalsoul;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.nivalsoul.config.ESConfig;
import com.nivalsoul.config.FileSaveConfig;

@SpringBootApplication
@EnableSwagger
@EnableConfigurationProperties({FileSaveConfig.class, ESConfig.class})
public class Application extends SpringBootServletInitializer{
	static Logger logger = LoggerFactory.getLogger(Application.class);
	
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		//app.addListeners(new DoInit());
		app.run(args);
		logger.info("Hello World");
	}
	
	/**
	 * 实现启动服务后做一些初始化工作
	 * @return
	 */
	@Bean
    protected DoInit listener() {
		logger.info("do init===");
        return new DoInit ();
    } 
	
	@Bean
	public FilterRegistrationBean delegateFilter(ServletContext servletContext)
			throws ServletException {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new AuthFilter());
		filterRegistrationBean.setEnabled(true);
		filterRegistrationBean.addUrlPatterns("/v1/*");
		return filterRegistrationBean;
	}

}
