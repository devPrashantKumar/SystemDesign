package com.thecodeexperience.WithoutCompositeDesignPattern;

public class Main {
    public static void main(String[] args) {
        File file1 = new File("File01");
        File file2 = new File("File02");
        File file3 = new File("File03");
        File file4 = new File("File04");
        File file5 = new File("File05");

        Directory directory1 = new Directory("Directory01");
        Directory directory2 = new Directory("Directory02");
        Directory directory3 = new Directory("Directory03");

        directory3.add(file5);
        directory2.add(file4);
        directory2.add(file3);
        directory2.add(directory3);
        directory1.add(file2);
        directory1.add(file1);
        directory1.add(directory2);

        directory1.ls();
    }

}
