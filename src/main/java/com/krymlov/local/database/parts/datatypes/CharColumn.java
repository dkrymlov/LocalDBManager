package com.krymlov.local.database.parts.datatypes;

import com.krymlov.local.database.parts.Column;

public class CharColumn extends Column {

    public CharColumn(String name){
        super(name);
        this.type = ColumnType.CHAR.name();
    }

    @Override
    public boolean validate(String data){
        return data.length() == 1 || data.length() == 0;
    }
}
