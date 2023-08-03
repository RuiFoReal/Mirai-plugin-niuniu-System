package com.example;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class QQrobot extends JavaPlugin {
    public static final QQrobot INSTANCE = new QQrobot();
    public static List<Task> tasks = new Vector<>();
    //QQ - cold
    public static ConcurrentHashMap<Long, Integer> tietie_colddown_and_user = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Integer> bihua_colddown_and_user = new ConcurrentHashMap<>();

    private QQrobot() {
        super(new JvmPluginDescriptionBuilder("com.example.demo1", "0.1.0")
                .name("QQrobot")
                .author("R")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        GlobalEventChannel.INSTANCE.registerListenerHost(new MessageListener());
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            if (tasks.size() == 0) {
                return;
            }
            Iterator<Task> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                int index = tasks.indexOf(task);
                task.setTime(task.getTime() - 1);
                tasks.set(index, task);
                if (task.getTime() == 0) {
                    iterator.remove();
                }
            }
        };

        Runnable runnable1 = () -> {
            if (tietie_colddown_and_user.size() == 0) {
                return;
            }
            Iterator<Map.Entry<Long, Integer>> iterator = tietie_colddown_and_user.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Integer> next = iterator.next();
                next.setValue(next.getValue() - 1);
                if (next.getValue() == 0) {
                    iterator.remove();
                }
            }
        };
        Runnable runnable2 = () -> {
            if (bihua_colddown_and_user.size() == 0) {
                return;
            }
            Iterator<Map.Entry<Long, Integer>> iterator = bihua_colddown_and_user.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Integer> next = iterator.next();
                next.setValue(next.getValue() - 1);
                if (next.getValue() == 0) {
                    iterator.remove();
                }
            }
        };

        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        service.scheduleAtFixedRate(runnable1, 0, 1, TimeUnit.HOURS);
        service.scheduleAtFixedRate(runnable2, 0, 1, TimeUnit.HOURS);
    }
}