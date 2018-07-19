package com.javarush.task.task20.task2028;

@FunctionalInterface
public interface CustomTreeVisitor<T> {
    Command visit(T subject);
}
