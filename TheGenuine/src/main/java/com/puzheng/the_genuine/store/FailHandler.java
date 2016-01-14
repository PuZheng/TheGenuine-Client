package com.puzheng.the_genuine.store;

/**
 * Created by xc on 16-1-14.
 */
public interface FailHandler<T> {
    void fail(T t);
}
