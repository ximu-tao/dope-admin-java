package com.cool.core.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.enums.QueryModeEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.CrudOption;
import com.cool.core.request.PageResult;
import com.cool.core.request.R;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

public abstract class AdminController <S extends BaseService<T>, T extends BaseEntity<T>> extends BaseController<S,T> {
    
    
    /**
     * 新增
     * <p>
     * // * @param t 实体对象
     */
    @Operation(summary = "新增", description = "新增信息，对应后端的实体类")
    @PostMapping("/add")
    protected R add(@RequestAttribute() JSONObject requestParams) {
        String body = requestParams.getStr("body");
        if (JSONUtil.isTypeJSONArray(body)) {
            JSONArray array = JSONUtil.parseArray(body);
            return R.ok(Dict.create()
                .set("ids", service.addBatch(requestParams, array.toList(currentEntityClass()))));
        } else {
            return R.ok(Dict.create().set("id",
                service.add(requestParams, requestParams.toBean(currentEntityClass()))));
        }
    }

    /**
     * 删除
     *
     * @param params 请求参数 ids 数组 或者按","隔开
     */
    @Operation(summary = "删除", description = "支持批量删除 请求参数 ids 数组 或者按\",\"隔开")
    @PostMapping("/delete")
    protected R delete(HttpServletRequest request, @RequestBody Map<String, Object> params,
        @RequestAttribute() JSONObject requestParams) {
        service.delete(requestParams, Convert.toLongArray(getIds(params)));
        return R.ok();
    }

    /**
     * 修改
     *
     * @param t 修改对象
     */
    @Operation(summary = "修改", description = "根据ID修改")
    @PostMapping("/update")
    protected R update(@RequestBody T t, @RequestAttribute() JSONObject requestParams) {
        Long id = t.getId();
        JSONObject info = JSONUtil.parseObj(JSONUtil.toJsonStr(service.getById(id)));
        requestParams.forEach(info::set);
        info.set("updateTime", new Date());
        service.update(requestParams, JSONUtil.toBean(info, currentEntityClass()));
        return R.ok();
    }

    /**
     * 信息
     *
     * @param id ID
     */
    @Operation(summary = "信息", description = "根据ID查询单个信息")
    @GetMapping("/info")
    protected R<T> info(@RequestAttribute() JSONObject requestParams,
        @RequestParam() Long id,
        @RequestAttribute(COOL_INFO_OP) CrudOption<T> option) {
        T info = service.info(requestParams, id, null);
        invokerTransform(option, info);
        return R.ok(info);
    }

    /**
     * 列表查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "查询", description = "查询多个信息")
    @PostMapping("/list")
    protected R<List<T>> list(@RequestAttribute() JSONObject requestParams,
        @RequestAttribute(COOL_LIST_OP) CrudOption<T> option) {
        QueryModeEnum queryModeEnum = option.getQueryModeEnum();
        List list = (List) switch (queryModeEnum) {
            case ENTITY_WITH_RELATIONS -> service.listWithRelations(requestParams, option.getQueryWrapper(currentEntityClass()), null);
            case CUSTOM -> transformList(service.list(requestParams, option.getQueryWrapper(currentEntityClass()), option.getAsType()), option.getAsType());
            default -> service.list(requestParams, option.getQueryWrapper(currentEntityClass()));
        };
        invokerTransform(option, list);
        return R.ok(list);
    }

    /**
     * 分页查询
     *
     * @param requestParams 请求参数
     */
    @Operation(summary = "分页", description = "分页查询多个信息")
    @PostMapping("/page")
    protected R<PageResult<T>> page(@RequestAttribute() JSONObject requestParams,
                                    @RequestAttribute(COOL_PAGE_OP) CrudOption<T> option) {
        Integer page = requestParams.getInt("page", 1);
        Integer size = requestParams.getInt("size", 20);
        QueryModeEnum queryModeEnum = option.getQueryModeEnum();
        Page<T> obj = switch (queryModeEnum) {
            case ENTITY_WITH_RELATIONS -> service.pageWithRelations(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()), null);
            case CUSTOM -> transformPage(service.page(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()), option.getAsType()), option.getAsType());
            default -> service.page(requestParams, new Page<>(page, size), option.getQueryWrapper(currentEntityClass()));
        };
        invokerTransform(option, obj.getRecords());
        return R.ok(pageResult(obj));
    }
    
        /**
     * 转换参数，组装数据
     */
    private void invokerTransform(CrudOption<T> option, Object obj) {
        if (ObjUtil.isNotEmpty(option.getTransform())) {
            if (obj instanceof List) {
                ((List)obj).forEach(o -> {
                    option.getTransform().apply(o);
                });
            } else {
                option.getTransform().apply(obj);
            }
        }
    }
    
    protected List<Long> getIds(Map<String, Object> params) {
        Object ids = params.get("ids");
        CoolPreconditions.checkEmpty(ids, "ids 参数错误");
        if (!(ids instanceof ArrayList)) {
            ids = ids.toString().split(",");
        }
        return Convert.toList(Long.class, ids);
    }
}
