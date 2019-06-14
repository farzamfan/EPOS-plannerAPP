package com.example.scheduler.BackgroundTasks;

/**
 * Created by warrens on 23.08.17.
 */

//given a set of states, a wattage, a parent task from which it was called, and which schedule and device it is to work on, create an Energy plan for one possible plan and device.

public class deviceFileFiller implements Runnable {
    public Thread t;
    private String threadName;
    public String[][][] data;
    public String appWattage;
    public createFilesTask caller;
    public int schSiz;
    public int devIndex;
    public String devicePlans = "";
    private StringBuilder builder = new StringBuilder(2880);

    public deviceFileFiller(String name,String[][][] fullData, String wattage, createFilesTask task, int scheduleSize, int deviceIndex){
        this.threadName = name;
        this.data = fullData;
        this.appWattage = wattage;
        this.caller = task;
        this.schSiz = scheduleSize;
        this.devIndex = deviceIndex;
    }

    public void run(){
        for(int c = 0; c<schSiz;c++){
            singlePlan(c);
        }
        devicePlans = builder.toString();
        returnData(devicePlans);
    }

    public void start(){
        if(t==null){
            t = new Thread(this, this.threadName);
        }
        t.start();
    }

    public void returnData(String d){
        caller.returnDeviceFile(devIndex,d);
    }

    public void singlePlan(int c){
        StringBuilder planBuilder = new StringBuilder(2880);
        String dataToReturn = "Plan "+c;
        planBuilder.append(dataToReturn);
        for(int i =0;i<data[c][devIndex].length;i++){
            planBuilder.append(","+data[c][devIndex][i]);
        }
        dataToReturn = planBuilder.toString();
        compilePlans(dataToReturn);
    }

    public void compilePlans(String data){
        builder.append(data+"-");
    }
}