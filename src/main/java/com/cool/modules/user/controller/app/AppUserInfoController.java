package com.cool.modules.user.controller.app;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.core.util.EntityUtils;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Tag(name = "用户信息", description = "用户信息")
@CoolRestController
public class AppUserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "用户个人信息", description = "获得App、小程序或者其他应用的用户个人信息")
    @GetMapping("/person")
    public R person() {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        UserInfoEntity userInfoEntity = userInfoService.person(userId);
        return R.ok(EntityUtils.toMap(userInfoEntity,
            "password"));
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/updatePerson")
    public R updatePerson(@RequestBody UserInfoEntity infoEntity ) {
        infoEntity.setId(CoolSecurityUtil.getCurrentUserId());
        
        infoEntity.setPassword(null);
        infoEntity.setPassword(null);
        infoEntity.setStatus(null);
        
        return R.ok(
            userInfoService.updateById(infoEntity)
        );
    }
    
    
    @Data
    public static class UpdateUserInfo{
        
        @Schema( description = "密码")
        private String password;
        
        @Schema( description = "手机号")
        private String phone;
        
        @Schema( description = "验证码")
        private String code;
    }

    @Operation(summary = "更新用户密码")
    @PostMapping("/updatePassword")
    public R updatePassword(@Valid @RequestBody UpdateUserInfo updateUserInfo) {
        userInfoService.updatePassword(CoolSecurityUtil.getCurrentUserId(), updateUserInfo.getPassword() , updateUserInfo.getCode() );
        return R.ok();
    }

    @Operation(summary = "注销")
    @PostMapping("/logoff")
    public R logoff() {
        userInfoService.logoff(CoolSecurityUtil.getCurrentUserId());
        return R.ok();
    }

    @Operation(summary = "绑定手机号")
    @PostMapping("/bindPhone")
    public R bindPhone( @Valid @RequestBody UpdateUserInfo updateUserInfo ) {
        userInfoService.bindPhone(CoolSecurityUtil.getCurrentUserId(), updateUserInfo.getPhone() , updateUserInfo.getCode());
        return R.ok();
    }
}
