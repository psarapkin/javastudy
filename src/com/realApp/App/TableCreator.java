package com.realApp.App;

import com.examples.annotations.database.Constraints;
import com.examples.annotations.database.DBTable;
import com.examples.annotations.database.SQLInteger;
import com.examples.annotations.database.SQLString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableCreator {

    public static String getSqlCommand(Class<?> cl) {
        DBTable dbTable = cl.getAnnotation(DBTable.class);

        if (dbTable == null) {
            System.out.println("No DBTable annotations in class");
            throw new RuntimeException("Not found DBTable annotation");
        }

        StringBuilder resultSql = new StringBuilder();

        String tableName = dbTable.name();

        if (tableName.length() < 1)
            tableName = cl.getSimpleName().toUpperCase();

        List<String> columnDefs = new ArrayList<String>();

        for (Field field : cl.getDeclaredFields()) {
            String columnName = null;

            Annotation[] anns = field.getDeclaredAnnotations();

            if (anns.length < 1)
                continue;

            if (anns[0] instanceof SQLInteger) {
                SQLInteger sInt = (SQLInteger) anns[0];
                if (sInt.name().length() < 1)
                    columnName = field.getName().toUpperCase();
                else
                    columnName = sInt.name();

                columnDefs.add(columnName + " INT" + getConstraints(sInt.constraints()));
            }

            if (anns[0] instanceof SQLString) {
                SQLString sString = (SQLString) anns[0];

                if (sString.name().length() < 1)
                    columnName = field.getName().toUpperCase();
                else
                    columnName = sString.name();

                columnDefs.add(columnName + " VARCHAR(" + sString.value() + ")" + getConstraints(sString.constraints()));
            }
        }

        resultSql.append("CREATE TABLE " + tableName + "(");

        for (String columnDef : columnDefs)
            resultSql.append("\n    " + columnDef + ",");

        String tableCreate = resultSql.substring(0, resultSql.length() - 1) + "\n)";

        return tableCreate;
    }

    private static String getConstraints(Constraints con) {
        StringBuilder sb = new StringBuilder();
        if (!con.allowNull())
            sb.append(" NOT NULL");
        if (con.primaryKey())
            sb.append(" PRIMARY KEY ");
        if (con.unique())
            sb.append(" UNIQUE " );

        return sb.toString();
    }

}
