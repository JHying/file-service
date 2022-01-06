/**
 * 
 */
package tw.hyin.demo.utils.xDocReport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.google.common.io.Files;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import lombok.NoArgsConstructor;

/**
 * @author YingHan 2021-12-28
 *
 */
@NoArgsConstructor
public class XDocReportUtil extends XDocReport {

    public XDocReportUtil(String template_fullpath, Map<String, Object> paramMap) {
        setTemplatePath(template_fullpath);
        setParamMap(paramMap);
    }

    @Override
    public ByteArrayOutputStream addWatermark(String inputFilePath, String outputFilePath, String watermark) throws Exception {
        FileInputStream inputStream = new FileInputStream(inputFilePath);
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        this.setWatermark(inputStream, outputStream, watermark);//新增浮水印落地至outputFilePath
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byteOut.write(Files.toByteArray(new File(outputFilePath)));//讀取outputFilePath
        inputStream.close();
        outputStream.close();
        byteOut.close();
        return byteOut;
    }

    public ByteArrayOutputStream download(String outputFileName, ConverterTypeTo typeto, boolean deleteTemp, String watermark) throws Exception {
        String rootPath = "temp/";
        ByteArrayOutputStream outputStream = build(rootPath, outputFileName, typeto, watermark);
        //取得串流後刪除暫存
        if (deleteTemp) {
            new File(rootPath + outputFileName).delete();
        }
        return outputStream;
    }

    /**
     * Add text water mark
     */
    private void setWatermark(InputStream inputStream, OutputStream outputStream, String watermark)
            throws Exception {

        int repeat = 3;
        int fontSize = 40;
        float opacity = 0.5f;

        Document document = new Document(PageSize.A4);
        //Read the existing PDF document
        PdfReader pdfReader = new PdfReader(inputStream);
        //Get the PdfStamper object
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);

        //Get the PdfContentByte type by pdfStamper.
        for (int i = 1, pdfPageSize = pdfReader.getNumberOfPages() + 1; i < pdfPageSize; i++) {
            PdfContentByte pageContent = pdfStamper.getOverContent(i);
            pageContent.setGState(this.getPdfGState(opacity));
            pageContent.beginText();
            pageContent.setFontAndSize(this.getBaseFont(), fontSize);
            pageContent.setColorFill(new BaseColor(220, 220, 220));
            //            pageContent.showTextAligned(Element.ALIGN_CENTER, watermark, document.getPageSize().getWidth() / 2,
            //                    document.getPageSize().getHeight() / 2, 30);
            for (int x = 0; x <= repeat; x++) {
                for (int y = 0; y <= repeat; y++) {
                    pageContent.showTextAligned(Element.ALIGN_CENTER, watermark,
                            document.getPageSize().getWidth() / repeat * x, document.getPageSize().getHeight() / repeat * y,
                            45);
                }
            }
            pageContent.endText();
        }
        pdfStamper.close();
    }

    /**
     * Get BaseFont
     */
    private BaseFont getBaseFont() throws Exception {
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
    }

    /**
     * Get PdfGState
     */
    private PdfGState getPdfGState(float opacity) {
        PdfGState graphicState = new PdfGState();
        graphicState.setFillOpacity(opacity);
        graphicState.setStrokeOpacity(1f);
        return graphicState;
    }
}
