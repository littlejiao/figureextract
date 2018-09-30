package edu.kndev.figure.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FigureParser {
	public static void main(String[] args) {
		Map<String, String> map = parsefig("e1", "D:/pdffigures2/");
		for (Map.Entry<String, String> m : map.entrySet()) {
			System.out.println(m.getKey());
			System.out.println(m.getValue());
		}
	}

	// 解析pdf，获取pdf中的图表及描述文字到本地,filename为pdf保存本地的名字，localpath为pdffigure项目在本地的路径
	public static Map<String, String> parsefig(String filename, String project_path) {
		Map<String, String> map = new HashMap<String, String>();

		System.out.println(filename + ": 开始解析图表...");
		String cmdline = "sbt \"run-main org.allenai.pdffigures2.FigureExtractorBatchCli " + project_path + "/pdf/"
				+ filename + ".pdf" + " -m fig- -d data-\"";
		if (!findByLocal(filename, project_path)) {
			CommandLine.runCommands(cmdline, project_path);
			saveToOneFile(filename, project_path);
		}
		// 再次判断存储解析后图表的文件夹是否为空，如果为空，证明该文章解析不了，返回空的map
		if (!findByLocal(filename, project_path)) {
			System.out.println("解析失败: 本地没有找到对应的pdf或该pdf解析不了.");
			return map;
		}
		map = JsonParser.jsonParse(project_path + "result/" + filename + "/" + "data-" + filename + ".json");
		if (map == null)
			System.out.println(filename + ": 该pdf解析结果为空...");
		else {
			System.out.println(filename + ": 解析成功, 已保存至本地" + project_path + "result/" + filename + "/");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				map.replace(entry.getKey(), entry.getValue(), project_path + filename + "/" + entry.getValue());
			}
		}
		return map;
	}

	// 先查看本地是否已保存该pdf的图表
	private static Boolean findByLocal(String filename, String project_path) {
		File srcFile = new File(project_path + "result/" + filename);
		if (!srcFile.exists()) {
			srcFile.mkdirs();
		}

		File[] files = srcFile.listFiles();
		if(files.length>0) {
			for (File file : files) {
				if (file.getName().startsWith("data-" + filename) || file.getName().startsWith("fig-" + filename))
					return true;
			}
		}else
			return false;
		return false;
	}

	// 将属于一个pdf的所有结果放在以pdf文件名命名的文件夹里
	private static void saveToOneFile(String filename, String srcpath) {
		try {
			File startFile = new File(srcpath);
			for (File file : startFile.listFiles()) {
				if (file.getName().startsWith("data-" + filename) || file.getName().startsWith("fig-" + filename)) {
					File resultfile = new File(srcpath + "result/");
					if (!resultfile.exists())
						resultfile.mkdirs();
					file.renameTo(new File(srcpath + "result/" + filename + "/" + file.getName()));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
