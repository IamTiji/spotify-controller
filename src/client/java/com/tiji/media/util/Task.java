package com.tiji.media.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Task<T, O>  {
    private final T task;
    private final @Nullable Consumer<O> onComplete;

    public Task(T taskIdentifier, @Nullable Consumer<O> onComplete) {
        task = taskIdentifier;
        this.onComplete = onComplete;
    }
    public T getTask() {
        return task;
    }
    public void run(O result) {
        if(onComplete != null){
            onComplete.accept(result);
        }
    }
}
