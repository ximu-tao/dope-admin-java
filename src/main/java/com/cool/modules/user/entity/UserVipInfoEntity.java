package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Table(value = "user_vip_info" , comment = "会员开通配置")
@Schema( description = "会员开通配置")
public class UserVipInfoEntity extends BaseEntity<UserVipInfoEntity> {
    
    @ColumnDefine(comment = "价格", notNull = true)
    private BigDecimal price;
    
    @ColumnDefine(comment = "原价" )
    private Double oldPrice;
    
    @ColumnDefine(comment = "标题" )
    private String title;
    
    @ColumnDefine(comment = "会员天数" )
    private Integer day;
    
}
