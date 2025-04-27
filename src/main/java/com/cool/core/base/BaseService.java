package com.cool.core.base;

import cn.hutool.json.JSONObject;
import com.cool.core.request.PageParams;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 基础service类
 *
 * @param <T> 实体
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 获取允许在 Page、List 接口中显示的字段
     *
     * @return
     */
    QueryColumn[] getListSelectQueryColumn();

    /**
     * 获取允许在 Page、List 接口中显示的字段
     *
     * @return
     */
    List<Field> getListSelectField();

    /**
     * 获取支持模糊查询的字段
     *
     * @return
     */
    QueryColumn[] getKeyWordQueryColumn();

    /**
     * 获取支持模糊查询的字段
     *
     * @return
     */
    List<Field> getKeyWordField();

    /**
     * 获取所有查询列
     *
     * @return
     */
    QueryColumn[] getAllQueryColumn();

    /**
     * 获取所有数据库字段
     *
     * @return
     */
    List<Field> getAllField();

    /**
     * 获取支持 Eq 查询的列
     *
     * @return
     */
    QueryColumn[] getEqQueryColumn();

    /**
     * 获取支持 Eq 查询的字段
     *
     * @return
     */
    List<Field> getEqField();

    /**
     * 新增
     *
     * @param entity 对应的实体
     */
    Long add(T entity);

    /**
     * 新增
     *
     * @param requestParams 请求参数
     * @param entity        对应的实体
     * @return ID
     */
    Long add(JSONObject requestParams, T entity);

    /**
     * 批量添加
     *
     * @param requestParams 请求参数
     * @param entitys       请求参数
     * @return ID 集合
     */
    List<Long> addBatch(JSONObject requestParams, List<T> entitys);

    /**
     * 删除, 支持单个或者批量删除
     *
     * @param ids ID数组
     */
    boolean delete(Long... ids);

    /**
     * 多个删除，带请求参数
     *
     * @param requestParams 请求参数
     * @param ids           ID数组
     */
    boolean delete(JSONObject requestParams, Long... ids);

    /**
     * 更新
     *
     * @param entity 实体
     */
    boolean update(T entity);

    /**
     * 更新
     *
     * @param requestParams 请求参数
     * @param entity        实体
     */
    boolean update(JSONObject requestParams, T entity);

    /**
     * 查询所有
     *
     * @param requestParams 请求参数
     * @param queryWrapper  查询条件
     * @return 列表信息
     */
    List<T> list(JSONObject requestParams, QueryWrapper queryWrapper);

    /**
     * 查询所有
     *
     * @param requestParams 请求参数
     * @param queryWrapper  查询条件
     * @return 列表信息
     */
    <R> List<R> list(JSONObject requestParams, QueryWrapper queryWrapper, Class<R> asType);

    /**
     * 查询所有
     * 带关联查询
     *
     * @param requestParams 请求参数
     * @param queryWrapper  查询条件
     * @return 列表信息
     */
    List<T> listWithRelations(JSONObject requestParams, QueryWrapper queryWrapper);

    /**
     * 分页查询
     *
     * @param requestParams 请求参数
     * @param page          分页信息
     * @param queryWrapper  查询条件
     * @return 分页信息
     */
    Page<T> page(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper);

    /**
     * 分页查询
     *
     * @param requestParams 请求参数
     * @param page          分页信息
     * @param queryWrapper  查询条件
     * @return 分页信息
     */
    <R> Page<R> page(JSONObject requestParams, Page page, QueryWrapper queryWrapper, Class<R> asType);

    /**
     * 分页查询
     * 带关联查询
     *
     * @param requestParams 请求参数
     * @param page          分页信息
     * @param queryWrapper  查询条件
     * @return 分页信息
     */
    Page<T> pageWithRelations(JSONObject requestParams, Page<T> page, QueryWrapper queryWrapper);

    /**
     * 查询信息
     *
     * @param id ID
     */
    T info(Long id);

    /**
     * 查询信息
     *
     * @param requestParams 请求参数
     * @param id            ID
     */
    T info(JSONObject requestParams, Long id);

    /**
     * 修改之后
     *
     * @param requestParams 请求参数
     * @param t             对应实体
     */
    void modifyAfter(JSONObject requestParams, T t);

    /**
     * 修改之后
     *
     * @param requestParams 请求参数
     * @param t             对应实体
     * @param type          修改类型
     */
    void modifyAfter(JSONObject requestParams, T t, ModifyEnum type);

    /**
     * 修改之前
     *
     * @param requestParams 请求参数
     * @param t             对应实体
     */
    void modifyBefore(JSONObject requestParams, T t);


    /**
     * 管理端 删除之前
     *
     * @param requestParams
     * @param type
     * @param ids
     */
    void deleteBefore(JSONObject requestParams, ModifyEnum type, Long... ids);

    /**
     * 管理端 删除之后
     *
     * @param requestParams
     * @param type
     * @param ids
     */
    void deleteAfter(JSONObject requestParams, ModifyEnum type, Long... ids);

    /**
     * 修改之前
     *
     * @param requestParams 请求参数
     * @param t             对应实体
     * @param type          修改类型
     */
    void modifyBefore(JSONObject requestParams, T t, ModifyEnum type);


    /**
     * App端 新增数据
     *
     * @param entity
     * @return 数据ID
     */
    Long create(T entity);


    /**
     * App端 删除数据
     *
     * @param entity
     * @return
     */
    Boolean delete(T entity);

    /**
     * App端 修改数据
     *
     * @param entity
     * @return
     */
    Boolean modify(T entity);


    /**
     * APP端查询
     */
    Page<T> lists(PageParams<T> pageParams);

    /**
     * APP端查询我的数据
     */
    Page<T> myList(PageParams<T> pageParams);

    /**
     * APP端查询数据详情
     */
    T details(Long id);


    /**
     * APP端查询我的数据详情
     */
    T myDetails(Long id);


}
