package com.example.Personal_Server.Service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import com.example.Personal_Server.DTO.FileItem;

class WindowsSystemAccessServiceTest {

    private WindowsSystemAccessService service;
    private File tempDir;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        service = new WindowsSystemAccessService();

        // Create temporary directory
        tempDir = Files.createTempDirectory("test-dir").toFile();

        // Create temporary file inside directory
        tempFile = new File(tempDir, "test.txt");
        Files.writeString(tempFile.toPath(), "test-content");

        System.out.println("=== Setup Complete ===");
        System.out.println("Temp Directory: " + tempDir.getAbsolutePath());
        System.out.println("Temp File: " + tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) tempFile.delete();
        if (tempDir != null && tempDir.exists()) tempDir.delete();
        System.out.println("=== TearDown Complete ===\n");
    }

    // ---------------------- listRoots ----------------------

    @Test
    void listRoots_shouldReturnSystemRoots() {
        List<FileItem> roots = service.listRoots();
        System.out.println("listRoots result:");
        roots.forEach(f -> System.out.println("  " + f.name() + " | Directory: " + f.isDirectory() + " | Size: " + f.size() + " | Path: " + f.path()));

        assertNotNull(roots);
        assertFalse(roots.isEmpty());
    }

    // ---------------------- listDirectory ----------------------

    @Test
    void listDirectory_validDirectory_shouldReturnFilesAndFolders() {
        List<FileItem> items = service.listDirectory(tempDir.getAbsolutePath());
        System.out.println("listDirectory result for: " + tempDir.getAbsolutePath());
        items.forEach(f -> System.out.println("  " + f.name() + " | Directory: " + f.isDirectory() + " | Size: " + f.size() + " | Path: " + f.path()));

        assertEquals(1, items.size());
    }

    @Test
    void listDirectory_invalidPath_shouldThrowException() {
        System.out.println("Testing listDirectory with invalid path...");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.listDirectory("Z:\\non-existing-path"));
        System.out.println("Caught expected exception: " + ex.getMessage());
    }

    @Test
    void listDirectory_fileInsteadOfDirectory_shouldThrowException() {
        System.out.println("Testing listDirectory with a file path...");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.listDirectory(tempFile.getAbsolutePath()));
        System.out.println("Caught expected exception: " + ex.getMessage());
    }

    // ---------------------- isValidPath ----------------------

    // @Test
    // void isValidPath_validWindowsPath_shouldReturnTrue() {
    //     String[] paths = {"C:\\Users\\Public", "D:\\", "E:\\folder\\file.txt"};
    //     for (String path : paths) {
    //         boolean valid = service.isValidPath(path);
    //         System.out.println("isValidPath('" + path + "') = " + valid);
    //         assertTrue(valid);
    //     }
    // }

    @Test
    void isValidPath_validWindowsPath_shouldReturnTrue() {
        File[] roots = File.listRoots();
        for (File root : roots) {
            boolean valid = service.isValidPath(root.getAbsolutePath());
            System.out.println("isValidPath('" + root + "') = " + valid);
            assertTrue(valid);
        }
    }

    @Test
    void isValidPath_invalidWindowsPath_shouldReturnFalse() {
        String[] paths = {"/home/user", "C:/Users", "relative/path", "c:\\lowercase-drive"};
        for (String path : paths) {
            boolean valid = service.isValidPath(path);
            System.out.println("isValidPath('" + path + "') = " + valid);
            assertFalse(valid);
        }
    }

    // -------------------- loadFileAsResource --------------------

    @Test
    void loadFileAsResource_validFile_shouldReturnResource() {
        Resource resource = service.loadFileAsResource(tempFile.toString());

        System.out.println("loadFileAsResource -> " + resource.getFilename());

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void loadFileAsResource_nonExistingFile_shouldThrowException() {
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.loadFileAsResource("Z:\\non-existing-file.txt")
        );

        System.out.println("Expected exception: " + ex.getMessage());
        assertEquals("File not found", ex.getMessage());
    }

    // -------------------- saveFile --------------------

    @Test
    void saveFile_validTarget_shouldSaveFile() throws IOException {
        MockMultipartFile multipartFile =
                new MockMultipartFile(
                        "file",
                        "upload.txt",
                        "text/plain",
                        "hello-world".getBytes()
                );

        service.saveFile(tempDir.toString(), multipartFile);

        Path savedFile = tempDir.toPath().resolve("upload.txt");

        System.out.println("saveFile -> Saved at: " + savedFile);

        assertTrue(Files.exists(savedFile));
        assertEquals("hello-world", Files.readString(savedFile));
    }

    @Test
    void saveFile_invalidTargetDirectory_shouldThrowException() {
        MockMultipartFile multipartFile =
                new MockMultipartFile(
                        "file",
                        "upload.txt",
                        "text/plain",
                        "data".getBytes()
                );

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.saveFile("Z:\\invalid-dir", multipartFile)
        );

        System.out.println("Expected exception: " + ex.getMessage());
        assertEquals("Invalid Target Directory", ex.getMessage());
    }
}
