package com.thecodeexperience.WithoutCompositeDesignPattern;

public class File {
    String fileName;

    File(String fileName){
        this.fileName=fileName;
    }

    public void ls(){
        System.out.println("File Name : "+fileName);
    }
}
