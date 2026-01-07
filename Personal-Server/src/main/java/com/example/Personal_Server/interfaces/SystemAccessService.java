package com.example.Personal_Server.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.example.Personal_Server.DTO.FileItem;

public interface SystemAccessService {
    List<FileItem> listRoots();
    List<FileItem> listDirectory(String path);
    boolean isValidPath(String path);
    Resource loadFileAsResource(String path);
    void saveFile(String targetDir, MultipartFile file) throws IOException;
}
