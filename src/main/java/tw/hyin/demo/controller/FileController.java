package tw.hyin.demo.controller;

import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import tw.hyin.demo.pojo.ResponseObj;
import tw.hyin.demo.pojo.UploadFileObj;
import tw.hyin.demo.pojo.ResponseObj.RspMsg;
import tw.hyin.demo.service.FileService;

/**
 * API 接口
 * 
 * @author YingHan 2021
 *
 */
@RestController
@ResponseBody
public class FileController extends BaseController {

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@ApiOperation(value = "檔案上傳")
	@PostMapping(value = "/upload")
	public ResponseEntity<ResponseObj<RspMsg>> fileUpload(@RequestParam("file") MultipartFile file,
			@ModelAttribute UploadFileObj uploadFileObj)
	{
		try {
			if (fileService.upload(file, uploadFileObj)) {
				fileService.addAttachment(uploadFileObj);
				return super.sendSuccessRsp(RspMsg.SUCCESS);
			} else {
				return super.sendFailRsp(new Exception("file saved failed."));
			}
		} catch (Exception e) {
			return super.sendFailRsp(e);
		}
	}

	@ApiOperation(value = "檔案下載")
	@GetMapping(value = "/download/**")
	public ResponseEntity<?> fileDownload(HttpServletRequest request) {
		try {
			String requestURL = request.getRequestURL().toString();
			String filePath = URLDecoder.decode(requestURL.split("f/download/")[1], "UTF-8");
			// 獲取檔案
			Path fullPath = fileService.getFullPath(filePath);
			File file = fullPath.toFile();
			if (!file.exists()) {
				return super.sendBadRequestRsp(RspMsg.NOT_FOUND);
			}
			// 回傳檔案串流
			return super.downloadFile(file);
		} catch (Exception e) {
			return super.sendFailRsp(e);
		}
	}

	@ApiOperation(value = "檔案新增浮水印後下載", notes = "浮水印包在 header (name=watermark)")
	@GetMapping(value = "/download/watermark/**")
	public ResponseEntity<?> fileDownloadWithWatermark(HttpServletRequest request) {
		try {
			String requestURL = request.getRequestURL().toString();
			String filePath = URLDecoder.decode(requestURL.split("/download/watermark/")[1], "UTF-8");
			// 獲取檔案
			Path fullPath = fileService.getFullPath(filePath);
			File file = fullPath.toFile();
			if (!file.exists()) {
				return super.sendBadRequestRsp(RspMsg.NOT_FOUND);
			}
			// 新增浮水印回傳
			return super.addWaterMark(file, true, request.getHeader("watermark"));
		} catch (Exception e) {
			return super.sendFailRsp(e);
		}
	}

	@ApiOperation(value = "報表製作")
	@PostMapping(value = "/report/{templateName}", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> getReport(@PathVariable(value = "templateName") String templateName,
			@RequestBody T object)
	{
		try {
			return super.createPDF(object, templateName + ".docx", true, null);
		} catch (Exception e) {
			return super.sendFailRsp(e);
		}
	}

}
