package com.thecodeexperience.WithCompositeDesignPattern02;

public class Number implements EvaluateExpression {
    Integer operand;

    Number(Integer operand){
        this.operand=operand;
    }

    public Integer evaluate(){
        return operand;
    }
}
