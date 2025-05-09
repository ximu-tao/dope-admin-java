package com.cool.modules.user.controller.app.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Schema( description = "登录成功" )
@Accessors( chain = true )
@Builder
public class LoginResponse implements Serializable {
    
    @Schema(description = "TOKEN")
    private String token;
    
    @Schema( description = "TOKEN过期时间" )
    private Long expire;
    
        
    @Schema(description = "refreshToken")
    private String refreshToken;
    
    @Schema( description = "refreshExpire 过期时间" )
    private Long refreshExpire;
    
}
