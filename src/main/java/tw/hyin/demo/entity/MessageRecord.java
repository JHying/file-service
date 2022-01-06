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

import lombok.Builder;
import lombok.Data;
import tw.hyin.demo.config.JsonDateDeseriConfig;

/**
 * 
 * @author YingHan 2022-01-04
 *
 */
@Data
@Builder
@Entity
@DynamicInsert // 解決 not null 欄位沒給值時，不會自動塞 default 的問題
@Table(name = "MessageRecord", catalog = "TESTDB", schema = "dbo")
public class MessageRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MessageID")
	private Integer MessageId;

	@Column(name = "IpAddress")
	private String ipAddress;

	@Column(name = "Message")
	private String message;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@JsonDeserialize(using = JsonDateDeseriConfig.class)
	@Column(name = "UpdateDate")
	private Date updateDate;

}
