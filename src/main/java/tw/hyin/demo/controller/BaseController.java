package tw.hyin.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.common.io.Files;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import tw.hyin.demo.pojo.ResponseObj;
import tw.hyin.demo.utils.JsonUtil;
import tw.hyin.demo.utils.xDocReport.XDocReportUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseController {

	@Value("xdoc.template.rootpath")
	private String template_rootpath;

	protected <T> ResponseEntity<ResponseObj<T>> sendSuccessRsp(T result) {
		return new ResponseEntity(ResponseObj.builder().status(HttpStatus.OK)
				.result(result).build(), HttpStatus.OK);
	}

	protected <T> ResponseEntity<ResponseObj<T>> sendFailRsp(Exception e) {
		e.printStackTrace();
		List<String> errors = new ArrayList<>();
		errors.add("Internal Server Error");
		errors.add(e.getMessage());
		return new ResponseEntity(ResponseObj.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
				.errors(errors).build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected <T> ResponseEntity<ResponseObj<T>> sendBadRequestRsp(T result) {
		List<String> errors = new ArrayList<>();
		errors.add(result.toString());
		return new ResponseEntity(ResponseObj.builder().status(HttpStatus.BAD_REQUEST)
				.errors(errors).build(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * 
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 下載檔案
	 */
	protected <T> ResponseEntity<?> downloadFile(File originFile) throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byteOut.write(Files.toByteArray(originFile));
		byteOut.close();
		// 包在 resource 裡才能 return 給前台，否則會 406
		ByteArrayResource resource = new ByteArrayResource(byteOut.toByteArray());
		return this.setResponse(resource, originFile.getName());
	}

	/**
	 * 
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 檔案+浮水印後下載
	 */
	protected <T> ResponseEntity<?> addWaterMark(File originFile, boolean deleteTemp, String watermark)
			throws Exception
	{
		XDocReportUtil xDocReportUtil = new XDocReportUtil();
		String outputPath = originFile.getAbsolutePath().split("\\.")[0] + "_watermark.pdf";
		String outputName = originFile.getName().split("\\.")[0] + "_watermark.pdf";
		ByteArrayOutputStream os = xDocReportUtil.addWatermark(originFile.getAbsolutePath(), outputPath, watermark);
		// 包在 resource 裡才能 return 給前台，否則會 406
		ByteArrayResource resource = new ByteArrayResource(os.toByteArray());
		// 取得串流後刪除暫存
		if (deleteTemp) {
			new File(outputPath).delete();
		}
		return this.setResponse(resource, outputName);
	}

	/**
	 * 
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 製作報表 (paramObj=變數物件)
	 */
	protected <T> ResponseEntity<?> createPDF(T paramObj, String templateName, boolean deleteTemp, String watermark)
			throws Exception
	{
		Map<String, Object> map = (Map<String, Object>) JsonUtil.objToMap(paramObj);
		XDocReportUtil xDocReportUtil = new XDocReportUtil(template_rootpath + "/" + templateName, map);
		String fileName = templateName.split("\\.")[0] + ".pdf";
		ByteArrayResource resource = new ByteArrayResource(
				xDocReportUtil.download(fileName, ConverterTypeTo.PDF, deleteTemp, watermark).toByteArray()
		);
		return this.setResponse(resource, fileName);
	}

	/**
	 * 
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 統一的 response 方法
	 */
	private <T> ResponseEntity<?> setResponse(ByteArrayResource resource, String fileName) throws Exception {
		String mimeType = URLConnection.guessContentTypeFromName(fileName);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"))
				.contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType(mimeType))
				.body(resource);
	}

	/**
	 * 
	 * @author YingHan
	 * @since 2022-01-06
	 * 
	 * @Description 取得客戶端真實 IP 位址
	 */
	protected String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {// 若為本機傳送，則根據網卡取本機配置的IP
			InetAddress inet = null;
			try {
				inet = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			ip = inet.getHostAddress();
		}
		if (ip != null && ip.length() > 15) {// 對於通過多重代理的情況，第一個IP為客戶端真實IP,多個IP按照','分割
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

}
