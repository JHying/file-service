package tw.hyin.demo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import tw.hyin.demo.config.JsonDateDeseriConfig;

/**
 * @author H-yin on 2021.
 */
@Data
@Entity
@DynamicInsert//解決 not null 欄位沒給值時，不會自動塞 default 的問題
@Table(name = "UploadRecord", catalog = "TESTDB", schema = "dbo")
public class UploadRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UploadID")
    private Integer uploadId;

    @Column(name = "FilePath")
    private String filePath;

    @Column(name = "UserID")
    private String userID;

    @Column(name = "FileName")
    private String fileName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @JsonDeserialize(using = JsonDateDeseriConfig.class)
    @Column(name = "UploadDate")
    private Date uploadDate;

}
