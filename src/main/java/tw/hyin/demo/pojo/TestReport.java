/**
 * 
 */
package tw.hyin.demo.pojo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @author YingHan 2022-01-05
 *
 */
@Data
@Builder
public class TestReport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String language;
	private String type;

}
