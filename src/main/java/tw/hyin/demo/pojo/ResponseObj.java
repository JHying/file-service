package tw.hyin.demo.pojo;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObj<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status; // success 或 error
	private List<String> errors; // 錯誤集合
	private T result; //傳送的物件

	public enum RspMsg {

		SUCCESS("成功"),
		FAILED("發生錯誤"),
		UNAUTHORIZED("驗證失敗"),
		NOT_FOUND("檔案不存在");

		@Getter
		private String msg;

		RspMsg(String msg) {
			this.msg = msg;
		}
	}
	
}
