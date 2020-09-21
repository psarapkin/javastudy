package com.realApp.App;

import com.realApp.Dataobjects.MyTable1;

public class realApp {

    public static void main(String[] args) {
        TableCreator tc = new TableCreator();

        String sqlCommand = tc.getSqlCommand(MyTable1.class);

        System.out.println(sqlCommand);
    }
}
