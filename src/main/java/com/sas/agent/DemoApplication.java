package com.sas.agent;


import io.hawt.config.ConfigFacade;
import io.hawt.springboot.EnableHawtio;
import io.hawt.springboot.HawtPlugin;


import io.hawt.system.ConfigManager;
import io.hawt.web.AuthenticationFilter;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;


@SpringBootApplication
@EnableHawtio
@Configuration

public class DemoApplication {

    @Autowired
    private ServletContext servletContext;
    public static void main(String[] args) {
        System.setProperty(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED, "false");
        SpringApplication.run(DemoApplication.class, args);
    }


    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
        registration.setName("CamelServlet");
        return registration;
    }

 /*@Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(
                new CamelHttpTransportServlet(), "/rest/*");
        servlet.setName("CamelServlet");
        return servlet;
    } */

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
     * Loading an example plugin.
     */
    @Bean
    public HawtPlugin samplePlugin() {
        return new HawtPlugin("sample-plugin",
                "/hawtio/plugins",
                "",
                new String[] { "sample-plugin/js/sample-plugin.js" });
    }

}
