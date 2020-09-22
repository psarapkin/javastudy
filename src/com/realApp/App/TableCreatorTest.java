package com.realApp.App;

import com.examples.annotations.database.DBTable;
import com.examples.annotations.database.SQLInteger;
import com.examples.annotations.database.SQLString;
import com.realApp.Dataobjects.MyTable1;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class TableCreatorTest {

    public static boolean checkTableExists(Connection conn, String schemaName, Class<?> tableClass) {
        boolean result = false;

        try {
            Statement stmt = conn.createStatement();
            String sql = "select * from SYS.SYSSCHEMAS ss inner join SYS.SYSTABLES st on ss.SCHEMAID = st.SCHEMAID where ss.SCHEMANAME = '" + schemaName + "'";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs != null) {

                while (rs.next()) {
                    String currentTableName = rs.getString(5);
                    if (Objects.equals(currentTableName.toUpperCase(), tableClass.getSimpleName().toUpperCase())) {
                        result = true;
                    }

                }

            }

        }
        catch(SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return result;
    }

    public static void createTable(Connection conn, Class<?> cl) {
        String sqlCommand = TableCreator.getSqlCommand(cl);
        System.out.println("sqlCommand = " + sqlCommand);
        System.out.println("Creating table " + cl.getSimpleName());

        try {
            Statement st = conn.createStatement();

            st.executeUpdate(sqlCommand);

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }


    }

    public static void insertNewRecord(Connection conn, MyTable1 row) {
        StringBuilder sb = new StringBuilder();

        Class<?> tc = row.getClass();

        if (tc.getAnnotation(DBTable.class) == null) {
            System.err.println("Table is not marked as DBTable");
            return;
        }

        sb.append("INSERT INTO ");
        sb.append(tc.getSimpleName());
        sb.append("(");

        var fields = tc.getDeclaredFields();
        var sqlValues = new StringBuilder();

        sqlValues.append("VALUES(");

        for (int i = 0; i <= fields.length-1; i++) {
            var field = fields[i];

            field.setAccessible(true);

            sb.append(field.getName());

            var annotations = field.getAnnotations();

            if (annotations.length > 1) {
                System.err.println("Too many annotations for field " + field.getName());
            }

            try {

                if (annotations[0] instanceof SQLInteger) {
                    sqlValues.append(field.get(row));
                }
                if (annotations[0] instanceof SQLString) {
                    sqlValues.append("'");
                    sqlValues.append(field.get(row));
                    sqlValues.append("'");
                }
            }
            catch(IllegalAccessException ae) {
                ae.printStackTrace();
            }

            if (i < fields.length-1) {
                sb.append(",");
                sqlValues.append(",");
            }
        }

        sb.append(")");
        sqlValues.append(")");

        sb.append(sqlValues);


        try {
            var st = conn.createStatement();

            System.out.println("sb = " + sb);

            st.executeUpdate(sb.toString());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static <T extends Object> List<T> getRowsFromBase(Connection conn, Class<T> dbTableClass) {
        var myList = new ArrayList<T>();

        if (dbTableClass.getAnnotation(DBTable.class) == null) {
            System.err.println("There is no DBTable annotation for table");
            return null;
        }

        Field[] fields = dbTableClass.getDeclaredFields();

        List<Class> mappedClassFields = new ArrayList<Class>();

        for (Field field: fields) {
            mappedClassFields.add(field.getClass());
        }

        String tableName = dbTableClass.getSimpleName().toUpperCase();

        String selectSql = "select * from " + tableName;

        try {
            var st = conn.createStatement();

            st.executeQuery(selectSql);

            var results = st.getResultSet();

            var metaData = results.getMetaData();

            var dbTableConstructor = dbTableClass.getConstructors()[0];

            while (results.next()) {
                var objectVals = new ArrayList<Object>();

                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    if (metaData.getColumnType(j) == Types.INTEGER) {
                        objectVals.add(results.getInt(j));
                    }
                    if (metaData.getColumnType(j) == Types.VARCHAR) {
                        objectVals.add(results.getString(j));
                    }
                }

                T newObject = (T) dbTableConstructor.newInstance(objectVals.toArray());

                myList.add(newObject);
            }

        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException sqlException ) {
            sqlException.printStackTrace();
            System.err.println("Something went wrong while trying to select all rows from " + tableName + "...");
            return null;
        }


        return myList;
    }

    public static void main(String[] args) {

        String connectionString = "jdbc:derby://localhost:1527/mydb";
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectionString);

        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (!(checkTableExists(connection, "APP", MyTable1.class))) {
            createTable(connection, MyTable1.class);
        }

//        for (int i = 0; i < 20; i++) {
//            String name = "MrBlack no." + i;
//            String surname = "Handsome no." + i;
//            String phone = new StringBuilder().append(new Random().nextInt(90000) + 10000).toString();
//
//            var newRecord = new MyTable1(i, name, surname, phone);
//            insertNewRecord(connection, newRecord);
//        }

        var allResults = getRowsFromBase(connection, MyTable1.class);

        for (MyTable1 row: allResults) {
            System.out.println("id = " + row.getId() + ", name = " + row.getName() + ", surname = " + row.getSurname() + ", phone = " + row.getPhone());

        }

    }
}
