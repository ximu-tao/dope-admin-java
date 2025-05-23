package com.cool.modules.base.controller.app;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.config.CoolProperties;
import com.cool.core.eps.CoolEps;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.file.FileUploadStrategyFactory;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.BaseSysParamEntity;
import com.cool.modules.base.service.sys.BaseSysParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * app通用接口
 */
@RequiredArgsConstructor
@Tag(name = "应用通用", description = "应用通用")
@CoolRestController
public class AppBaseCommController {

    private final CoolEps coolEps;

    private final BaseSysParamService baseSysParamService;

    final private CoolProperties coolProperties;

    final private FileUploadStrategyFactory fileUploadStrategyFactory;

    @TokenIgnore
    @Operation(summary = "参数配置")
    @GetMapping("/param")
    public R param(@RequestAttribute() JSONObject requestParams) {
        String key = requestParams.get("key", String.class);
        BaseSysParamEntity byKey = baseSysParamService.getByKey(key);
        CoolPreconditions.checkEmpty(byKey, "非法操作");
        CoolPreconditions.check(!byKey.getOpen(), "非法操作");
        return R.ok(byKey.getData());
    }

    @TokenIgnore
    @Operation(summary = "参数配置")
    @GetMapping("/page/{key}")
    public String page(@PathVariable("key") String key) {
        BaseSysParamEntity byKey = baseSysParamService.getByKey(key);
        CoolPreconditions.checkEmpty(byKey, "非法操作");
        CoolPreconditions.check(!byKey.getOpen(), "非法操作");
        return """
                <!DOCTYPE html><html lang=en><title>"""
                + byKey.getName() +
                """
                        </title> <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;"/>
                        <style>
                            * {
                                box-sizing: border-box;
                                margin: 0;
                                padding: 0;
                            }
                        
                            .text {
                                word-wrap: break-word;
                                overflow-wrap: break-word;
                                word-break: break-word;
                                white-space: normal;
                                font-family: Simsun;
                                width: 100%;
                                max-width: 100vw;
                            }
                        
                            .text a {
                                display: inline;
                            }
                        
                            img {
                                max-width: 100vw;
                                height: auto;
                            }
                        
                            p {
                                text-indent: 2em;
                            }
                        
                            .ql-align-center {
                                text-indent: 0em;
                            }
                        
                        </style>
                        
                        <body style="width: 100vw;">
                        <div class="body text" style="line-height: 4vw; font-size: 4vw;">
                        """
                + byKey.getData() +
                """
                        </div>
                        </body>
                        </html>
                        """;
    }


    @TokenIgnore
    @Operation(summary = "实体信息与路径", description = "系统所有的实体信息与路径，供前端自动生成代码与服务")
    @GetMapping("/eps")
    public R eps() {
        return R.ok(coolEps.getApp());
    }


    @Operation(summary = "文件上传")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.ALL_VALUE})
    public R upload(@RequestPart(value = "file", required = false) @Parameter(description = "文件") MultipartFile[] files,
                    HttpServletRequest request) {
        return R.ok(fileUploadStrategyFactory.upload(files, request));
    }

    @Operation(summary = "文件上传模式")
    @GetMapping("/uploadMode")
    public R uploadMode() {
        return R.ok(fileUploadStrategyFactory.getMode());
    }
}
