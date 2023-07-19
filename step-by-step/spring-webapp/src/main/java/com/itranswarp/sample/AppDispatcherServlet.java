package com.itranswarp.sample;

import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;

/**
 * web.xml等效配置:
 * 
 * <code>
 * <?xml version="1.0"?>
 * <web-app>
 *     <servlet>
 *         <servlet-name>dispatcher</servlet-name>
 *         <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 *         <init-param>
 *             <param-name>contextClass</param-name>
 *             <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
 *         </init-param>
 *         <init-param>
 *             <param-name>contextConfigLocation</param-name>
 *             <param-value>com.itranswarp.sample.AppConfig</param-value>
 *         </init-param>
 *     </servlet>
 *     <servlet-mapping>
 *         <servlet-name>dispatcher</servlet-name>
 *         <url-pattern>/</url-pattern>
 *     </servlet-mapping>
 * </web-app>
 * </code>
 */
@WebServlet(urlPatterns = "/", // default servlet
        initParams = { //
                @WebInitParam(name = "contextClass", value = "org.springframework.web.context.support.AnnotationConfigWebApplicationContext"),
                @WebInitParam(name = "contextConfigLocation", value = "com.itranswarp.sample.AppConfig") })
public class AppDispatcherServlet extends DispatcherServlet {

}
