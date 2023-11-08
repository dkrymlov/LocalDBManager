package com.krymlov.local.database.parts;

public abstract class Column {
    public String name;
    public String type;

    public Column(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean validate(String data);
}
