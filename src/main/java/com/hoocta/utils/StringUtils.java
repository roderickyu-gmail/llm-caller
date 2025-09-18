package com.hoocta.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * AI 有时会出错，比如让返回空串（''），有可能返回的是“无”、“null”等。所以除了判断是否真的为空外，还要判断长度是否太短，太短一般也是有问题的，当成空串来处理。更严谨的判断是将结果内容发给 AI 去额外判断一下。
	 * @param result
	 * @param minLength
	 * @return
	 */
	public static boolean isEmpty(String result, int minLength) {
		return isBlank(result) || result.length() < minLength;
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	public static String toString(InputStream inputStream) throws IOException {
       return convert(inputStream, "UTF-8");
    }
	
	public static List<String> removePrefixNumber(List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		List<String> retList = new LinkedList<>();
		for (String str : list) {
			retList.add(SequenceChecker.removeSequence(str));
		}
		return retList;
	}
	public static List<String> removeFirstAndLastSymbols(List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		List<String> retList = new LinkedList<>();
		for (String str : list) {
			if (isBlank(str)) {
				continue;
			}
			String cleanStr = SequenceChecker.removeSequence(str);
			cleanStr = removeLastSymbolIfAny(cleanStr);
			retList.add(cleanStr);
		}
		return retList;
	}
	
//	public static String toIndexString(List<String> list) {
//		if (list == null) {
//			return "";
//		}
//		StringBuilder howToWriteSb = new StringBuilder(); 
//		int index = 0;
//		for (String example : list) {
//			howToWriteSb.append(GlobalTextGenerator.gen(String.format("（%s）", ++index)));
//			howToWriteSb.append(example);
//			howToWriteSb.append("\r\n");
//		}
//		return howToWriteSb.toString();
//		
//	}
	
	private static String convert(InputStream inputStream, String charsetName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charsetName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n"); // 添加换行符以保持原有的换行
            }
        }
        return stringBuilder.toString();
    }
	
	/**
	 * 将一个字符串转成 long 型。使用时注意，有可能会有冲突，适用于能接受极少概率冲突场景。
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static long stringToUniqueLong(String input) {
        try {
            // 创建 SHA-256 哈希函数实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 计算哈希值
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 转换前 8 个字节为 long 型，并确保其为正整数
            return bytesToPositiveLong(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
	
	public static int getOptionalInt(String str) {
		if (isNotBlank(str)) {
			try {
				return Integer.parseInt(str);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

    private static long bytesToPositiveLong(byte[] hash) {
        // 将前16个字节转换为BigInteger
        BigInteger bigInt = new BigInteger(1, hash);
        // 取前 64 位，并确保结果是正整数
        return bigInt.longValue() & 0x7FFFFFFFFFFFFFFFL;
    }
    
	public static void main(String[] args) {
//		System.out.println(stringToUniqueLong("基本信息"));
//		System.out.println(stringToUniqueLong("工作经历"));
//		System.out.println(stringToUniqueLong("项目经历"));
//		System.out.println(removeSpecialCharacters("基本信息姓名:于越                                出生年月:2002年1月23日\n"
//				+ "民族:汉                                   身高:173cm\n"
//				+ "电话 15546523313                   政治面貌:预备党员\n"
//				+ "邮箱:1293654538@qq.com     毕业院校:哈尔滨信息工程学院\n"
//				+ "住址:黑龙江哈尔滨市道外区    学历:本科\n"
//				+ ""));
//		Map<String, String> map = new HashMap<>();
//		map.put("aaa", "bb");
//		map.put("cc", "dd");
//		System.out.println(toValueString(map));

		List<String> examples = List.of(
				"1) 负责金风科技海外业务板块的校园和社会招聘，成功交付全球22个岗位类别约50个职位，确保招聘完成率达95%，并提升人才质量，通过精准筛选实现高达90%的用人满", "没有序号的文本",
				"4) 从0到1负责策划6个招聘项目，如关键人才引进和面试官培训，提升了面试效率20%，使得重要岗位空缺时间缩短了15%。", "普通文本，没有序号", "文本内容有（3）序号不在开头的情况");
		List<String> list = new LinkedList<>();
		list.addAll(examples);
		list.add(null);

		examples = List.of("aa", "bbb", "cc");
		System.out.println("======toIndexString(list)============");
		System.out.println(toIndexString(list));

//		 System.out.println(removeNewLines("这是第一行\r\n这是第二行\n这是第三行\r\n"));

		System.out.println(toIndexStringValueMap(examples));
		System.out.println(toIndexString(toIndexStringValueMap(examples)));
		;

		System.out.println("=======simple name========");
		System.out.println(getSimpleSkillName(null));
		System.out.println(getSimpleSkillName("商务礼仪：哈哈哈"));
		System.out.println(getSimpleSkillName("商务礼仪A:哈哈哈"));

		System.out.println("====remove prefix number===");

		list = Arrays.asList("1)dsfs", "2, dsdsdf", "(3)dsdfd", "3， dsdsdf");
		System.out.println(removePrefixNumber(list));

		System.out.println("=======================================");

//		 System.out.println(removeFirstAndLastSymbols(Arrays.asList("(1)运营优化：提出运营优化方案，提升广告点击率和转化率。")));
//		 System.out.println(removeFirstAndLastSymbols(Arrays.asList("1. 运营优化：提出运营优化方案，提升广告点击率和转化率。")));
//		 System.out.println(removeFirstAndLastSymbols(Arrays.asList("2)运营优化：提出运营优化方案，提升广告点击率和转化率。")));
		System.out.println(toStringWithNextLine(Arrays.asList("20. 编写患者随访计划，确保治疗效果跟踪。", "15. 编写患者教育材料，提高患者自我管理能力。")));

		System.out.println("-----remove sequence-----");
		List<String> sequeceList = Arrays.asList("（1）设计季度技术挑战赛与跨团队合作选拔机制，通过架构迭代评审考察人才创新与实战能力，选拔高潜力人才",
				"（2）构建职业发展档案与动态调整机制，实施应届生三年阶梯晋升路径及技术管理培训生项目", "（27）完善专家委员会双轨评审体系，规范高级职级评定流程与知识传承路径建设标准");

		System.out.println(removePrefixNumber(sequeceList));
		System.out.println("======getOptionalInt=====");
		System.out.println(getOptionalInt(null));
		System.out.println(getOptionalInt("dfsfs"));
		System.out.println(getOptionalInt("3"));
		
		System.out.println(canonical("MYSQL"));
		System.out.println(canonical("My SQL"));
		System.out.println(canonical("My-SQL"));
		System.out.println(canonical("my_ SQL"));
		System.out.println(canonical("Message Queue"));
		System.out.println(canonical("Confluence 技术 方案文档协同"));
		
	}

    public static String unescapeUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\\' && i + 1 < chars.length && chars[i + 1] == 'u') {
                String hexValue = str.substring(i + 2, i + 6);
                sb.append((char) Integer.parseInt(hexValue, 16));
                i += 5; // 跳过已处理的转义序列
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }
    
    /**
     * 去除字符串中的换行符（\n）、回车符（\r）、制表符（\t）以及其他非可打印字符，并确保字符之间最多只留一个空格
     * @param str
     * @return
     */
	public static String removeSpecialCharacters(String str) {
		// 使用正则表达式替换所有非可打印字符
		str.replaceAll("[\\n\\r\\t]", "");
		// 使用正则表达式将多个连续的空格替换为一个空格
		str = str.replaceAll("\\s+", " ");
		// 去除字符串开头和结尾的空格
		return str.trim();
	}
	
	// 修改后的 toIndexString 方法
    public static <V> String toIndexString(Collection<V> list) {
    	Map<Integer, String> map = toIndexStringValueMap(list);
    	return toIndexString(map);
    }
    public static <V> String toString(Collection<V> list) {
    	if (CollectionUtils.isEmpty(list)) {
    		return "";
    	}
    	StringBuilder sb = new StringBuilder();
    	for (V v : list) {
    		sb.append(v);
    		sb.append(", ");
    	}
    	return sb.toString();
    }
    public static <V> String toStringWithNextLine(Collection<V> list) {
    	if (CollectionUtils.isEmpty(list)) {
    		return "";
    	}
    	StringBuilder sb = new StringBuilder();
    	for (V v : list) {
    		sb.append(v);
    		sb.append("\r\n");
    	}
    	return sb.toString();
    }
    public static <V> String toIndexString(Map<Integer, V> map) {
		if (CollectionUtils.isEmpty(map)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, V> entry : map.entrySet()) {
			// TODO 注意，这里是中文符号
			sb.append("（");
			sb.append(entry.getKey());
			sb.append("）");
			sb.append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
    public static <V> Map<Integer, String> toIndexStringValueMap(Collection<V> list) {
    	Map<Integer, String> retMap = new HashMap<>();
        if (list == null) {
            return retMap;
        }

        // 检查列表中是否有部分元素有序号，部分没有
        boolean hasAnySequence = false;
        boolean hasNoSequence = false;

        for (V v : list) {
        	if (v == null) {
        		continue;
        	}
        	String example = v.toString();
            if (SequenceChecker.hasSequence(example)) {
                hasAnySequence = true;
            } else {
                hasNoSequence = true;
            }
            if (hasAnySequence && hasNoSequence) {
                break; // 一旦发现混合情况，直接退出循环
            }
        }

        int index = 0;

        for (V v : list) {
        	if (v == null) {
        		continue;
        	}
        	String example = v.toString();
            String cleanExample = example;
            // 如果存在部分带序号的情况，统一去除序号
            if (hasAnySequence && hasNoSequence) {
                cleanExample = SequenceChecker.removeSequence(example);
            }
            // 为每个元素生成新的序号
            retMap.put(++index, cleanExample);
        }

        return retMap;
    }
    
	public static <K, V> String toValueString(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		if (!CollectionUtils.isEmpty(map)) {
			for (V v : map.values()) {
				sb.append(v);
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
//	public static <V> Map<Integer, V> toIndexMap(Collection<V> list) {
//		Map<Integer, V> retMap = new HashMap<>();
//		if (!CollectionUtils.isEmpty(list)) {
//			int index = 0;
//			for (V v : list) {
//				retMap.put(++index, v);
//			}
//		}
//		return retMap;
//	}
	public static String removeNewLines(String str) {
		if (isBlank(str)) {
			return str;
		}
		return str.replaceAll("\\r\\n|\\n|\\r", "");
	}
	/**
	 * 若字符串最后一个字符是符号，那么删掉
	 * @param input
	 * @return
	 */
	public static String removeLastSymbolIfAny(String input) {
        if (isBlank(input)) {
            return input; // 空字符串或 null 不处理
        }
        // 正则匹配最后一个符号
        return input.replaceAll("[\\p{P}\\p{S}]$", ""); 
    }
	
	public static String preprocessJson(String input) {
	    // 转义所有未转义的双引号
	    return input.replaceAll("(?<!\\\\)\"", "\\\\\"");
	}
	
	public static String getSimpleSkillName(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		int index = str.indexOf(":");
		if (index > 0) {
			return str.substring(0, index);
		}
		index = str.indexOf("：");
		if (index > 0) {
			return str.substring(0, index);
		}
		return str;
	}
	/**
	 * 把同义字符串规约到一个“机器可比”的形式
	 * 1. 统一大小写
	 * 2. 去掉所有空白符
	 * 3. 去掉所有 Unicode 标点
	 * 4. Unicode NFKD 兼容分解，去掉重音、全形转半形
	 */
	private static final Pattern PUNCT = 
	        Pattern.compile("\\p{Punct}|\\p{IsPunctuation}");
	public static String canonical(String s) {
	    if (s == null) return "";
	    String nfkd = Normalizer.normalize(s, Normalizer.Form.NFKD);
	    String noPunct = PUNCT.matcher(nfkd).replaceAll("");
	    String noSpace = noPunct.replaceAll("\\s+", "");
	    return noSpace.toLowerCase(Locale.ROOT);
	}
	
	/**
	 * 用勒文斯坦 / Jaro-Winkler 做“兜底”近似匹配
	 * @param candidate
	 * @param canon2Original
	 * @return
	 */
}

