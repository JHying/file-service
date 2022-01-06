package tw.hyin.demo.utils;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author H-yin on 2020.
 */
@Data
public class FileUtil {

    private Path filePath;
    private String fileName;
    private File fileDir;
    private File file;

    public FileUtil(Path filePath) {
        this.filePath = filePath;
    }

    public FileUtil(Path filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public FileUtil(String filePath) {
        this.fileDir = new File(filePath).getParentFile();
        this.file = new File(filePath);
    }

    public FileUtil(String fileDir, String fileName) {
        this.fileDir = new File(fileDir);
        this.file = new File(this.fileDir + "/" + fileName);
    }

    public static byte[] readFile(String filePath) throws Exception {
        return Files.readAllBytes(new File(filePath).toPath());
    }

    /**
     * 寫入檔案
     */
    public static void writeFile(String savePath, byte[] bytes) throws IOException {
        File file = new File(savePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), bytes);
    }

    public static String getMyDocPath() {
        //Get My Documents Path
        JFileChooser fr = new JFileChooser();
        FileSystemView fw = fr.getFileSystemView();
        return fw.getDefaultDirectory().getAbsolutePath();
    }

    public boolean saveFile(MultipartFile file) throws Exception {

        if (!file.isEmpty()) {
            //路徑不存在則建立
            if (!this.filePath.toFile().exists()) {
                this.filePath.toFile().mkdirs();
            }
            byte[] bytes = file.getBytes();

            if(this.fileName == null){
                this.filePath = Paths.get(this.filePath.toFile().getAbsolutePath(), file.getOriginalFilename());
            }else{
                this.filePath = Paths.get(this.filePath.toFile().getAbsolutePath(), this.fileName);
            }
            Files.write(this.filePath, bytes);
        }

        return ((this.filePath.toFile().exists()) && (this.filePath.toFile().isFile()));
    }

    public boolean deleteFile() {
        if (this.file.exists() && this.file.isFile()) {
            return this.file.delete();
        } else {
            return false;
        }
    }

    public File[] getAllFile() {
        if (this.fileDir.exists() && this.fileDir.isDirectory()) {
            return this.file.listFiles();
        } else {
            return null;
        }
    }

    public String getType() throws Exception {
        return Files.probeContentType(this.file.toPath());
    }

    public Long getSize(SizeUnit unit) throws Exception {
        if (this.file.exists()) {
            switch (unit) {
                case B:
                    return Files.size(this.file.toPath());
                case KB:
                    return Files.size(this.file.toPath()) / 1024;
                case MB:
                    return Files.size(this.file.toPath()) / 1024 / 1024;
                case GB:
                    return Files.size(this.file.toPath()) / 1024 / 1024 / 1024;
                case TB:
                    return Files.size(this.file.toPath()) / 1024 / 1024 / 1024 / 1024;
                default:
                    return Files.size(this.file.toPath());
            }
        } else {
            return 0L;
        }
    }

    public enum SizeUnit {

        B("bytes"), KB("kilobytes"), MB("megabytes"), GB("gigabytes"), TB("terabytes");

        @Getter
        private String unitDesc;

        SizeUnit(String unitDesc) {
            this.unitDesc = unitDesc;
        }
    }
}
