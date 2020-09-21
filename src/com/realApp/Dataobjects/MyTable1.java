package com.realApp.Dataobjects;

import com.examples.annotations.database.DBTable;
import com.examples.annotations.database.SQLInteger;
import com.examples.annotations.database.SQLString;

@DBTable
public class MyTable1 {
    @SQLInteger int id;
    @SQLString(30) String name;
    @SQLString(30) String surname;
    @SQLString(10) String phone;

    public MyTable1(int id, String name, String surname, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
