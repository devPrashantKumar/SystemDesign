package com.thecodeexperience.WithCompositeDesignPattern02;

import java.util.ArrayList;
import java.util.List;

public class Expression implements EvaluateExpression {
    EvaluateExpression leftExpression;
    EvaluateExpression rightExpression;
    Operator operator;

    Expression(EvaluateExpression leftExpression, EvaluateExpression rightExpression, Operator operator){
        this.leftExpression=leftExpression;
        this.rightExpression=rightExpression;
        this.operator=operator;
    }

    public Integer evaluate(){
        return switch (operator){
            case ADD -> leftExpression.evaluate() + rightExpression.evaluate();
            case SUBTRACT -> leftExpression.evaluate() - rightExpression.evaluate();
            case MULTIPLY -> leftExpression.evaluate() * rightExpression.evaluate();
            case DIVIDE -> leftExpression.evaluate() / rightExpression.evaluate();
        };
    }

}
