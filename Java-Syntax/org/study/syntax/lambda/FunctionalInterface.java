package org.study.syntax.lambda;


interface MyFunctionalInterface {
    void execute();  // 这是唯一的抽象方法

    default void defaultMethod() {
        System.out.println("This is a default method.");
    }

    static void staticMethod() {
        System.out.println("This is a static method.");
    }
}

public class FunctionalInterface {
    public static void main(String[] args) {
        // 使用Lambda表达式实现函数式接口
        MyFunctionalInterface func = () -> System.out.println("Executing...");
        func.execute();

    }
}
