package com.tw.heima.service;

/**
 * 需求：
 * 1. 当输入3的倍数时，打印foo
 * 2. 当输入5的倍数时，打印bar
 * 3. 当输入3和5的倍数时，打印foobar
 * 4. 当输入其他值时，打印各自的值
 */
public class Printer {

    public static void main(String[] args) {
        Printer printer = new Printer();
        for (int i = 0; i < 20; i++) {
            System.out.println(printer.print(i));
        }
    }

    public String print(int value) {
        return "foo";
    }
}
