package com.cool.modules.space.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import org.dromara.autotable.annotation.AutoColumn;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Ignore;
import org.dromara.autotable.annotation.Index;

/**
 * 文件空间信息
 */
@Getter
@Setter
@Table(value = "space_info", comment = "文件空间信息")
public class SpaceInfoEntity extends BaseEntity<SpaceInfoEntity> {
    @AutoColumn(comment = "地址", notNull = true)
    private String url;

    @AutoColumn(comment = "类型", notNull = true)
    private String type;

    @AutoColumn(comment = "分类ID")
    private Integer classifyId;

    @Index()
    @AutoColumn(comment = "文件id")
    private String fileId;

    @AutoColumn(comment = "文件名")
    private String name;

    @AutoColumn(comment = "文件大小")
    private Integer size;

    @AutoColumn(comment = "文档版本", defaultValue = "1")
    private Long version;

    @AutoColumn(comment = "文件位置")
    private String filePath;

    @Ignore
    @Column(ignore = true)
    private String key;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.key = filePath;
    }

    public void setKey(String key) {
        this.key = key;
        this.filePath = key;
    }
}
