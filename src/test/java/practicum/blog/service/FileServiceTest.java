package practicum.blog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private MinioService minioService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileService fileService;

    @Test
    void uploadFile_ShouldReturnUrl_WhenFileIsValid() throws Exception {
        String fileName = "image.png";
        String contentType = "image/png";
        long size = 2048L;
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getSize()).thenReturn(size);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        String result = fileService.uploadFile(multipartFile);

        assertNotNull(result);
        assertTrue(result.endsWith(fileName));
        verify(minioService).uploadFile(eq(fileName), any(InputStream.class), eq(size), eq(contentType));
    }

    @Test
    void uploadFile_ShouldReturnNull_WhenFileIsNull() {
        String result = fileService.uploadFile(null);
        assertNull(result);
        verifyNoInteractions(minioService);
    }

    @Test
    void uploadFile_ShouldReturnNull_WhenFileIsEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);

        String result = fileService.uploadFile(multipartFile);
        assertNull(result);
        verifyNoInteractions(minioService);
    }

    @Test
    void uploadFile_ShouldThrowException_WhenInputStreamFails() throws Exception {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException("IO error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileService.uploadFile(multipartFile));

        assertTrue(exception.getMessage().contains("Error uploading file"));
        verifyNoInteractions(minioService);
    }

    @Test
    void deleteFile_ShouldCallMinioService_WhenUrlIsValid() throws Exception {
        String url = "http://localhost:9000/myblog-bucket/test.png";

        fileService.deleteFile(url);

        verify(minioService).deleteFile("test.png");
    }

    @Test
    void deleteFile_ShouldDoNothing_WhenUrlIsNull() {
        fileService.deleteFile(null);
        verifyNoInteractions(minioService);
    }

    @Test
    void deleteFile_ShouldDoNothing_WhenUrlIsEmpty() {
        fileService.deleteFile("");
        verifyNoInteractions(minioService);
    }

    @Test
    void deleteFile_ShouldThrowException_WhenMinioFails() throws Exception {
        String url = "http://localhost:9000/myblog-bucket/broken.png";

        doThrow(new RuntimeException("Deletion failed"))
                .when(minioService).deleteFile("broken.png");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(url));

        assertTrue(exception.getMessage().contains("Error deleting file"));
    }
}
