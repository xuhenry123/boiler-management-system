package com.boiler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 配置CORS（跨域资源共享）策略，允许前端应用跨域访问后端API
 */
@Configuration
public class CorsConfig {
    
    /**
     * 配置跨域过滤器
     * 允许所有来源、所有请求头和所有HTTP方法的跨域请求
     * @return CorsFilter实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源_pattern
        config.addAllowedOriginPattern("*");
        // 允许携带凭证（如Cookie）
        config.setAllowCredentials(true);
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有HTTP方法
        config.addAllowedMethod("*");
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有API路径应用跨域配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
