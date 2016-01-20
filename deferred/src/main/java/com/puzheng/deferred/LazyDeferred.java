package com.puzheng.deferred;

public abstract class LazyDeferred<DataType, ErrorType> extends Deferred<DataType, ErrorType> {

    private boolean started;

    public abstract void onStart();


    @Override
    public Deferrable<DataType, ErrorType> done(DoneHandler<DataType> doneHandler) {
        Deferrable<DataType, ErrorType> ret = super.done(doneHandler);
        if (!started) {
            started = true;
            onStart();
        }
        return ret;
    }

    @Override
    public Deferrable<DataType, ErrorType> fail(FailHandler<ErrorType> failHandler) {
        Deferrable<DataType, ErrorType> ret = super.fail(failHandler);
        if (!started) {
            started = true;
            onStart();
        }
        return ret;
    }

    @Override
    public Deferrable<DataType, ErrorType> always(AlwaysHandler alwaysHandler) {
        Deferrable<DataType, ErrorType> ret = super.always(alwaysHandler);
        if (!started) {
            started = true;
            onStart();
        }
        return ret;
    }
}
