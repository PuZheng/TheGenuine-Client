package com.puzheng.the_genuine.store;

import android.util.Pair;

import com.puzheng.the_genuine.data_structure.User;

/**
 * Created by xc on 16-1-14.
 */
public interface Deferrable<DataType, ErrorType> {
    Deferrable<DataType, ErrorType> done(DoneHandler<DataType> doneHandler);

    Deferrable<DataType, ErrorType> fail(FailHandler<ErrorType> failHandler);

    Deferrable<DataType, ErrorType> always(AlwaysHandler alwaysHandler);

    void resolve(DataType body);

    void reject(ErrorType err);
}
