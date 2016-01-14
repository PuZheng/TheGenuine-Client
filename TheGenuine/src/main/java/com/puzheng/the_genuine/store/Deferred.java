package com.puzheng.the_genuine.store;

/**
 * Created by xc on 16-1-14.
 */
public class Deferred<DataType, ErrorType> implements Deferrable<DataType, ErrorType> {


    private DoneHandler<DataType> doneHandler;
    private FailHandler<ErrorType> failHandler;
    private AlwaysHandler alwaysHandler;

    public Deferrable<DataType, ErrorType> done(DoneHandler<DataType> doneHandler) {
        this.doneHandler = doneHandler;
        return this;
    }

    public Deferrable<DataType, ErrorType> fail(FailHandler<ErrorType> failHandler) {
        this.failHandler = failHandler;
        return this;
    }

    public Deferrable<DataType, ErrorType> always(AlwaysHandler alwaysHandler) {
        this.alwaysHandler = alwaysHandler;
        return this;
    }

    public DoneHandler<DataType> getDoneHandler() {
        return doneHandler;
    }

    public FailHandler<ErrorType> getFailHandler() {
        return failHandler;
    }

    public AlwaysHandler getAlwaysHandler() {
        return alwaysHandler;
    }
}
