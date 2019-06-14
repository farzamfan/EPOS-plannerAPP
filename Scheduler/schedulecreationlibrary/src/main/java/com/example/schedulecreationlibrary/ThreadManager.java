package com.example.schedulecreationlibrary;

/**
 * Created by warrens on 08.08.17.
 */

public class ThreadManager implements Runnable{
    public Thread t;
    private String threadName;
    private Action[] list;
    private Schedule caller;
    private long startingIndex;
    private long endingIndex;

    public ThreadManager(String name, Action[] list, Schedule caller, long startingIndex, long endingIndex){
        this.threadName = name;
        this.list = list;
        this.caller = caller;
        this.startingIndex = startingIndex;
        this.endingIndex = endingIndex;
    }

    /*
     * Initialise the list by creating the first list. This is when all items are in their first acceptable non-conflicting positions.
     * Call change window on the last item, each time it returns a result add that item to the end of the list and call returnActionList to add it to the list of schedules.
     * Do this until that returns null. When it returns null, call changeWindow on the 2nd last item,
     * Then go back to calling changeWindow on the last item. When that returns null again, call changeWindow on the 2nd last item.
     * When calling changeWindow returns null on the 2nd last item, call changeWindow on the 3rd last item.
     * When this process returns you to the first item, get a new starting list.
     */
    public void run(){
        long count = 0;
        long maxCount = 50000L;
        long step = endingIndex/maxCount;
        if(step<1){
            step = 1;
        }
        System.out.print("\n"+"Running "+threadName+"...\n");
        long optimalPoint = 0L;
        long multiplier = 1L;
        for(int i = 0; i< list.length;i++){
            optimalPoint+=list[i].getOptimalIndex()*multiplier;
            multiplier*=list[i].versions.length;
        }
        long[] indeces = new long[list.length];
        if(optimalPoint>startingIndex&&optimalPoint<endingIndex){
            Action[] check = new Action[list.length];
            for(int j = 0; j< list.length; j++){
                check[j] = list[j].getVersion(list[j].getOptimalIndex());
            }
            if(checkList(check)){
                caller.returnActionList(check,optimalPoint);
            }
        }else{
            if(optimalPoint<0){
                if(threadName.equals("Thread 0")){
                    Action[] check = new Action[list.length];
                    for(int j = 0; j< list.length; j++){
                        check[j] = list[j].getVersion(list[j].getOptimalIndex());
                    }
                    if(checkList(check)){
                        caller.returnActionList(check,optimalPoint);
                    }
                }
            }
        }
        for(long i = startingIndex; i<endingIndex&&count<maxCount;i+=step){
            count++;
            if(i!=optimalPoint){
                indeces = getCombination(i+1,list,0,indeces);
                Action[] check = new Action[list.length];
                for(int j = 0; j< list.length; j++){
                    if((int)indeces[j]>0){
                        check[j] = list[j].getVersion((int)indeces[j]);
                    }else{
                        indeces[j]*=-1;
                        check[j] = list[j].getVersion((int)indeces[j]);
                    }

                }
                if(checkList(check)){
                    caller.returnActionList(check,i);
                }
            }
        }
        System.out.println("\n" + this.threadName + " exiting.");
    }
    public void start(){
        System.out.print("\n"+"Starting "+this.threadName+"\n");
        if(t==null){
            t = new Thread(this, this.threadName);
        }
        t.start();
    }

    public boolean checkConflict(Action a, Action b, boolean reverse){
        if(a.isParallel()||b.isParallel()){
            return true;
        }else{
            if(a.windowStart<=b.windowStart&&a.windowEnd<=b.windowEnd&&a.windowEnd>=b.windowStart){
                //System.out.print("Conflict between "+a.name+" and "+b.name+"\n");
                return false;
            }
            if(a.windowStart>=b.windowStart&&a.windowEnd<=b.windowEnd){
                //System.out.print("Conflict between "+a.name+" and "+b.name+"\n");
                return false;
            }
            if(!reverse){
                if(a.windowStart == b.windowStart){
                    return false;
                }
                if(a.windowEnd == b.windowEnd){
                    return false;
                }
                return checkConflict(b,a, true);
            }
            else{
                return true;
            }
        }
    }

    public boolean checkPosition(int indexOfItem, Action a, Action[] currentSchedule){
        boolean noConflict = true;
        if(a.isParallel()){
            return true;
        }else{
            for(int i = 0; i<currentSchedule.length&&noConflict; i++){
                if(currentSchedule[i]!=null&&i!=indexOfItem){
                    noConflict = checkConflict(a,currentSchedule[i], false);
                }
            }
        }
        return noConflict;
    }

    public boolean checkList(Action[] list){
        boolean check = true;
        for(int i = 0; i<list.length&&check;i++){
            check = checkPosition(i, list[i], list);
        }
        return check;
    }

    public static long[] getCombination(long i, Action[] lists, int index, long[] indeces){
        indeces[index] = (i-1)%lists[index].versions.length;
        long passOn = (((i-1)-indeces[index])/lists[index].versions.length)+1;
        if(index+1<lists.length){
            indeces = getCombination(passOn, lists, index+1, indeces);
        }
        return indeces;
    }
}
