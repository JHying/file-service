package tw.hyin.demo.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.hyin.demo.config.JsonDateDeseriConfig;
import tw.hyin.demo.pojo.SysEnum.Msg_Key;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageObj implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("訊息辨別代碼")
	private Msg_Key msgKey;

	@JsonIgnore
	private String ipAddress;

	@ApiModelProperty("訊息內容(json)")
	private String jsonMsg; // 傳送的物件

	@ApiModelProperty("傳送時間")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonDeserialize(using = JsonDateDeseriConfig.class)
	private Date updateDate;

}
