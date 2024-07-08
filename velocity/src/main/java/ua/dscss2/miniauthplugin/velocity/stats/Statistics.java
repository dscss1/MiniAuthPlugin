package ua.dscss2.miniauthplugin.velocity.stats;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import ua.dscss2.miniauthplugin.velocity.MiniAuthPlugin;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class Statistics {

    private final LongAdder blockedConnections = new LongAdder();
    private final LongAdder connections = new LongAdder();
    private final LongAdder pings = new LongAdder();
    private final AtomicLong interpolatedCpsBefore = new AtomicLong();
    private final AtomicLong interpolatedPpsBefore = new AtomicLong();
    private final List<ScheduledTask> scheduledTaskList = new LinkedList<>();
    private final Map<InetAddress, Integer> pingMap = new HashMap<>();

    public void addBlockedConnection() {
        this.blockedConnections.increment();
    }

    public void addConnection() {
        this.connections.add(300 * 2L);
    }

    public void addPing() {
        this.pings.add(300 * 2L);
    }

    public long getBlockedConnections() {
        return this.blockedConnections.longValue();
    }

    public long getConnections() {
        return this.connections.longValue() / 300 / 2L;
    }

    public long getPings() {
        return this.pings.longValue() / 300 / 2L;
    }

    public long getTotalConnection() {
        return this.getPings() + this.getConnections();
    }

    public void restartUpdateTasks(MiniAuthPlugin plugin, Scheduler scheduler) {
        synchronized (this.scheduledTaskList) {
            this.scheduledTaskList.forEach(ScheduledTask::cancel);
            this.scheduledTaskList.clear();

            this.startUpdatingCps(plugin, scheduler);
            this.startUpdatingPps(plugin, scheduler);
        }
    }

    private void startUpdatingCps(MiniAuthPlugin plugin, Scheduler scheduler) {
        long delayInterpolate = 300 * 1000L;

        this.scheduledTaskList.add(scheduler
                .buildTask(plugin, () -> this.interpolatedCpsBefore.set(Statistics.this.connections.longValue() / 300 / 2L))
                .delay(delayInterpolate, TimeUnit.MILLISECONDS)
                .repeat(delayInterpolate, TimeUnit.MILLISECONDS)
                .schedule());

        long delay = delayInterpolate / 300 / 2L;

        this.scheduledTaskList.add(scheduler
                .buildTask(plugin, () -> {
                    long current = Statistics.this.connections.longValue();
                    long before = Statistics.this.interpolatedCpsBefore.get();

                    if (current >= before) {
                        Statistics.this.connections.add(-before);
                    }
                })
                .delay(delay, TimeUnit.MILLISECONDS)
                .repeat(delay, TimeUnit.MILLISECONDS)
                .schedule());
    }

    private void startUpdatingPps(MiniAuthPlugin plugin, Scheduler scheduler) {
        long delayInterpolate = 5 * 1000L;

        this.scheduledTaskList.add(scheduler
                .buildTask(plugin, () -> this.interpolatedPpsBefore.set(Statistics.this.pings.longValue() / 5 / 2L))
                .delay(delayInterpolate, TimeUnit.MILLISECONDS)
                .repeat(delayInterpolate, TimeUnit.MILLISECONDS)
                .schedule());

        long delay = delayInterpolate / 5 / 2L;

        this.scheduledTaskList.add(scheduler
                .buildTask(plugin, () -> {
                    long current = Statistics.this.pings.longValue();
                    long before = Statistics.this.interpolatedPpsBefore.get();

                    if (current >= before) {
                        Statistics.this.pings.add(-before);
                    }
                })
                .delay(delay, TimeUnit.MILLISECONDS)
                .repeat(delay, TimeUnit.MILLISECONDS)
                .schedule());
    }

    public void updatePing(InetAddress address, int currentPing) {
        this.pingMap.merge(address, currentPing, (previousPing, newPing) -> (previousPing * 3 + newPing) / 4);
    }

    public int getPing(InetAddress address) {
        Integer ping = this.pingMap.get(address);

        if (ping == null) {
            return -1;
        } else {
            return ping;
        }
    }

    public void removeAddress(InetAddress address) {
        this.pingMap.remove(address);
    }
}