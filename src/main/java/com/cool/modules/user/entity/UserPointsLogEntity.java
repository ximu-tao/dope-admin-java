package com.cool.modules.user.entity;

import com.cool.core.base.AppEntity;
import com.cool.core.base.BelongingUserEntity;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Table(value = "user_points_log", comment = "用户积分变更记录")
@Data
@Schema( description = "用户积分变更记录")
public class UserPointsLogEntity extends AppEntity<UserPointsLogEntity> implements BelongingUserEntity {
    
    
    @AutoColumn(comment = "变更前积分", notNull = true)
    @Schema(description = "变更前积分")
    private Integer oldPoints;
    
    @AutoColumn(comment = "变更积分", notNull = true)
    @Schema(description = "变更积分")
    private Integer points;
    
    @AutoColumn(comment = "变更后积分", notNull = true)
    @Schema(description = "变更后积分")
    private Integer newPoints;
    
    @AutoColumn(comment = "备注", notNull = true)
    @Schema(description = "备注")
    private String remarks;
}
