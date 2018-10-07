package com.example.jitesh.android_brill_training_task_2;

public class Conv
{
    public boolean Seen;
    public long TimeStamp;

    public Conv(){

    }

    public Conv(boolean seen, long timeStamp) {
        Seen = seen;
        TimeStamp = timeStamp;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}

