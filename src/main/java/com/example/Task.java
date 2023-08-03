package com.example;

public class Task {
    private long user_qq;
    private TaskType taskType;
    private int time;

    private long target_qq;

    public Task(long user_qq, TaskType taskType, int time, long target_qq) {
        this.user_qq = user_qq;
        this.taskType = taskType;
        this.time = time;
        this.target_qq = target_qq;
    }

    public long getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(long user_qq) {
        this.user_qq = user_qq;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getTarget_qq() {
        return target_qq;
    }

    public void setTarget_qq(long target_qq) {
        this.target_qq = target_qq;
    }
}

