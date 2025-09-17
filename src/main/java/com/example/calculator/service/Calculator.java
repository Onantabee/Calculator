package com.example.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class Calculator {
    public static final double PI = 3.142;
    public int sum(int a, int b) {
        return a + b;
    }
    public void subtract(int a, int b) {
        for(int i = 0; i <=100; i++)
        {
            System.out.println(i + "number");
        }
    }
}
