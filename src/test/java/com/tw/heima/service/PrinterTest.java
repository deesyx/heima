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

    @Test
    void should_print_foobar_when_input_is_15() {
        Printer printer = new Printer();

        String res = printer.print(15);

        assertEquals("foobar", res);
    }

    @Test
    void should_print_foo_when_input_is_6() {
        Printer printer = new Printer();

        String res = printer.print(6);

        assertEquals("foo", res);
    }

    @Test
    void should_print_bar_when_input_is_10() {
        Printer printer = new Printer();

        String res = printer.print(10);

        assertEquals("bar", res);
    }

    @Test
    void should_print_foobar_when_input_is_30() {
        Printer printer = new Printer();

        String res = printer.print(30);

        assertEquals("foobar", res);
    }

    @Test
    void should_print_1_when_input_is_1() {
        Printer printer = new Printer();

        String res = printer.print(1);

        assertEquals("1", res);
    }
}