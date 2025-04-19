package com.cool.core.request;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Schema( description = "分页查询数据模型")
public class PageParams<T> {
    
    @Schema(description = "页码" , defaultValue = "1")
    @Min(value = 1 , message = "必须大于0")
    protected Integer page;
    
    @Min(value = 1 , message = "必须大于0")
    @Schema(description = "分页大小", defaultValue = "10")
    protected Integer size;
    
    @Schema(description = "模糊查询" )
    protected String keyWord;
    
    
    @NotNull( message = "params 必须存在，至少提供一个空对象 {} ")
    protected T params;
    
    @Schema(description = "排序字段" , defaultValue = "id")
    protected String orderBy = "id";
    
    @Schema(description = "排序" , defaultValue = "desc")
    protected String order = "desc";


    public Integer getPage() {
        if (page == null) {
            return 1;
        }
        return page;
    }

    public Integer getSize() {
        if (size == null) {
            return 20;
        }
        return size;
    }
    
    public Integer getPage( Integer defaultPage ) {
        if ( page == null || size == 0 ) {
            return defaultPage;
        }
        return page;
    }
    
    public Integer getSize( Integer defaultSize ) {
        if ( size == null || size == 0 ) {
            return defaultSize;
        }
        return size;
    }
    
    public String getOrderBy() {
        if (StringUtils.isEmpty(orderBy)) {
            return "id";
        }else {
            return orderBy;
        }
    }
    
    
    public Page<T> toPage(){
        return Page.of( page , size );
    }
}
