package com.cool.core.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.QuickQueryField;
import com.cool.core.annotation.ListSelectField;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.PageParams;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 基础service实现类
 *
 * @param <M> Mapper 类
 * @param <T> 实体
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity<T>> extends
    ServiceImpl<M, T>
    implements BaseService<T> {


    protected Class<T> entityClass;

    protected QueryColumn[] selectField;
    
    protected QueryColumn[] keyWordField;

    protected QueryColumn[] allField;
    
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


    protected Field[] getAllDeclaredFields(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        // 遍历当前类及所有父类
        for (Class<?> currentClass = clazz; currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
        }
        return fields.toArray(new Field[0]);
    }


    @Override
    public QueryColumn[] getAllField() {
            if (allField != null) {
            return allField;
        }

        List<QueryColumn> allFieldList = new ArrayList<QueryColumn>();

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(this.currentEntityClass());

        Arrays.stream(this.getAllDeclaredFields(entityClass))
                .filter(field -> {
                    ColumnDefine fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, ColumnDefine.class);
                    return fieldInfo!=null;
                })
                .forEach(field -> {
                    String name = field.getName();
                    allFieldList.add(tableInfo.getQueryColumnByProperty(name));
                });
        this.allField = allFieldList.toArray(new QueryColumn[0]);

        System.out.println( allFieldList );
        System.out.println( allField );
        
        return allField;
    }

    @Override
    public QueryColumn[] getListSelectField() {
        if (selectField != null) {
            return selectField;
        }

        List<QueryColumn> selectFieldList = new ArrayList<QueryColumn>();

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(this.currentEntityClass());

        Arrays.stream(this.getAllDeclaredFields(entityClass))
                .filter(field -> {
                    ListSelectField fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, ListSelectField.class);
                    if ( fieldInfo == null ) {
                        return true;
                    }
                    return !fieldInfo.hidden();
                })
                .forEach(field -> {
                    String name = field.getName();
                    selectFieldList.add(tableInfo.getQueryColumnByProperty(name));
                });
        this.selectField = selectFieldList.toArray(new QueryColumn[0]);
        return selectField;
    }
    
    /**
     * 获取支持模糊查询的字段
     * @return
     */
    @Override
    public QueryColumn[] getKeyWordField(){
        if (keyWordField != null) {
            return keyWordField;
        }
        List<QueryColumn> keyWordFieldList = new ArrayList<QueryColumn>();

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(this.currentEntityClass());

        Arrays.stream(this.getAllDeclaredFields(entityClass))
                .filter(field -> {
                    QuickQueryField fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, QuickQueryField.class);
                    return fieldInfo != null;
                })
                .forEach(field -> {
                    String name = field.getName();
                    keyWordFieldList.add(tableInfo.getQueryColumnByProperty(name));
                });
        this.keyWordField = keyWordFieldList.toArray(new QueryColumn[0]);
        return keyWordField;
    }


    @Override
    public Long add(T entity) {
        mapper.insertSelective(entity);
        return entity.getId();
    }

    @Override
    public Object add(JSONObject requestParams, T entity) {
        this.modifyBefore(requestParams, entity, ModifyEnum.ADD);
        this.add(entity);
        this.modifyAfter(requestParams, entity, ModifyEnum.ADD);
        return entity.getId();
    }

    @Override
    public Object addBatch(JSONObject requestParams, List<T> entitys) {
        this.modifyBefore(requestParams, null, ModifyEnum.ADD);
        List<Long> ids = new ArrayList<>();
        entitys.forEach(e -> ids.add(this.add(e)));
        requestParams.set("ids", ids);
        this.modifyAfter(requestParams, null, ModifyEnum.ADD);
        return ids;
    }

    @Override
    public boolean delete(Long... ids) {
        return mapper.deleteBatchByIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public boolean delete(JSONObject requestParams, Long... ids) {
        this.modifyBefore(requestParams, null, ModifyEnum.DELETE);
        this.deleteBefore(requestParams, ModifyEnum.DELETE, ids );
        
        boolean flag = this.delete(ids);
        if (flag) {
            this.modifyAfter(requestParams, null, ModifyEnum.DELETE);
            this.deleteAfter(requestParams, ModifyEnum.DELETE , ids );
        }
        return flag;
    }

    @Override
    public boolean update(T entity) {
        return mapper.update(entity) > 0;
    }

    @Override
    public boolean update(JSONObject requestParams, T entity) {
        this.modifyBefore(requestParams, entity, ModifyEnum.UPDATE);
        boolean flag = this.update(entity);
        if (flag) {
            this.modifyAfter(requestParams, entity, ModifyEnum.UPDATE);
        }
        return flag;
    }

    @Override
    public Object list(JSONObject requestParams, QueryWrapper queryWrapper) {
        return this.list(queryWrapper);
    }

    @Override
    public <R> List<R> list(JSONObject requestParams, QueryWrapper queryWrapper, Class<R> asType) {
        return mapper.selectListByQueryAs(queryWrapper, asType);
    }

    @Override
    public Object listWithRelations(JSONObject requestParams, QueryWrapper queryWrapper) {
        return mapper.selectListWithRelationsByQuery(queryWrapper);
    }

    @Override
    public Object page(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper) {
        return this.page(page, queryWrapper);
    }

    @Override
    public <R> Page<R> page(JSONObject requestParams, Page page, QueryWrapper queryWrapper,
        Class<R> asType) {
        return mapper.paginateAs(page, queryWrapper, asType);
    }

    @Override
    public Object pageWithRelations(JSONObject requestParams, Page<T> page,
        QueryWrapper queryWrapper) {
        return mapper.paginateWithRelations(page, queryWrapper);
    }

    @Override
    public Object info(JSONObject requestParams, Long id) {
        return info(id);
    }

    @Override
    public Object info(Long id) {
        return mapper.selectOneById(id);
    }

    @Override
    public void modifyAfter(JSONObject requestParams, T t) {

    }

    @Override
    public void modifyAfter(JSONObject requestParams, T t, ModifyEnum type) {
        modifyAfter(requestParams, t);
    }

    @Override
    public void deleteAfter(JSONObject requestParams, ModifyEnum type, Long... ids) {
        
    }

    @Override
    public void deleteBefore(JSONObject requestParams, ModifyEnum type, Long... ids) {
        
    }

    @Override
    public void modifyBefore(JSONObject requestParams, T t) {

    }

    @Override
    public void modifyBefore(JSONObject requestParams, T t, ModifyEnum type) {

    }

    @Override
    public Long create(T entity) {
        this.modifyBefore( null , entity, ModifyEnum.ADD);
        Long add = this.add(entity);
        this.modifyAfter( null , entity, ModifyEnum.ADD);
        return add;
    }

    @Override
    public Boolean delete(T entity) {
        this.modifyBefore( null , entity, ModifyEnum.DELETE );
        
        if ( entity.getId() == null || entity.getId() <= 0 ) {
            CoolPreconditions.alwaysThrow("仅支持通过ID删除");
        }
        boolean delete = this.delete( entity.getId());
        
        this.modifyAfter( null , entity, ModifyEnum.DELETE );
        return delete;
    }

    @Override
    public Boolean modify(T entity) {
        this.modifyBefore( null , entity, ModifyEnum.UPDATE);
        boolean update = this.update(entity);
        this.modifyAfter( null , entity, ModifyEnum.UPDATE);
        return update;
    }

    public QueryWrapper listsBefore(PageParams<T> pageParams) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .orderBy(pageParams.getOrder(), pageParams.getSort().toUpperCase(Locale.ENGLISH).equals("ASC"))
                .select(this.getListSelectField());

        if (!StrUtil.isBlankIfStr(pageParams.getKeyWord())) {
            String keyWord = pageParams.getKeyWord().trim();
            queryWrapper.and(queryWrapper1 -> {
                for (QueryColumn field : this.getKeyWordField()) {
                    queryWrapper1.or(field.like(keyWord));
                }
            });
        }
        
        try {
            T params = pageParams.getParams();
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(params.getClass());

            for (Field field : params.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(params);

                if (value != null) {
                    QueryColumn queryColumnByProperty = tableInfo.getQueryColumnByProperty(field.getName());

                    queryWrapper.and(queryColumnByProperty.eq(value));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add conditions", e);
        }

        return queryWrapper;
    }
    
    public void listsAfter(PageParams<T> pageParams ,  Page<T> page ){}
    
    @Override
    public Page<T> lists(PageParams<T> pageParams) {
        Page<T> tPage = this.mapper.paginateWithRelations(pageParams.toPage(), listsBefore(pageParams) );
        this.listsAfter(pageParams, tPage);
        return tPage;
    }

    @Override
    public Page<T> myList(PageParams<T> pageParams) {
        Page<T> tPage = this.mapper.paginateWithRelations(pageParams.toPage(), listsBefore(pageParams) );
        this.listsAfter(pageParams, tPage);
        return tPage;
    }
    
    public void detailsBefore( Long id){}
    public void detailsyAfter( T t){}
    
    @Override
    public T details(Long id) {
        this.detailsBefore(id);
        T byId = this.mapper.selectOneWithRelationsById(id);
        this.detailsyAfter( byId );
        return byId;
    }

    @Override
    public T myDetails(Long id) {
        this.detailsBefore(id);
        T byId = this.details(id);
        
        if (byId instanceof BelongingUserEntity appEntity){
            
            CoolPreconditions.check( !CoolSecurityUtil.getCurrentUserId().equals( appEntity.getUserId() ),
                    "不是你的数据");

        }
        
        this.detailsyAfter( byId );
        return byId;
    }
}
