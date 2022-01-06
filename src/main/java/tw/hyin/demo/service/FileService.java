package tw.hyin.demo.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import tw.hyin.demo.pojo.UploadFileObj;

/**
 * @author rita6 on 2021.
 */
public interface FileService {

	/**
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 上傳檔案
	 */
	public boolean upload(MultipartFile uploadfile, UploadFileObj uploadFileObj) throws Exception;

	/**
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 取得完整路徑
	 */
	public Path getFullPath(String... filePath) throws Exception;

	/**
	 * @author YingHan
	 * @since 2021-12-28
	 * 
	 * @Description 寫入上傳資料
	 */
	public void addAttachment(UploadFileObj uploadFileObj) throws Exception;

	

}
