package com.clinxin.picmanagerbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.clinxin.picmanagerbackend.mapper")
public class PicManagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicManagerBackendApplication.class, args);
    }

}
