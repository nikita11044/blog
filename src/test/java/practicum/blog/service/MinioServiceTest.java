package practicum.blog.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    private String fileName;
    private InputStream inputStream;
    private long size;
    private String contentType;

    @BeforeEach
    void setUp() {
        fileName = "test-file.jpg";
        inputStream = mock(InputStream.class);
        size = 1024L;
        contentType = "image/jpeg";
    }

    @Test
    void uploadFile_ShouldDelegateToMinioClient() throws Exception {
        minioService.uploadFile(fileName, inputStream, size, contentType);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void deleteFile_ShouldDelegateToMinioClient() throws Exception {
        minioService.deleteFile(fileName);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
}
