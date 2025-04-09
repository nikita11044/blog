package practicum.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileService {
    private final MinioService minioService;
    private static final String BUCKET_NAME = "myblog-bucket";
    private static final String BASE_URL = "http://localhost:9000/" + BUCKET_NAME + "/";

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try (InputStream fileStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            minioService.uploadFile(fileName, fileStream, file.getSize(), file.getContentType());
            return BASE_URL + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file", e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        try {
            String fileName = extractFilenameFromUrl(fileUrl);
            minioService.deleteFile(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }

    private String extractFilenameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
