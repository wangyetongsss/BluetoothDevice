package com.example.administrator.bluetoothdevice.eventbus;

/**
 * Created by 2018/4/12 11:17
 * 创建：Administrator on
 * 描述:Eventbus传值对象
 * int string
 */

public class ViewEvent {
    private final int Event;
    private String Message;
    private int What;

    public ViewEvent(int event) {
        Event = event;
    }

    public int getEvent() {
        return Event;
    }

    public String getMessage() {
        return Message;
    }

    public ViewEvent setMessage(String message) {
        Message = message;
        return this;
    }

    public int getWhat() {
        return What;
    }

    public ViewEvent setWhat(int what) {
        What = what;
        return this;
    }
}
