package org.example.compA;

import javax.validation.ValidationException;

import org.junit.Test;

public class TestForMyClass {

    @Test(expected = ValidationException.class)
    public void testValidate() {
        MyClass cut = new MyClass();
        cut.validate();
    }
}
