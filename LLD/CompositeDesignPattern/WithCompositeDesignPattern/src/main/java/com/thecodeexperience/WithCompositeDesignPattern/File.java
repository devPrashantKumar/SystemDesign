package com.thecodeexperience.WithCompositeDesignPattern;

public class File implements FileSystem {
    String fileName;

    File(String fileName){
        this.fileName=fileName;
    }

    public void ls(int indentation){
        System.out.println(" ".repeat(indentation)+"File Name : "+fileName);
    }
}
