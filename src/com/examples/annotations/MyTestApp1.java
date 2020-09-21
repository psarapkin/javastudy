package com.examples.annotations;

public class MyTestApp1 {

    @MyNewAnnotation1(path = "hello", id = 10)
    public static String getSomething() {
        return "something";
    }


    public static void main(String[] args) {

    }
}
