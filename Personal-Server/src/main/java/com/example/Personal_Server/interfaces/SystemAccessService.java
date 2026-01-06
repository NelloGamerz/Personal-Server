package com.example.Personal_Server.interfaces;

import java.util.List;

import com.example.Personal_Server.DTO.FileItem;

public interface SystemAccessService {
    List<FileItem> listRoots();
    List<FileItem> listDirectory(String path);
    boolean isValidPath(String path);
}
