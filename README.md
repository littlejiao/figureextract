# figureextract
提供了三个方法：
extractByUrl_SD(String url, String project_path) : 从sciencedirect数据库下载pdf到本地project_path/pdf/,
再解析图表保存到本地project_path/result/filename/	url: pdf所在的起始页面, 如"https://www.sciencedirect.com/science/article/pii/S0946672X17308763"
project_path: pdffigure项目在本地的位置	Figure对象（包含状态码、图表描述信息、图表在本地的位置）；

extractByPath(String path, String project_path)	: 从pdf存储路径下载pdf到本地project_path/pdf/,
再解析图表保存到本地project_path/result/filename/	Path: 存储pdf的路径, 如"C:/Users/13838157862/Desktop/关于pdf解析的文章/pdf2table.pdf"
project_path: pdffigure项目在本地的位置	Figure对象（包含状态码、图表描述信息、图表在本地的位置）；

extractByUrl(String url, String project_path)	: 从pdf所在的页面下载pdf到本地project_path/pdf/,
再解析图表保存到本地project_path/result/filename/	url: pdf所在的网页，如https://arxiv.org/pdf/1809.10020.pdf
project_path: pdffigure项目在本地的位置	Figure对象（包含状态码、图表描述信息、图表在本地的位置）
