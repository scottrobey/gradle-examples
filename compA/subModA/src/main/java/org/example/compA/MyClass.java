package org.example.compA;

import javax.validation.ValidationException;

public class MyClass {


    public void validate() throws ValidationException {
        throw new ValidationException("orange man bad!");
    }

  
}
