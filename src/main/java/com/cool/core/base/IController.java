package com.cool.core.base;

public interface IController<S extends BaseService<T>, T extends BaseEntity<T>> {
    public S getService();
}
