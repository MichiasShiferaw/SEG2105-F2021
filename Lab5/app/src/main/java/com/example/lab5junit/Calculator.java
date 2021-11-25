package com.example.lab5junit;

import static java.lang.Math.pow;

public class Calculator {

    public double add(String op1, String op2) {
        if (op1.equals("null")||op2.equals("null")){
            return -1;
        } else {
            return Double.parseDouble(op1) + Double.parseDouble(op2);
        }
    }

    public double diff(String op1, String op2) {
        if (op1.equals("null")||op2.equals("null")){
            return -1;
        } else {
            return Double.parseDouble(op1) - Double.parseDouble(op2);
        }
    }

    public double div(String op1, String op2) {
        if (op1.equals("null")||op2.equals("null")){
            return -1;
        } else if (op2.equals("0")) {
            return -1.1;
        } else {
            return Double.parseDouble(op1) / Double.parseDouble(op2);
        }
    }

    public double mul(String op1, String op2) {
        if (op1.equals("null")||op2.equals("null")){
            return -1;
        } else {
            return Double.parseDouble(op1) * Double.parseDouble(op2);
        }
    }

    public double expon(String op1, String op2) {
        if (op1.equals("null")||op2.equals("null")) {
            return -1;
        } else if (op1.equals("0") && op2.equals("0")) {
            return -1.1;
        } else if (op1.equals("0") && Double.parseDouble(op2) < Double.parseDouble("0")) {
            return -1.2;
        } else {
            return pow(Double.parseDouble(op1), Double.parseDouble(op2));
        }
    }

    public double log10(String op1){
        if (op1.equals("null")){
            return -1;
        } else if (Double.parseDouble(op1) < Double.parseDouble("0")) {
            return -1.1;
        } else if (op1.equals("0")) {
            return -1.2;
        } else {
            return Math.log10(Double.parseDouble(op1));
        }
    }

    public double ceilingval(String op1){
        if (op1.equals("null")){
            return -1;
        } else {
            return Math.ceil(Double.parseDouble(op1));
        }
    }
}
