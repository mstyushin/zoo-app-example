package com.mstyushin.example.protocol;

import java.io.Serializable;

public class SimpleMessage implements Serializable {

    private static final long serialVersionUID = 5193392663743561680L;

    private String text;

    public String getText() {
        return text;
    }

    public SimpleMessage(String text) {
        this.text = text;
    }
}
