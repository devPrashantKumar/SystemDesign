package com.thecodeexperience.WithoutTemplateMethodDesignPattern;

/** The thing being imported. Not the point of the pattern — just cargo. */
public record Employee(String name, int salary) {

    @Override
    public String toString() {
        return name + " (" + salary + ")";
    }

}
