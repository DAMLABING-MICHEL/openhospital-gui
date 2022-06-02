package org.isf.utils.tasks;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SwingBackgroundTaskExecutor {

    private static final SwingBackgroundTaskExecutor instance = new SwingBackgroundTaskExecutor();
    private BackgroundSwingWorker swingWorker;

    private SwingBackgroundTaskExecutor() {}

    public static SwingBackgroundTaskExecutor getInstance() {
        return instance;
    }

    public <V, T> Future<T> execute(SwingBackgroundTask<T, V> backgroundTask, SwingUiRenderer<T, V> renderer) {
        swingWorker = new BackgroundSwingWorker<T, V>(backgroundTask, renderer);
        swingWorker.addPropertyChangeListener(new AccomplishedTaskListener(renderer));
        swingWorker.execute();
        renderer.start();
        return swingWorker.getFuture();
    }

    class AccomplishedTaskListener implements PropertyChangeListener {

        private SwingUiRenderer renderer;
        <T, V> AccomplishedTaskListener(SwingUiRenderer<T, V> renderer) {
            this.renderer = renderer;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("state")) {
                if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    renderer.cancel();
                }
            }
        }
    }

    public void cancel() {
        swingWorker.getFuture().cancel(true);
    }

    static class BackgroundSwingWorker<T, V> extends SwingWorker<T, V> {

        private final SwingBackgroundTask<T, V> backgroundTask;
        private final SwingUiRenderer<T, V> uiRenderer;
        private Future<T>  wrappedFuture;

        public BackgroundSwingWorker(SwingBackgroundTask<T, V> backgroundTask, SwingUiRenderer<T, V> uiRenderer) {
            super();
            this.backgroundTask = backgroundTask;
            this.uiRenderer = uiRenderer;
        }

        @Override
        protected T doInBackground() throws Exception {
            Callable<T> callable = new Callable<T>() {
                public T call() throws Exception {
                    return BackgroundSwingWorker.this.backgroundTask.doInBackground();
                }
            };

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            wrappedFuture = executorService.submit(callable);

            while (!wrappedFuture.isDone() && !wrappedFuture.isCancelled()) {
                //cancel the wrapped future if original task was cancelled
                if (isCancelled())
                {
                    wrappedFuture.cancel(true);
                    return null;
                }
                V result = this.backgroundTask.getNextResultChunk();
                if (result != null) {
                    this.publish(result);
                }
            }
            this.uiRenderer.done(wrappedFuture.get());
            return wrappedFuture.get();
        }

        @Override
        protected void process(List<V> intermediateResults) {}

        protected Future<T> getFuture() {
            return wrappedFuture;
        }
    }
}