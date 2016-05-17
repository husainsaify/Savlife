package com.hackerkernel.blooddonar.pojo;

/**
 * Pojo class to hold simple response
 */
public class SimplePojo {
    private String message;
    private boolean returned;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
