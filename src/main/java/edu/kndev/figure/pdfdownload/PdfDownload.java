package edu.kndev.figure.pdfdownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.io.Files;

public class PdfDownload {
	private static CloseableHttpClient client;
	private static final String[] User_Agent = {
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.119 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134",
			"Opera/9.80 (Windows NT 6.1; WOW64; U; en) Presto/2.10.229 Version/11.62",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27" };

	public static void main(String[] args) throws ClientProtocolException, IOException {
		System.out.println(getUrl("https://www.sciencedirect.com/science/article/pii/S0021850200000215"));
		//readFile("D:\\Tools\\pdffigures2\\pdffigures2\\e1.pdf", "D:\\pdffigures2\\pdf\\e3.pdf");
		
		//System.out.println(PdfDownload.pdfdown("https://www.aanda.org/articles/aa/pdf/2017/03/aa30064-16.pdf", "D:/pdffigures2/pdf/a.pdf"));
	}
	
	//srcurl为pdf对应的网站，filepath为pdf存储在本地的绝对路径，如D:/pdffigures2/pdf/a.pdf
	public static int savepdf(String srcurl, String filepath) throws IOException, ClientProtocolException {
		int statuscode = 0;
		String url = PdfDownload.getUrl(srcurl);
		statuscode = PdfDownload.pdfdown(url, filepath);
		return statuscode;
	}

	// sciencedirect会有一个跳转页面，从跳转页面获取pdf所在的网页,srcurl是起始网页
	private static String getUrl(String srcurl) throws IOException {
		String url = "";
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(srcurl + "/pdf");
		httpGet.addHeader("User-Agent", User_Agent[new Random().nextInt(5)]);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000)
				.setSocketTimeout(100000).build();
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = client.execute(httpGet);
		try {
			Thread.sleep(new Random().nextInt(500) + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpEntity httpEntity = response.getEntity();
			
			Document doc = Jsoup.parse(EntityUtils.toString(httpEntity));
			System.out.println(doc.toString());
			Elements e = doc.select("script");
			Pattern p = Pattern.compile("\'.*\'");
			Matcher m = p.matcher(e.eq(1).toString());
			while (m.find()) {
				url = m.group().replaceAll("\'", "");
			}
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			response.close();
		}
		return url;
	}

	// url为pdf所在的真正页面，将网页内容以字节的方式下载
	public static int pdfdown(String url, String savePath) throws ClientProtocolException, IOException {
		int statuscode=404;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", User_Agent[new Random().nextInt(5)]);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000)
				.setSocketTimeout(100000).build();
		httpGet.setConfig(requestConfig);

		CloseableHttpResponse response = client.execute(httpGet);
		try {
			Thread.sleep(new Random().nextInt(500) + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpEntity httpEntity = response.getEntity();
			byte[] bytes = EntityUtils.toByteArray(httpEntity);
			File file = new File(savePath);
			Files.write(bytes, file);
			statuscode = 200;
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			response.close();
		}
		return statuscode;
	}

	// filepath为绝对路径，如D:/pdffigures2/a.pdf,objectpath为目标路径
	public static void readFile(String srcpath, String objectpath) throws IOException {
		File file = new File(srcpath);
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(objectpath)));
			byte[] by = new byte[1024];
			int temp = 0;
			while ((temp = in.read(by)) != -1) {
				out.write(by, 0, temp);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
