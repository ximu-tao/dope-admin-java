package com.cool.modules.user.entity;

import com.cool.core.base.AppEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import com.tangzc.mybatisflex.autotable.annotation.UniIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.autotable.annotation.Index;

@Data
@Table(value = "user_oauth", comment = "第三方绑定信息")
@Schema( description = "第三方绑定信息")
public class UserOauthEntity extends AppEntity<UserOauthEntity> implements BelongingUserEntity {
    
    @ColumnDefine(comment = "厂商", notNull = true)
    private String provider;
    
    @ColumnDefine(comment = "平台", notNull = true)
    private String platform;

    @Index
    @ColumnDefine(comment = "第三方unionid")
    private String unionid;

    @UniIndex
    @ColumnDefine(comment = "第三方openid", notNull = true)
    private String openid;


    @ColumnDefine(comment = "其他扩展数据" , type = "varchar(512)")
    private String extend;
}
