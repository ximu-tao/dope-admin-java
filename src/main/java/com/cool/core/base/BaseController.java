package com.cool.core.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 控制层基类
 *
 * @param <S>
 * @param <T>
 */
public abstract class BaseController<S extends BaseService<T>, T extends BaseEntity<T>> {

    @Getter
    @Autowired
    protected S service;
    protected Class<T> entityClass;

    @Getter
    private HttpServletRequest request;
    
    protected final String COOL_PAGE_OP = "COOL_PAGE_OP";
    protected final String COOL_LIST_OP = "COOL_LIST_OP";
    protected final String COOL_INFO_OP = "COOL_INFO_OP";

    private final ThreadLocal<CrudOption<T>> pageOption = new ThreadLocal<>();
    private final ThreadLocal<CrudOption<T>> listOption = new ThreadLocal<>();
    private final ThreadLocal<CrudOption<T>> infoOption = new ThreadLocal<>();
    private final ThreadLocal<JSONObject> requestParams = new ThreadLocal<>();

    @ModelAttribute
    protected void preHandle(HttpServletRequest request,
        @RequestAttribute JSONObject requestParams) {
        
        this.request = request;
        
        String requestPath = ((ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest().getRequestURI();
        if (!requestPath.endsWith("/page") && !requestPath.endsWith("/list")
            && !requestPath.endsWith("/info")) {
            // 非page或list不执行
            return;
        }
        this.pageOption.set(new CrudOption<>(requestParams));
        this.listOption.set(new CrudOption<>(requestParams));
        this.infoOption.set(new CrudOption<>(requestParams));
        this.requestParams.set(requestParams);
        init(request, requestParams);
        request.setAttribute(COOL_PAGE_OP, this.pageOption.get());
        request.setAttribute(COOL_LIST_OP, this.listOption.get());
        request.setAttribute(COOL_INFO_OP, this.infoOption.get());

        removeThreadLocal();
    }

    /**
     * 手动移除变量
     */
    private void removeThreadLocal() {
        this.listOption.remove();
        this.pageOption.remove();
        this.requestParams.remove();
    }

    public CrudOption<T> createOp() {
        return new CrudOption<>(this.requestParams.get());
    }

    public void setInfoOption(CrudOption<T> infoOption) {
        this.infoOption.set(infoOption);
    }

    public void setListOption(CrudOption<T> listOption) {
        this.listOption.set(listOption);
    }

    public void setPageOption(CrudOption<T> pageOption) {
        this.pageOption.set(pageOption);
    }

    protected void init(HttpServletRequest request, JSONObject requestParams){
        setPageOption(
            createOp()
                .keyWordLikeFields( service.getKeyWordQueryColumn() )
                .fieldEq( service.getAllQueryColumn() )
                .select( service.getListSelectQueryColumn() )
                );

        setListOption(
            createOp()
                .keyWordLikeFields( service.getKeyWordQueryColumn() )
                .fieldEq( service.getAllQueryColumn() )
                .select( service.getListSelectQueryColumn() )
                );
    }


    /**
     * 分页结果
     *
     * @param page 分页返回数据
     */
    protected PageResult<T> pageResult(Page<T> page) {
        return PageResult.of(page);
    }

    public Class<T> currentEntityClass() {
        if (entityClass != null) {
            return this.entityClass;
        }
        // 使用  获取泛型参数类型
        Type type = TypeUtil.getTypeArgument(this.getClass(), 1); // 获取第二个泛型参数
        if (type instanceof Class<?>) {
            entityClass = (Class<T>) type;
            return entityClass;
        }
        throw new IllegalStateException("Unable to determine entity class type");
    }



    /**
     * 适用于自定义返回值为 map，map 的key为数据库字段，转驼峰命名
     */
    protected List transformList(List records, Class<?> asType) {
        if (ObjUtil.isEmpty(asType) || !Map.class.isAssignableFrom(asType)) {
            return records;
        }
        List<Map> list = new ArrayList<>();
        Editor<String> keyEditor = property -> StrUtil.toCamelCase(property);
        records.forEach(o ->
            list.add(BeanUtil.beanToMap(o, new HashMap(), false, keyEditor)));
        return list;
    }
    protected Page transformPage(Page page, Class<?> asType) {
        page.setRecords(transformList(page.getRecords(), asType));
        return page;
    }
    
    
    public String getDoamin() {
        String s = "https://" + this.request.getServerName();
        return s;
    }
    
}