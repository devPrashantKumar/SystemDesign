package com.thecodeexperience.WithCompositeDesignPattern02;

public class Main {
    public static void main(String[] args) {
        Number number1 = new Number(5);
        Number number2 = new Number(6);
        Number number3 = new Number(7);
        Number number4 = new Number(8);
        Number number5 = new Number(9);

        Expression expression1 = new Expression(number1,number2,Operator.ADD);
        Expression expression2 = new Expression(expression1,number3,Operator.SUBTRACT);
        Expression expression3 = new Expression(number4,number5,Operator.MULTIPLY);
        Expression expression4 = new Expression(expression2,expression3,Operator.ADD);

        System.out.println(expression4.evaluate());
    }

}
