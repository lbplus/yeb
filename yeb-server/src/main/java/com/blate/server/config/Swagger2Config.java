package com.blate.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2配置类
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean//规定扫描哪些包下面生成swagger2文档
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())//设置基本介绍信息
                .select()//选择扫描哪个包
                .apis(RequestHandlerSelectors.basePackage("com.blate.server.controller"))
                .paths(PathSelectors.any())//所有的路径都可以
                .build()
                //给swagger2令牌，不然测试接口太繁琐，需要登录会被拦截
                .securityContexts(securityContexts())//全局
                .securitySchemes(securitySchemes());//安全计划

    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //标题
                .title("云端办公系统接口文档")
                //描述
                .description("项目介绍 本项目目的是实现中小型企业的在线办公系统，云E办在线办公系统是一个用来管理日常的办公事务的 一个系统，他能够管的内容有：日常的各种流程审批，新闻，通知，公告，文件信息，财务，人事，费 用，资产，行政，项目，移动办公等等。它的作用就是通过软件的方式，方便管理，更加简单，更加扁 平。更加高效，更加规范，能够提高整体的管理运营水平。 本项目在技术方面采用最主流的前后端分离开发模式，使用业界最流行、社区非常活跃的开源框架 Spring Boot来构建后端，旨在实现云在线办公系统。包括职位管理、职称管理、部门管理、员工管 理、工资管理、在线聊天等模块。")
                .contact(new Contact("blate","http:localhost:80/doc.html","666666@qq.com"))
                .version("1.0")
                .build();
    }

    private List<ApiKey> securitySchemes(){
        //设置请求头信息
        List<ApiKey> result = new ArrayList<>();
        //令牌
        ApiKey apikey = new ApiKey("authorization","authorization","Header");
        result.add(apikey);
        return result;
    }

    private List<SecurityContext> securityContexts(){
        //设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextBypath("/hello/.*"));
        return result;
    }

    private SecurityContext getContextBypath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization",authorizationScopes));
        return result;
    }

}
