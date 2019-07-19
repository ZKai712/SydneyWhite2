package com.dyxy.zkai.sydneywhite.entity;


public class TaskForm {
    private String taskName;
    private int flag;
    private String details;

    public TaskForm() {
    }

    public TaskForm(String taskName,String details) {
        this.taskName = taskName;
        this.details = details;
    }

    public TaskForm(String taskName, int flag, String details) {
        this.taskName = taskName;
        this.flag = flag;
        this.details = details;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
