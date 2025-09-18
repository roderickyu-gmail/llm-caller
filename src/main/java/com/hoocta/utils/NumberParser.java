package com.hoocta.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * 数字解析器，从任意字符串中解析出里面的数字
 */
public class NumberParser {
	
	public static List<Integer> parseSequenceNumbers(String input) {
        // 正则表达式匹配数字
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<Integer> result = new LinkedList<>();

        while (matcher.find()) {
            result.add(Integer.parseInt(matcher.group()));
        }

        return result;
    }

    public static void main(String[] args) {
//        String input = "1）2）、1、2、（1）【1】{1}、1/2/3 33 4";
    	String input = " 1 2 3 4 ";
    	input = "'1'";
    	input = null;
        List<Integer> parsedNumbers = parseSequenceNumbers(input);

        System.out.println("Parsed Sequence Numbers:");
        for (Integer number : parsedNumbers) {
            System.out.println(number);
        }
    }
}
