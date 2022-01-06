/**
 * 
 */
package tw.hyin.demo.pojo;

import lombok.Getter;

/**
 * @author YingHan 2022-01-06
 *
 */
public class SysEnum {

	// 訊息辨別
	public enum Msg_Key {

		test("t", "測試"),
		alert("a", "警告");

		@Getter
		private String value;

		@Getter
		private String desc;

		Msg_Key(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}
	}

}
