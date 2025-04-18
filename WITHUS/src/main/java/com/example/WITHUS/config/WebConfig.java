package com.example.WITHUS.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile_images/**") // URL에서 접근할 경로
                .addResourceLocations("file:///C:/community_uploads/profiles/"); // 실제 서버 경로

        // 업로드된 파일을 /uploaded/** URL로 접근 가능하게 설정
        registry.addResourceHandler("/uploaded/**")
                .addResourceLocations("file:///C:/withus_uploads/")  // 실제 파일 경로
                .setCachePeriod(3600); // 캐시 시간 설정 (선택)

    }


}
