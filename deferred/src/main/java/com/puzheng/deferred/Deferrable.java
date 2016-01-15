package com.puzheng.deferred;

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
