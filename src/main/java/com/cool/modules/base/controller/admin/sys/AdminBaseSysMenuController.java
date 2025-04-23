package com.cool.modules.base.controller.admin.sys;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.AdminController;
import com.cool.core.base.BaseController;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.CrudOption;
import com.cool.core.request.R;
import com.cool.core.util.I18nUtil;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import com.cool.modules.base.service.sys.BaseSysMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统菜单
 */
@Tag(name = "系统菜单", description = "系统菜单")
@CoolRestController(api = {"add", "delete", "update", "page", "list", "info"})
public class AdminBaseSysMenuController extends
        AdminController<BaseSysMenuService, BaseSysMenuEntity> {


    @Operation(summary = "新增", description = "新增信息，对应后端的实体类")
    @PostMapping("/add")
    @Override
    protected R add(@RequestAttribute() JSONObject requestParams) {
        String body = requestParams.getStr("body");
        if (JSONUtil.isTypeJSONArray(body)) {
            JSONArray array = JSONUtil.parseArray(body);
            Object ids = service.addBatch(requestParams, array.toList(currentEntityClass()));
            
            return R.ok(Dict.create().set("ids", ids ));
        } else {
            BaseSysMenuEntity bean = requestParams.toBean(currentEntityClass());
            
            try {
                Object id = service.add(requestParams, bean);
                return R.ok(Dict.create().set("id", id ) );
            } catch (DuplicateKeyException e) {
                QueryWrapper queryWrapper = QueryWrapper.create()
                        .eq( BaseSysMenuEntity::getRouter , bean.getRouter() );
                List<BaseSysMenuEntity> list = this.service.list(queryWrapper);
                BaseSysMenuEntity baseSysMenuEntity = list.get(0);
                return R.ok(Dict.create().set("id", baseSysMenuEntity.getId() ) );
            }
        }
    }

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        CrudOption<BaseSysMenuEntity> transform = createOp()
            .transform(o -> {
                BaseSysMenuEntity entity = (BaseSysMenuEntity) o;
                entity.setName(I18nUtil.getI18nMenu(entity.getName()));
            });
        setPageOption(transform);
        setListOption(transform);
        setInfoOption(transform);
    }

    @Operation(summary = "创建代码", description = "创建代码")
    @PostMapping("/create")
    public R create(@RequestBody() Map<String, Object> params) {
        CoolPreconditions.checkEmpty(params.get("module"), "module参数不能为空");
        CoolPreconditions.checkEmpty(params.get("entity"), "entity参数不能为空");
        CoolPreconditions.checkEmpty(params.get("controller"), "controller参数不能为空");
        CoolPreconditions.checkEmpty(params.get("service"), "service参数不能为空");
        CoolPreconditions.checkEmpty(params.get("service-impl"), "service-impl参数不能为空");
        CoolPreconditions.checkEmpty(params.get("mapper"), "mapper参数不能为空");
        CoolPreconditions.checkEmpty(params.get("fileName"), "fileName参数不能为空");
        this.service.create(params);
        return R.ok();
    }

    @Operation(summary = "导出", description = "导出")
    @PostMapping("/export")
    public R export(@RequestBody Map<String, Object> params) {
        return R.ok(this.service.export(getIds(params)));
    }

    @Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    public R importMenu(@RequestBody Map<String, List<BaseSysMenuEntity>> params) {
        CoolPreconditions.checkEmpty(params.get("menus"), "参数不能为空");
        return R.ok(this.service.importMenu(params.get("menus")));
    }
}