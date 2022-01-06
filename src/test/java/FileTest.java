import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import tw.hyin.demo.pojo.TestReport;
import tw.hyin.demo.utils.JsonUtil;
import tw.hyin.demo.utils.xDocReport.XDocReportUtil;

/**
 * @author H-yin on 2021.
 */
public class FileTest {

	@SuppressWarnings("unchecked")
	@Test
	public void pdfTest() {
		try {
			TestReport object = TestReport.builder().title("測試標題").language("測試語言").type("測試類型").build();
			Map<String, Object> map = (Map<String, Object>) JsonUtil.objToMap(object);
			XDocReportUtil xDocReportBuild = new XDocReportUtil("src/main/resources/template/test.docx", map);
			xDocReportBuild.build("temp/result", "test.pdf", ConverterTypeTo.PDF, null);
			// 浮水印
			xDocReportBuild.addWatermark("temp/result/test.pdf", "temp/result/test_wm.pdf", "testWatermark");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
