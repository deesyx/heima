package com.tw.heima.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrinterTest {

    @Test
    void should_print_foo_when_input_is_3() {
        Printer printer = new Printer();

        String res = printer.print(3);

        assertEquals("foo", res);
    }

    @Test
    void should_print_bar_when_input_is_5() {
        Printer printer = new Printer();

        String res = printer.print(5);

        assertEquals("bar", res);
    }
}