package net.shieldcommunity.spigot.ticks.task;

public class TaskPerTick implements Runnable {

    private final int tick;
    private final Runnable task;

    public TaskPerTick(int creationTicks, Runnable task) {
        this.tick = creationTicks;
        this.task = task;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public void run() {
        task.run();
    }
}
