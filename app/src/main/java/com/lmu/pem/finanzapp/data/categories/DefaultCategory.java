package com.lmu.pem.finanzapp.data.categories;

public enum  DefaultCategory implements Category {

    FOOD("Food"), PARTY("Party"), OTHER("Other");

    private String name;

    private DefaultCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
