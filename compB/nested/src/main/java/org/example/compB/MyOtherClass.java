package org.example.compB;

import javax.validation.ValidationException;

import org.example.compA.MyClass;

public class MyOtherClass {

    public static void main(String args[]) {
        MyClass myClass = new MyClass();
        System.out.println("MyClass: "  + myClass);
        try {
            myClass.validate();
        } catch (ValidationException e) {
            System.out.println(e);
        }
    }

}
