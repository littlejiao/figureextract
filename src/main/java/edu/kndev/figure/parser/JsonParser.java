package edu.kndev.figure.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.io.Files;

public class JsonParser {

	// 对保存到本地的data-filename.json进行解析，获得一篇pdf中的所有解析出来的图表以及caption信息,并进行排序
	// 需要注意两个情况：
	// 1.解析结果为空，返回一个data-filename.json文件，文件为空
	// 2.解析不了，不返回任何东西
	public static Map<String, String> jsonParse(String filepath) {
		// BiMap<String, String> bm = HashBiMap.create();
		Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				int cr = 0;
				// TODO Auto-generated method stub
				int a = o1.charAt(0) - o2.charAt(0);
				if (a != 0) {
					cr = (a > 0) ? 3 : -1;
				} else {
					cr = getNumberInString(o1) - getNumberInString(o2);
				}
				return cr;
			}
		});
		List<String> input = null;
		try {
			input = Files.readLines(new File(filepath), Charset.forName("GBK"));
			StringBuilder sb = new StringBuilder();
			for (String string : input) {
				sb.append(string + "\n");
			}
			JSONArray json = new JSONArray(sb.toString());
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsobject = json.getJSONObject(i);

				map.put(jsobject.getString("caption"), jsobject.getString("renderURL"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	// 从Figure1/Table1类似的字符串中提取字符串进行比较
	private static int getNumberInString(String str) {
		String string = getFigureOrTable(str);
		StringBuffer sb = new StringBuffer();
		Pattern p1 = Pattern.compile("\\d+");
		Pattern p2 = Pattern.compile("(\\Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ|ⅰ|ⅱ|ⅲ|ⅳ|ⅴ|ⅵ|ⅶ|ⅷ|ⅸ|ⅹ)+");
		Matcher m1 = p1.matcher(str);
		Matcher m2 = p2.matcher(str);
		if (m1.find())
			sb.append(m1.group());
		else if (m2.find())
			sb.append(romanToInt(m2.group()));
		else
			sb.append("999");
		return Integer.parseInt(sb.toString());
	}

	// 判断提取的图表是Figure还是Table,如果提取到的是乱码，类似于“Fig. S”这种,就返回前8位字符
	private static String getFigureOrTable(String str) {
		Pattern p1 = Pattern.compile("(Figure|Fig|figure|fig|Table|TAB|Tab|TABLE)(\\s|\\S)+?\\d+");
		Pattern p2 = Pattern.compile(
				"(Table|TAB|Tab|TABLE|Figure|Fig|figure|fig)(\\s|\\S)+?(\\Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ|ⅰ|ⅱ|ⅲ|ⅳ|ⅴ|ⅵ|ⅶ|ⅷ|ⅸ|ⅹ|)+");
		Matcher m1 = p1.matcher(str);
		Matcher m2 = p2.matcher(str);
		if (m1.find()) {
			return m1.group();
		} else if (m2.find()) {
			return m2.group();
		}
		return str.substring(0, 9);
	}

	// 识别罗马数字，将识别到的罗马数字转成int
	public static int romanToInt(String s) {
		if (s == null || s.length() == 0)
			return -1;
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		map.put('I', 1);
		map.put('V', 5);
		map.put('X', 10);
		map.put('L', 50);
		map.put('C', 100);
		map.put('D', 500);
		map.put('M', 1000);
		int len = s.length(), result = map.get(s.charAt(len - 1));
		for (int i = len - 2; i >= 0; i--) {
			if (map.get(s.charAt(i)) >= map.get(s.charAt(i + 1)))
				result += map.get(s.charAt(i));
			else
				result -= map.get(s.charAt(i));
		}
		return result;
	}
}
