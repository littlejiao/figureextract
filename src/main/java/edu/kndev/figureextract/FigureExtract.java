package edu.kndev.figureextract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import edu.kndev.figure.entity.Figure;
import edu.kndev.figure.parser.FigureParser;
import edu.kndev.figure.pdfdownload.PdfDownload;

/**
 * 实现：下载文章，解析pdf中的图表，保存至本地，返回Figure对象。
 * 
 * @author littlehang
 * 
 */
public class FigureExtract {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		FigureExtract fe = new FigureExtract();
		
		long time1 = System.currentTimeMillis();
		Figure f1 = fe.extractByUrl("https://www.aanda.org/articles/aa/pdf/2017/03/aa30073-16.pdf","D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time1);
		
		long time2 = System.currentTimeMillis();
		Figure f2=  fe.extractByUrl("http://cn.arxiv.org/pdf/1505.01072v1","D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time2);
		
		long time3 = System.currentTimeMillis();
		Figure f3 = fe.extractByUrl("http://iopscience.iop.org/article/10.3847/2041-8213/835/1/L1/pdf", "D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time3);
		
		long time4 = System.currentTimeMillis();
		Figure f4 = fe.extractByUrl_SD("https://www.sciencedirect.com/science/article/pii/S2212671614001255", "D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time4);
		
		long time5 = System.currentTimeMillis();
		Figure f5 = fe.extractByPath("D:\\Tools\\pdffigures2\\pdffigures2\\e1.pdf", "D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time5);
		
		long time6 = System.currentTimeMillis();
		Figure f6 = fe.extractByUrl("https://arxiv.org/pdf/1809.10020.pdf", "D:/pdffigures2/");
		System.out.println(System.currentTimeMillis()-time6);
		
		Figure f7 = fe.extractByUrl("https://www.sciencedirect.com/science/article/pii/S0021850200000215/pdfft?md5=adde1366559b96ba9b7d918d1dec845e&amp;pid=1-s2.0-S0021850200000215-main.pdf", "D:/pdffigures2/");
		System.out.println(f1.toString());
		System.out.println(f2.toString());
		System.out.println(f3.toString());
		System.out.println(f4.toString());
		System.out.println(f5.toString());
		System.out.println(f6.toString());
		
	}

	/**
	 * extractByUrl_SD: 从sciencedirect数据库下载pdf到本地project_path/pdf/,
	 * 再解析图表保存到本地project_path/result/filename/
	 * 
	 * @param url:
	 *            pdf所在的起始页面, 如"https://www.sciencedirect.com/science/article/pii/S0946672X17308763"
	 * 
	 * @param project_path:
	 *            pdffigures2项目在本地的保存路径，如"D:/pdffigures2/"
	 * 
	 * @return Figure对象, 存储statuscode/figureCaptions/figurePath
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */

	public static Figure extractByUrl_SD(String url, String project_path) throws ClientProtocolException, IOException {
		File file = new File(project_path + "/pdf");
		if (!file.exists())
			file.mkdirs();
		Figure figure = new Figure();
		// 设置从网上下载的pdf的名字及存储本地的路径,filename不包含后缀名
		String filename = url.substring(url.length() - 17, url.length());
		String pdf_path = project_path + "/pdf/" + filename + ".pdf";
		String result_path = project_path + "/result/" + filename+"/" ;
		File pdffile = new File(pdf_path);
		File resultfile = new File(result_path);
	
		if(resultfile.exists()&&resultfile.length()!=0) {
			
			figure = getFigureData(project_path, filename);
		}else if (!pdffile.exists()) {
			System.out.println(filename+": waiting...正在下载...");
			PdfDownload.savepdf(url, pdf_path);
			figure = getFigureData(project_path, filename);
		}else
			figure = getFigureData(project_path, filename);
		
		return figure;
	}



	/**
	 * extractByPath：从pdf存储路径下载pdf到本地project_path/pdf/,
	 * 再解析图表保存到本地project_path/result/filename/
	 * 
	 * @param path:
	 *            存储pdf的路径, 如"C:/Users/13838157862/Desktop/关于pdf解析的文章/pdf2table.pdf"
	 * 
	 * @param project_path:
	 *            pdffigures2项目在本地的保存路径，如"D:/pdffigures2/"
	 *            
	 * @return Figure对象, 存储statuscode、figureCaptions、figurePath
	 * @throws IOException 
	 */
	public static Figure extractByPath(String path, String project_path) throws IOException {
		
	

		File file = new File(project_path + "/pdf");
		if (!file.exists())
			file.mkdirs();
		Figure figure = new Figure();

		
		String filename = getFilename(path);// filename不包含后缀名
		String pdf_path = project_path + "pdf/" + filename +".pdf";// pdf_path是pdf文件的绝对路径
		String result_path = project_path + "/result/" + filename+"/" ;
		
		File pdffile = new File(pdf_path);
		File resultfile = new File(result_path);
	
		if(resultfile.exists()&&resultfile.length()!=0) {			
			figure = getFigureData(project_path, filename);
		}else if (!pdffile.exists()) {
			System.out.println(filename+": waiting...正在下载...");
			PdfDownload.readFile(path, pdf_path);
			figure = getFigureData(project_path, filename);
		}else
			figure = getFigureData(project_path, filename);	
		return figure;
	}

	/**
	 * extractByUrl：从pdf所在的页面下载pdf到本地project_path/pdf/,
	 * 再解析图表保存到本地project_path/result/filename/
	 * 
	 * @param url:
	 *            pdf所在的网页, 而不是对应的起始网页.
	 *            如"https://ac.els-cdn.com/S0021850200000215/1-s2.0-S0021850200000215-main.pdf
	 *            ?_tid=5c4f0ccc-5856-4479-be98-053c1fcc88a6&acdnat=1537325780_f36e6f314790bd43a71dbab56cdbd033"
	 * 
	 * @param project_path:
	 *            pdffigures2项目在本地的保存路径，如"D:/pdffigures2/"
	 * 
	 * @return Figure对象, 存储statuscode、figureCaptions、figurePath
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static Figure extractByUrl(String url, String project_path) throws ClientProtocolException, IOException {
		File file = new File(project_path + "/pdf");
		if (!file.exists())
			file.mkdirs();
		Figure figure = new Figure();
		String filename = "";
		filename = getFilename(url);
		if(filename=="") {
			filename = url.substring(url.length() - 17, url.length());
			filename = filename.replaceAll("/|\\\\|\\.", "");
		}
		String pdf_path = project_path + "pdf/" + filename + ".pdf";// 设置从网上下载的pdf的名字及存储本地的路径,filename不包含后缀名
		String result_path = project_path + "pdf/" + filename+"/";
		File pdffile = new File(pdf_path);
		File resultfile = new File(result_path);
	
		if(resultfile.exists()&&resultfile.length()!=0) {			
			figure = getFigureData(project_path, filename);
		}else if (!pdffile.exists()) {
			System.out.println(filename+": waiting...正在下载...");
			PdfDownload.pdfdown(url, pdf_path);
			figure = getFigureData(project_path, filename);
		}else
			figure = getFigureData(project_path, filename);
		
		return figure;
	}
	//从url中获取pdf的filename
	private static String getFilename(String url) {
		String result = "";
		String[] strarr = url.split("/|\\?|\\\\|;");
		for(String s : strarr) {
			if(s.contains(".pdf"))
				result = s;
		}
		return result.replace(".pdf", "");
	}
	//解析pdf，获得图像和表格
	private static Figure getFigureData(String project_path,  String filename) {
		Figure figure = new Figure();
		// 设置状态码
		int statuscode;
		Map<String, String> figuredata = FigureParser.parsefig(filename, project_path);
		if (figuredata == null || figuredata.size() == 0) {
			statuscode = 404;
		} else
			statuscode = 200;
		List<String> figureCaptions = new ArrayList<String>();
		List<String> figurePath = new ArrayList<String>();
		figureCaptions.addAll(figuredata.keySet());
		figurePath.addAll(figuredata.values());

		figure.setFigureCaptions(figureCaptions);
		figure.setFigurePath(figurePath);
		figure.setStatuscode(statuscode);
		return figure;
	}
}
