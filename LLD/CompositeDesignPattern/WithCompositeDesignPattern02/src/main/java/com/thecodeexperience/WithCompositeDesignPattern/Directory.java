package com.thecodeexperience.WithCompositeDesignPattern;

import java.util.ArrayList;
import java.util.List;

public class Directory implements FileSystem {
    String directoryName;
    List<FileSystem> directoryContent = new ArrayList<>();

    Directory(String directoryName){
        this.directoryName=directoryName;
    }

    public void add(FileSystem item){
        directoryContent.add(item);
    }

    public void ls(int indentation){
        System.out.println(" ".repeat(indentation)+"Directory Name : "+directoryName);

        for(FileSystem item : directoryContent){
            item.ls(indentation+1);
        }
    }

}
