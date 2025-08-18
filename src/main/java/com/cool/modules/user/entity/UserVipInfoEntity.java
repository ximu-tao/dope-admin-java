package com.cool.modules.user.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Table(value = "user_vip_info" , comment = "会员开通配置")
@Schema( description = "会员开通配置")
public class UserVipInfoEntity extends BaseEntity<UserVipInfoEntity> {
    
    @AutoColumn(comment = "价格", notNull = true)
    private BigDecimal price;
    
    @AutoColumn(comment = "原价" )
    private Double oldPrice;
    
    @AutoColumn(comment = "标题" )
    private String title;
    
    @AutoColumn(comment = "会员天数" )
    private Integer day;
    
}
