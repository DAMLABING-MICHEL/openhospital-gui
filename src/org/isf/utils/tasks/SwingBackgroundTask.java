package org.isf.utils.tasks;

public interface SwingBackgroundTask<T, V> {

    T  doInBackground() throws Exception;

    V  getNextResultChunk();

    int getProgress();

}