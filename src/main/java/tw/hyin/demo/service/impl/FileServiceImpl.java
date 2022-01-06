package tw.hyin.demo.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tw.hyin.demo.dao.UploadRecordDao;
import tw.hyin.demo.entity.UploadRecord;
import tw.hyin.demo.pojo.UploadFileObj;
import tw.hyin.demo.service.FileService;
import tw.hyin.demo.utils.FileUtil;
import tw.hyin.demo.utils.Log;
import tw.hyin.demo.utils.PojoUtil;

/**
 * 
 * @author YingHan 2021-11-02
 *
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

	@Value("${server.upload.rootpath}")
	private String rootPath;

	private final UploadRecordDao uploadRecordDao;

	@Autowired
	public FileServiceImpl(UploadRecordDao uploadRecordDao) {
		this.uploadRecordDao = uploadRecordDao;
	}

	@Override
	public boolean upload(MultipartFile uploadfile, UploadFileObj uploadFileObj) throws Exception {
		FileUtil fileUtil;
		Path fullPath = Paths.get(this.rootPath, uploadFileObj.getFilePath());

		if (uploadFileObj.getFileName() != null) {
			fileUtil = new FileUtil(fullPath, uploadFileObj.getFileName());
		} else {
			fileUtil = new FileUtil(fullPath);
		}

		return fileUtil.saveFile(uploadfile);
	}

	@Override
	public Path getFullPath(String... filePath) throws Exception {
		return Paths.get(this.rootPath, filePath);
	}

	@Override
	public void addAttachment(UploadFileObj uploadFileObj) throws Exception {
		// 新增 UploadRecord
		FileUtil fileUtil = new FileUtil(this.rootPath + "/" + uploadFileObj.getFilePath());
		if (fileUtil.getFile().exists()) {
			UploadRecord uploadRecord = PojoUtil.convertPojo(uploadFileObj, UploadRecord.class);
			uploadRecord.setFilePath(uploadFileObj.getFilePath() + "/" + uploadFileObj.getFileName());
			uploadRecord.setUserID(uploadFileObj.getUserId());
			uploadRecord.setUploadDate(new Date());
			uploadRecordDao.saveBean(uploadRecord);
			Log.info("insert into uploadRecord: " + this.rootPath + "/" + uploadRecord.getFilePath());
		} else {
			throw new Exception("File not exist! Fail to insert upload data.");
		}
	}

}
