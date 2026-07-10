package com.thecodeexperience.CommandPattern;

import java.util.HashMap;
import java.util.Stack;

public class MyRemoteControl{
    HashMap<String,ICommand> commandMap;
    Stack<String> commandStack;

    MyRemoteControl(){
        this.commandMap = new HashMap<>();
        this.commandStack = new Stack<>();
    }

    public void setCommand(String commandName, ICommand command) {
        this.commandMap.put(commandName,command);
    }

    public void pressButton(String command){
        this.commandMap.get(command).execute();
        this.commandStack.push(command);
    }

    public void undo(){
        if(this.commandStack.isEmpty())
            System.out.println("No operation left to Undo");
        else
            this.commandMap.get(this.commandStack.pop()).undo();
    }
}
