package com.cool.modules.user.entity;

import com.cool.core.annotation.EpsField;
import com.cool.core.annotation.EspRemoteSelectField;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

/**
 * 用户模块-收货地址
 */
@Getter
@Setter
@Table(value = "user_address", comment = "用户模块-收货地址")
@Schema( description = "用户收货地址" )
public class UserAddressEntity extends BaseEntity<UserAddressEntity> implements BelongingUserEntity {

    @Index
    @AutoColumn(comment = "用户ID", notNull = true)
    @EspRemoteSelectField( titleField = "nickName", clazz = UserInfoEntity.class)
    @EpsField( immutable = true )
    @Schema( description = "用户ID" )
    private Long userId;

    @AutoColumn(comment = "联系人", notNull = true)
    @Schema( description = "联系人" )
    private String contact;

    @Index
    @AutoColumn(comment = "手机号", length = 11, notNull = true)
    @Schema( description = "手机号" )
    private String phone;

    @AutoColumn(comment = "省", notNull = true)
    @Schema( description = "省" )
    private String province;

    @AutoColumn(comment = "市", notNull = true)
    @Schema( description = "市" )
    private String city;

    @AutoColumn(comment = "区", notNull = true)
    @Schema( description = "区" )
    private String district;

    @AutoColumn(comment = "地址", notNull = true)
    @Schema( description = "地址" )
    private String address;

    @AutoColumn(comment = "是否默认", defaultValue = "false")
    @Schema( description = "是否默认" )
    private Boolean isDefault;
}
