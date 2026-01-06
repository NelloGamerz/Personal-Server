package com.example.Personal_Server.Service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void isValidPath_validWindowsPath_shouldReturnTrue() {
        String[] paths = {"C:\\Users\\Public", "D:\\", "E:\\folder\\file.txt"};
        for (String path : paths) {
            boolean valid = service.isValidPath(path);
            System.out.println("isValidPath('" + path + "') = " + valid);
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
}
