package com.sas.agent;

import io.hawt.config.ConfigFacade;

import io.hawt.springboot.PluginService;
import io.hawt.system.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

public class ServletInitializer extends SpringBootServletInitializer {

	@Autowired
	private ServletContext servletContext;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DemoApplication.class);
	}

	@PostConstruct
	public void init() {
		final ConfigManager configManager = new ConfigManager();
		configManager.init();
		servletContext.setAttribute("ConfigManager", configManager);
	}



	/**
	 * Set things up to be in offline mode
	 * @return
	 * @throws Exception
	 */
	@Bean
	public ConfigFacade configFacade() throws Exception {
		ConfigFacade config = new ConfigFacade() {
			public boolean isOffline() {
				return true;
			}
		};
		config.init();
		return config;
	}

	/**
	 * Register rest endpoint to handle requests for /plugin, and return all registered plugins.
	 * @return
	 */
	@Bean
	public PluginService pluginService(){
		return new PluginService();
	}

}
