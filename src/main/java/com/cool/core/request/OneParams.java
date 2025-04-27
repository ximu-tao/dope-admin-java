package com.cool.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema( description = "单数据查询参数")
public class OneParams{

    @NotNull
    @Schema(description = "数据ID")
    private Long id;


    @Schema(description = "关联查询")
    private List<String> with;

}
