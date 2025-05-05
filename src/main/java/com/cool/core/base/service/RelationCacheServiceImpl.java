package com.cool.core.base.service;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.EpsField;
import com.cool.core.base.BaseEntity;
import com.cool.core.base.BaseService;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.util.SpringContextUtils;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class RelationCacheServiceImpl<M extends BaseMapper<T>, T extends BaseEntity<T>> extends CacheServiceImpl<M, T> implements BaseService<T> {


    private List<Field> OneToOneField;


    private List<Field> allClassField;

    @Override
    public List<Field> getAllField() {
        if (allClassField == null) {
            this.allClassField = Arrays.asList(this.getAllDeclaredFields(this.currentEntityClass()));
        }
        return allClassField;
    }

    public List<Field> getOneToOneField() {
        if (this.OneToOneField == null) {
            this.OneToOneField = this.getAllField().stream().filter(field -> {
                RelationOneToOne mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationOneToOne.class);
                return mergedAnnotation != null;
            }).toList();
        }

        return this.OneToOneField;
    }

    private List<Field> OneToManyField;

    public List<Field> getOneToManyField() {
        if (this.OneToManyField == null) {
            this.OneToManyField = this.getAllField().stream().filter(field -> {
                RelationOneToMany mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationOneToMany.class);
                return mergedAnnotation != null;
            }).toList();
        }

        return this.OneToManyField;
    }

    private List<Field> ManyToOneField;

    public List<Field> getManyToOneField() {
        if (this.ManyToOneField == null) {
            this.ManyToOneField = this.getAllField().stream().filter(field -> {
                RelationManyToOne mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationManyToOne.class);
                return mergedAnnotation != null;
            }).toList();
        }

        return this.ManyToOneField;
    }

    private List<Field> ManyToManyField;

    public List<Field> getManyToManyField() {
        if (this.ManyToManyField == null) {
            this.ManyToManyField = this.getAllField().stream().filter(field -> {
                RelationManyToMany mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationManyToMany.class);
                return mergedAnnotation != null;
            }).toList();
        }

        return this.ManyToManyField;
    }


    @Data
    @Accessors(chain = true)

    private static class RelationWith {
        String type;  // OneToOne、 OneToMany、 ManyToOne、 ManyToMany
        Field field;
        String name; // 字段名
        String className; // 字段类
        Class<?> classType; // 字段类


        String selfField;
        String targetField;

        BaseService<Class<?>> targetService;
    }

    private Map<String, RelationWith> Relations;

    private void putRelations(String type, List<Field> fields) {
        for (Field field : fields) {
            this.Relations.put(field.getName(),
                    new RelationWith()
                            .setName(field.getName())
                            .setField(field)
                            .setClassName(field.getType().getName())
                            .setClassType(field.getType())
                            .setType(type)
            );
        }
    }

    private void putRelationsOneToMany(String type, List<Field> fields) {

        for (Field field : fields) {

            Class<?> clazz = null;

            Type genericType = field.getGenericType();

            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Type[] actualTypeArguments = pt.getActualTypeArguments();

                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    clazz = (Class<?>) actualTypeArguments[0];
                }
            }

            this.Relations.put(field.getName(),
                    new RelationWith()
                            .setName(field.getName())
                            .setField(field)
                            .setClassName(clazz.getName())
                            .setClassType(clazz)
                            .setType(type)
            );
        }
    }

    private Map<String, RelationWith> getRelations() {
        if (this.Relations == null) {
            this.Relations = new HashMap<>();
            this.putRelations("OneToOne", this.getOneToOneField());
            this.putRelationsOneToMany("OneToMany", this.getOneToManyField());
            this.putRelations("ManyToOne", this.getManyToOneField());
            this.putRelations("ManyToMany", this.getManyToManyField());
        }

        return this.Relations;
    }

    private RelationWith getRelationWith(String key) throws ClassNotFoundException {
        RelationWith relationWith = this.getRelations().get(key);

        if (relationWith == null) {
//            Field field = ReflectionUtils.findField( this.currentEntityClass(), key);


            return null;
        }

        if ("OneToOne".equals(relationWith.getType())) {
            Class<?> clazz = Class.forName(relationWith.getClassName().replace("Entity", "Service").replace("entity", "service")); // 加载类
            BaseService<Class<?>> service = (BaseService<Class<?>>) SpringContextUtils.getBean(clazz);

            relationWith.setTargetService(service);
            Field field = relationWith.getField();
            RelationOneToOne mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationOneToOne.class);
            relationWith.setTargetField(mergedAnnotation.targetField());
            relationWith.setSelfField(mergedAnnotation.selfField());
        } else if ("OneToMany".equals(relationWith.getType())) {
            Class<?> clazz = Class.forName(relationWith.getClassName().replace("Entity", "Service").replace("entity", "service")); // 加载类
            BaseService<Class<?>> service = (BaseService<Class<?>>) SpringContextUtils.getBean(clazz);
            relationWith.setTargetService(service);
            Field field = relationWith.getField();
            RelationOneToMany mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(field, RelationOneToMany.class);
            relationWith.setTargetField(mergedAnnotation.targetField());
            relationWith.setSelfField(mergedAnnotation.selfField());
        }

        return relationWith;
    }

    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return getField(superClass, fieldName);
        }
    }

    /**
     * 根据字段名设置对象字段值
     *
     * @param target    目标对象
     * @param fieldName 字段名
     * @param value     要设置的值
     * @param <T>       对象类型
     */
    public static <T> void setFieldValue(T target, String fieldName, Object value) {
        try {
            Field field = ReflectionUtils.findField(target.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, target, value);
            } else {
                throw new IllegalArgumentException("字段不存在: " + fieldName);
            }
        } catch (Exception e) {
            throw new RuntimeException("设置字段值失败", e);
        }
    }

    public static Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = ReflectionUtils.findField(target.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return ReflectionUtils.getField(field, target);
            }
            throw new IllegalArgumentException("字段不存在: " + fieldName);
        } catch (Exception e) {
            throw new RuntimeException("获取字段值失败" + fieldName, e);
        }
    }


    protected List<T> handleWith(List<T> ts, List<RelationWith> withs) throws Exception {
        for (T t : ts) {

            for (RelationWith relationWith : withs) {

                if (relationWith.getType() == "OneToOne" || relationWith.getType() == "OneToMany") {

                    Object targetT = null;
                    if ("OneToOne".equals(relationWith.getType()) || "OneToMany".equals(relationWith.getType())) {

                        targetT = relationWith.getClassType().getDeclaredConstructor().newInstance();
                    }

                    setFieldValue(targetT, relationWith.getTargetField(),
                            getFieldValue(t, relationWith.getSelfField())
                    );

                    List<Class<?>> classes = relationWith.getTargetService().listWithRelations(null, QueryWrapper.create(targetT), null);

                    if (!classes.isEmpty() && relationWith.getType().equals("OneToOne")) {
                        setFieldValue(t, relationWith.getName(), classes.get(0));
                    }
                    if (Objects.equals(relationWith.getType(), "OneToMany")) {
                        setFieldValue(t, relationWith.getName(), classes);
                    }
                }
            }

        }
        return ts;
    }


    @Override
    public List<T> listWithRelations(JSONObject requestParams, QueryWrapper queryWrapper, List<String> with) {

        try {

            Boolean useCache = true;
            List<RelationWith> withs = new ArrayList<RelationWith>();

            for (String s : with) {
                RelationWith relationWith = this.getRelationWith(s);
                if (relationWith != null) {
                    withs.add(relationWith);
                }
            }


            List<T> ts = super.list(requestParams, queryWrapper);

            handleWith(ts, withs);

            return ts;
        } catch (Exception e) {
            return super.listWithRelations(requestParams, queryWrapper, with);
        }


    }
}
