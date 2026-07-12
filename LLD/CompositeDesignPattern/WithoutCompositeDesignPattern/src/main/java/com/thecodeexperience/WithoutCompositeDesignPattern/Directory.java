package com.thecodeexperience.WithoutCompositeDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Directory {
    String directoryName;
    List<Object> directoryContent = new ArrayList<>();

    Directory(String directoryName){
        this.directoryName=directoryName;
    }

    public void add(Object item){
        directoryContent.add(item);
    }

    public void ls(int indentation){
        System.out.println(" ".repeat(indentation)+"Directory Name : "+directoryName);

        for(Object item : directoryContent){
            if(item instanceof Directory){
                ((Directory) item).ls(indentation+1);
            }
            if(item instanceof File){
                ((File) item).ls(indentation+1);
            }
        }
    }

}
