package edu.kndev.figure.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class CommandLine {
	// 读命令行内容，并返回打印信息
	public static String runCommands(String cmds, String localpath) {

		String str = "";
		String errStr = "";
		String charset = Charset.defaultCharset().toString();
		try {
			Process p = Runtime.getRuntime().exec("cmd /c " + cmds, null, new File(localpath));
			InputStream err = p.getErrorStream();
			InputStream in = p.getInputStream();
			str = processStdout(in, charset);
			errStr = processStdout(err, charset);
			if (!errStr.isEmpty()) {
				return str + errStr;
			}
		} catch (IOException e) {
			errStr = e.getMessage();
			return errStr;
		}
		return str;
	}

	private static String processStdout(InputStream in, String charset) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		try {
			String s;
			while ((s = buffer.readLine()) != null) {
				sb.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			buffer.close();
		}
		return sb.toString();
	}
}
