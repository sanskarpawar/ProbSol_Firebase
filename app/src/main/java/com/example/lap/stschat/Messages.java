package com.example.lap.stschat;

public class Messages {
    public String from, message,type,count;
    public Messages()
    {

    }
    public Messages(String from, String message, String type,String count) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.count = count;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

