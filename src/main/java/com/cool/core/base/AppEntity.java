package com.cool.core.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.activerecord.Model;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

import java.io.Serializable;

@Getter
@Setter
public class AppEntity<T extends Model<T>> extends BaseEntity<T> implements Serializable {
    
    @Column(comment = "用户ID")
    @Schema( description = "用户ID（无需提交）")
    @ColumnDefine(comment = "用户ID", notNull = true)
    protected Long userId;
    
    @Column(isLogicDelete = true)
    @ColumnDefine(comment = "是否删除")
    @Schema( hidden = true)
    protected Boolean isDelete;
    
}
