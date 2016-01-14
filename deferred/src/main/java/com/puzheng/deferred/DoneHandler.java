package com.puzheng.deferred;

/**
 * Created by xc on 16-1-14.
 */
public interface DoneHandler<T> {
    void done(T t);
}
