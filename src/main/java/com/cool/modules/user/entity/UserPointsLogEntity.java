package com.cool.modules.user.entity;

import com.cool.core.base.AppEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Table(value = "user_points_log", comment = "用户积分变更记录")
@Data
@Schema( description = "用户积分变更记录")
public class UserPointsLogEntity extends AppEntity<UserPointsLogEntity> {
    
    
    @ColumnDefine(comment = "变更前积分", notNull = true)
    @Schema(description = "变更前积分")
    private Integer oldPoints;
    
    @ColumnDefine(comment = "变更积分", notNull = true)
    @Schema(description = "变更积分")
    private Integer points;
    
    @ColumnDefine(comment = "变更后积分", notNull = true)
    @Schema(description = "变更后积分")
    private Integer newPoints;
    
    @ColumnDefine(comment = "备注", notNull = true)
    @Schema(description = "备注")
    private String remarks;
}
