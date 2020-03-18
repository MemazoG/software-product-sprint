package com.google.sps;
import java.util.Comparator;

public class SortByStartTime implements Comparator<TimeRange> {
    @Override
    public int compare(TimeRange a, TimeRange b){
        //Compara by starting times, and if they are the same, by the shortest
        if(a.start() > b.start()){
            return 1;
        } else if(a.start() == b.start()){
            if(a.duration() > b.duration()){
                return 1;
            }
        }
        return 0;
    }
}