package com.hoocta.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceChecker {

	// 正则表达式匹配全角和半角的各种符号序号形式
    private static final Pattern SEQUENCE_PATTERN = Pattern.compile(
        "^\\s*(" +
        // 阿拉伯数字，支持全角和半角括号及符号
        "\\(\\d+\\)|\\d+\\)|\\d+\\.|\\（\\d+\\）|\\d+\\）|" +
        // 罗马数字，支持全角和半角括号及符号
        "\\(\\w+\\)|\\w+\\)|\\w+\\.|\\（\\w+\\）|\\w+\\）|" +
        // 汉字数字，支持全角和半角括号及符号
        "\\(一\\)|一\\)|一\\.|\\（一\\）|一\\）|" +
        // 圆圈数字
        "①|②|③|" +
        // 方括号数字
        "\\[\\d+\\]|\\[\\w+\\]" +
        ")\\s*"
    );

    // 判断给定字符串开头是否有序号
    public static boolean hasSequence(String text) {
        Matcher matcher = SEQUENCE_PATTERN.matcher(text);
        return matcher.find();
    }
    // 只删除开头的序号。Note：这种格式[bbbb]也会被当成序号给删掉。
    public static String removeSequence(String text) {
        return text.replaceFirst(SEQUENCE_PATTERN.pattern(), "").trim();
    }
    
    public static void main(String[] args) {
        String[] testCases = {
            // 阿拉伯数字
            "(1) 这是带括号的阿拉伯数字序号",
            "1) 这是带后括号的阿拉伯数字序号",
            "1. 这是带点号的阿拉伯数字序号",

            // 罗马数字
            "(I) 这是带括号的罗马数字序号",
            "I) 这是带后括号的罗马数字序号",
            "I. 这是带点号的罗马数字序号",
            "(II) 这是带括号的罗马数字序号",
            "II) 这是带后括号的罗马数字序号",
            "II. 这是带点号的罗马数字序号",

            // 英文字母
            "(A) 这是带括号的大写字母序号",
            "A) 这是带后括号的大写字母序号",
            "A. 这是带点号的大写字母序号",
            "(a) 这是带括号的小写字母序号",
            "a) 这是带后括号的小写字母序号",
            "a. 这是带点号的小写字母序号",

            // 汉字数字
            "(一) 这是带括号的汉字数字序号",
            "一) 这是带后括号的汉字数字序号",
            "一. 这是带点号的汉字数字序号",
            "(二) 这是带括号的汉字数字序号",
            "二) 这是带后括号的汉字数字序号",
            "二. 这是带点号的汉字数字序号",

            // 圆圈数字
            "① 这是带圆圈的阿拉伯数字序号",
            "② 这是带圆圈的阿拉伯数字序号",
            "③ 这是带圆圈的阿拉伯数字序号",

            // 方括号数字
            "[1] 这是带方括号的阿拉伯数字序号",
            "[2] 这是带方括号的阿拉伯数字序号",
            "[1003] 这是带方括号的阿拉伯数字序号",

            // 不包含序号的情况
            "这是普通的文本，没有序号",
            "无序号的文本",
            "普通文本没有特殊序号标记",
            
            "文本特殊序号标记③ ",
            "文本特③殊序号标记 ",
            "（1）已经有序号的文本",
            "1）已经有序号的文本",
            "1)已经有序号的文本", 
            "1) 主导组织多项技术服务相关培训项目，基于部门业务需求制定了详细培训方案，成功提升团队技术服务水平，减少了20%的客户投诉率。"
        };

        // 检查每个测试用例
        for (String testCase : testCases) {
            if (hasSequence(testCase)) {
                System.out.println("该文本已包含序号: " + testCase);
            } else {
                System.out.println("该文本没有序号: " + testCase);
            }
        }
        System.out.println("===b===");
        System.out.println(SequenceChecker.removeSequence("[bbbb]"));
    }

}
