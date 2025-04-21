package com.cool.modules.user.entity;

import com.cool.core.base.AppEntity;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.autotable.annotation.Index;

import java.util.Map;

@Data
@Table(value = "user_oauth", comment = "第三方绑定信息")
@Schema( description = "第三方绑定信息")
public class UserOauthEntity extends BaseEntity<UserOauthEntity> implements BelongingUserEntity {
    
    @ColumnDefine(comment = "绑定的用户ID，为0表示未绑定用户", notNull = true)
    private Long userId;
    
    @ColumnDefine(comment = "厂商", notNull = true)
    private String provider;
    
    @ColumnDefine(comment = "平台", notNull = true)
    private String platform;

    @UniIndex
    @ColumnDefine(comment = "第三方unionid")
    private String unionid;

    @UniIndex
    @ColumnDefine(comment = "第三方openid", notNull = true)
    private String openid;


    @ColumnDefine(comment = "其他扩展数据", type = "json", notNull = true)
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Map<String,Object> extend;
}
