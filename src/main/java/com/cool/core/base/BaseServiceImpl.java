package com.cool.core.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.EpsField;
import com.cool.core.annotation.QuickQueryField;
import com.cool.core.annotation.ListSelectField;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.PageParams;
import com.cool.core.util.CoolSecurityUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;

import com.mybatisflex.core.relation.RelationManager;
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
    
    
    private TableInfo tableInfo = null;
    public TableInfo getTableInfo(){
        if (tableInfo != null) {
            return tableInfo;
        }
        return tableInfo = TableInfoFactory.ofEntityClass(this.currentEntityClass());
    }


    protected Field[] getAllDeclaredFields(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        // 遍历当前类及所有父类
        for (Class<?> currentClass = clazz; currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
        }
        return fields.toArray(new Field[0]);
    }


    protected List<Field> allField;

    @Override
    public List<Field> getAllField() {

        if (allField != null) {
            return allField;
        }

        return allField = Arrays.stream(this.getAllDeclaredFields(entityClass))
                .filter(field -> {
                    ColumnDefine fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, ColumnDefine.class);
                    return fieldInfo != null;
                }).toList();

    }


    protected QueryColumn[] allQueryColumn;

    @Override
    public QueryColumn[] getAllQueryColumn() {
        if (allQueryColumn != null) {
            return allQueryColumn;
        }

        List<QueryColumn> allQueryColumnList = new ArrayList<QueryColumn>();

        List<Field> allField1 = getAllField();
        TableInfo tableInfo = getTableInfo();

        allField1.forEach(field -> {
            String name = field.getName();
            allQueryColumnList.add(tableInfo.getQueryColumnByProperty(name));
        });
        return this.allQueryColumn = allQueryColumnList.toArray(new QueryColumn[0]);

    }


    private List<Field> eqField = null;

    @Override
    public List<Field> getEqField() {
        if (eqField != null) {
            return eqField;
        }

        List<Field> allField1 = getAllField();

        return eqField = allField1.stream().filter(field -> {
            EpsField epsFieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
            if (epsFieldInfo != null) {
//                作为 like 条件时，不再作为 eq 条件
                return !( epsFieldInfo.like() || epsFieldInfo.excludeEq() );
            }
            return true;
        }).toList();
    }

    protected QueryColumn[] eqQueryColumn = null;

    @Override
    public QueryColumn[] getEqQueryColumn() {

        if (eqQueryColumn != null) {
            return eqQueryColumn;
        }

        List<QueryColumn> eqQueryColumnList = new ArrayList<QueryColumn>();

        List<Field> allField1 = getEqField();
        TableInfo tableInfo = getTableInfo();

        allField1.forEach(field -> {
            String name = field.getName();
            eqQueryColumnList.add(tableInfo.getQueryColumnByProperty(name));
        });
        return this.eqQueryColumn = eqQueryColumnList.toArray(new QueryColumn[0]);
    }

    protected List<Field> selectField;

    @Override
    public List<Field> getListSelectField() {
        if (selectField != null) {
            return selectField;
        }

        return selectField = this.getAllField().stream()
                .filter(field -> {
                    ListSelectField fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, ListSelectField.class);
                    if (fieldInfo != null) {
                        return !fieldInfo.hidden();
                    }
                    EpsField epsFieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
                    if (epsFieldInfo != null) {
                        return !epsFieldInfo.excludeListSelect();
                    }
                    return true;
                }).toList();
    }


    protected QueryColumn[] selectQueryColumn;

    @Override
    public QueryColumn[] getListSelectQueryColumn() {

        if (selectQueryColumn != null) {
            return selectQueryColumn;
        }

        List<QueryColumn> selectFieldList = new ArrayList<QueryColumn>();

        TableInfo tableInfo = getTableInfo();

        this.getListSelectField().forEach(field -> {
            String name = field.getName();
            selectFieldList.add(tableInfo.getQueryColumnByProperty(name));
        });

        return selectQueryColumn = selectFieldList.toArray(new QueryColumn[0]) ;

    }


    protected List<Field> keyWordField;


    /**
     * 获取支持模糊查询的字段
     * @return
     */
    @Override
    public List<Field> getKeyWordField() {
        if (keyWordField != null) {
            return keyWordField;
        }


        return keyWordField = getAllField().stream()
                .filter(field -> {
                    QuickQueryField fieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, QuickQueryField.class);
                    if (fieldInfo != null) {
                        return true;
                    }
                    EpsField epsFieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
                    if (epsFieldInfo != null) {
                        return epsFieldInfo.quickQuery();
                    }
                    return false;
                }).toList();
    }


    protected QueryColumn[] keyWordQueryColumn;

    @Override
    public QueryColumn[] getKeyWordQueryColumn() {
        if (keyWordQueryColumn != null) {
            return keyWordQueryColumn;
        }

        List<QueryColumn> keyWordFieldList = new ArrayList<QueryColumn>();

        TableInfo tableInfo = getTableInfo();

        List<Field> keyWordField1 = getKeyWordField();
        keyWordField1.forEach(field -> {
            String name = field.getName();
            keyWordFieldList.add(tableInfo.getQueryColumnByProperty(name));
        });

        return this.keyWordQueryColumn = keyWordFieldList.toArray(new QueryColumn[0]);
    }

    protected List<Field> immutableField = null;
    
    /**
     * 获取不可变字段
     * @return
     */
    protected List<Field> getImmutableField(){
        if (immutableField != null) {
            return immutableField;
        }
        return keyWordField = getAllField().stream()
            .filter(field -> {

                EpsField epsFieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
                if (epsFieldInfo != null) {
                    return epsFieldInfo.immutable();
                }
                return false;
            }).toList();
    }
    
    
    
    protected List<Field> likeField = null;
    
    protected List<Field> getLikeField() {
        if (likeField != null) {
            return likeField;
        }

        return likeField = getAllField().stream().filter(field -> {
            EpsField epsFieldInfo = AnnotatedElementUtils.findMergedAnnotation(field, EpsField.class);
            if (epsFieldInfo != null) {
                return epsFieldInfo.like();
            }
            return false;
        }).toList();
    }
    

    @Override
    public Long add(T entity) {
        mapper.insertSelective(entity);
        return entity.getId();
    }

    @Override
    public Long add(JSONObject requestParams, T entity) {
        this.modifyBefore(requestParams, entity, ModifyEnum.ADD);
        this.add(entity);
        this.modifyAfter(requestParams, entity, ModifyEnum.ADD);
        return entity.getId();
    }

    @Override
    public List<Long> addBatch(JSONObject requestParams, List<T> entitys) {
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
    public Page<T> page(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper) {
        return this.page(page, queryWrapper);
    }

    @Override
    public <R> Page<R> page(JSONObject requestParams, Page page, QueryWrapper queryWrapper,
        Class<R> asType) {
        return mapper.paginateAs(page, queryWrapper, asType);
    }

    @Override
    public Page<T> pageWithRelations(JSONObject requestParams, Page<T> page,
        QueryWrapper queryWrapper) {
        return mapper.paginateWithRelations(page, queryWrapper);
    }

    @Override
    public T info(JSONObject requestParams, Long id) {
        return info(id);
    }

    @Override
    public T info(Long id) {
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
    
    
    
    protected QueryWrapper buildModifyCondition( T params , ModifyEnum me ){

        QueryWrapper qw = QueryWrapper.create();
        try {

            TableInfo tableInfo = getTableInfo();

            for (Field field : this.getImmutableField()) {
                field.setAccessible(true);
                Object value = field.get(params);

                if (value != null) {
                    QueryColumn queryColumnByProperty = tableInfo.getQueryColumnByProperty(field.getName());

                    qw.and(queryColumnByProperty.eq(value));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add conditions", e);
        }
        return qw;
    }
    

    @Override
    public Boolean delete(T entity) {
        this.modifyBefore( null , entity, ModifyEnum.DELETE );
        
        if ( entity.getId() == null || entity.getId() <= 0 ) {
            CoolPreconditions.alwaysThrow("仅支持通过ID删除");
        }
        
        QueryWrapper qw = buildModifyCondition(entity, ModifyEnum.UPDATE);
        
        boolean delete = this.remove( qw );
        
        this.modifyAfter( null , entity, ModifyEnum.DELETE );
        return delete;
    }

    @Override
    public Boolean modify(T entity) {
        
        if ( entity.getId() == null || entity.getId() <= 0 ) {
             CoolPreconditions.alwaysThrow("ID不能为空");
        }
        
        this.modifyBefore( null , entity, ModifyEnum.UPDATE);

        QueryWrapper qw = buildModifyCondition(entity, ModifyEnum.UPDATE);
        System.out.println( qw.toSQL() );
        boolean update = this.mapper.updateByQuery( entity , qw ) > 0;
        this.modifyAfter( null , entity, ModifyEnum.UPDATE);
        return update;
    }
    
    protected BaseServiceImpl<M, T> buildOrder( PageParams<T> pageParams, QueryWrapper qw ){
        qw.orderBy(pageParams.getOrder(), pageParams.getSort().toUpperCase(Locale.ENGLISH).equals("ASC"));
        return this;
    }
    
    
    protected BaseServiceImpl<M, T> buildKeyWord( PageParams<T> pageParams, QueryWrapper qw ){
        if (!StrUtil.isBlankIfStr(pageParams.getKeyWord())) {
            String keyWord = pageParams.getKeyWord().trim();
            qw.and(queryWrapper1 -> {
                for (QueryColumn field : this.getKeyWordQueryColumn()) {
                    queryWrapper1.or(field.like(keyWord));
                }
            });
        }
        return this;
    }
    
    protected BaseServiceImpl<M, T> buildEqCondition( PageParams<T> pageParams, QueryWrapper qw ){
        try {
            T params = pageParams.getParams();
            
            TableInfo tableInfo = getTableInfo();

            for (Field field : this.getEqField()) {
                field.setAccessible(true);
                Object value = field.get(params);

                if (value != null) {
                    QueryColumn queryColumnByProperty = tableInfo.getQueryColumnByProperty(field.getName());

                    qw.and(queryColumnByProperty.eq(value));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add conditions", e);
        }
        return this;
    }
    
    protected BaseServiceImpl<M, T> buildLikeCondition( PageParams<T> pageParams, QueryWrapper qw ){
        try {
            T params = pageParams.getParams();
            
            TableInfo tableInfo = getTableInfo();

            for (Field field : this.getEqField()) {
                field.setAccessible(true);
                Object value = field.get(params);

                if (value != null) {
                    QueryColumn queryColumnByProperty = tableInfo.getQueryColumnByProperty(field.getName());

                    qw.and(queryColumnByProperty.like( value ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add conditions", e);
        }
        
        return this;
    }
    

    public QueryWrapper listsBefore(PageParams<T> pageParams) {

        RelationManager.addQueryRelations(pageParams.getWith().toArray(String[]::new));
        
        QueryWrapper queryWrapper = QueryWrapper.create().select(this.getListSelectQueryColumn());
        this.buildOrder( pageParams,  queryWrapper )
                .buildKeyWord( pageParams,  queryWrapper  )
                .buildEqCondition( pageParams,  queryWrapper )
                .buildLikeCondition( pageParams,  queryWrapper );

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
