package com.krymlov.local.database.parts.datatypes;

import com.krymlov.local.database.parts.Column;

public class StringColumn extends Column {

    public StringColumn(String name){
        super(name);
        this.type = ColumnType.STRING.name();
    }
    @Override
    public boolean validate(String data) {
        return true;
    }
}
