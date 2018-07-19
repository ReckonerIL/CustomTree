package com.javarush.task.task20.task2028;

import java.util.List;

public class Solution {
    public static void main(String[] args) {
        List<Integer> list = new CustomTree<>();

        for (int i = 1; i < 16; i++) {
            list.add(i);
        }

        System.out.println("List size is " + list.size());
        System.out.println("Expected parent is 3, actual parent is " + ((CustomTree<Integer>) list).getParent(8));
        System.out.println("Expected parent is null, actual parent is " + ((CustomTree<Integer>) list).getParent(20));

        list.remove(Integer.valueOf(3));
        System.out.println("Expected parent is null, actual parent is " + ((CustomTree<Integer>) list).getParent(8));

        list.add(16);
        System.out.println("Expected parent is 9, actual parent is " + ((CustomTree<Integer>) list).getParent(16));

        list.remove(Integer.valueOf(4));
        list.remove(Integer.valueOf(5));
        list.remove(Integer.valueOf(6));
        System.out.println("Expected true, actual " + list.add(20));
        System.out.println("Expected parent is 1, actual parent is " + ((CustomTree<Integer>) list).getParent(20));


    }
}
