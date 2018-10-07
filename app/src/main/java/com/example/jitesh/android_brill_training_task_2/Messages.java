package com.example.jitesh.android_brill_training_task_2;

public class Messages
{
    private String Message,Type,From;
    private long Time;
    private boolean Seen;

    public Messages() {
    }

    public Messages(String message, String type, String from, long time, boolean seen) {
        Message = message;
        Type = type;
        From = from;
        Time = time;
        Seen = seen;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }
}

