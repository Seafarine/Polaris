package es.xism4.software.spigot.ticks;

public abstract class SecondaryTickHandler<R extends Runnable> extends MainTickHandler<R> {

    private int count;

    public SecondaryTickHandler(String name) {
        super(name);
    }

    @Override
    protected boolean executables() {
        return this.runningTask() || super.executables();
    }

    protected boolean runningTask() {
        return this.count != 0;
    }

    @Override
    protected void doRunnable(R task) {
        ++this.count;
        try {
            super.doRunnable(task);
        } finally {
            --this.count;
        }
    }
}
