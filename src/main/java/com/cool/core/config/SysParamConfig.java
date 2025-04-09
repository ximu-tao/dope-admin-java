package com.cool.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "cool.sysParam")
@Data
public class SysParamConfig {
    private List<String> allowKeys;
}
