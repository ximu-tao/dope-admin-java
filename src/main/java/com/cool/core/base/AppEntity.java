package com.cool.core.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.activerecord.Model;
import org.dromara.autotable.annotation.AutoColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class AppEntity<T extends Model<T>> extends BaseEntity<T> implements Serializable, BelongingUserEntity {
    
    @Column(comment = "用户ID")
    @Schema( description = "用户ID（无需提交）")
    @AutoColumn(comment = "用户ID", notNull = true)
    protected Long userId;
    
    @Column(isLogicDelete = true)
    @AutoColumn(comment = "是否删除")
    @Schema( hidden = true)
    protected Boolean isDelete;
    
}
