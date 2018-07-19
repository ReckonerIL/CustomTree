package com.javarush.task.task20.task2028;

@FunctionalInterface
public interface EntryVisitor<T> {
    Command visit(T subject);
}
