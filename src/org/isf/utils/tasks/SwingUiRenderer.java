package org.isf.utils.tasks;

import java.util.List;

public interface SwingUiRenderer<T, V> {

    void start();

    void  processIntermediateResults(List<V> intermediateResults, int progress);

    void  done(T result);

    void cancel();
}
