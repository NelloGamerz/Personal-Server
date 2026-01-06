package com.example.Personal_Server.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Personal_Server.DTO.FileItem;
import com.example.Personal_Server.interfaces.SystemAccessService;

@Service
public class WindowsSystemAccessService implements SystemAccessService {

    @Override
    public List<FileItem> listRoots(){
        return Arrays.stream(File.listRoots())
                .map(root -> new FileItem(
                    root.getPath(),
                    root.getPath(),
                    true,
                    0
                )).toList();
    }

    @Override
    public List<FileItem> listDirectory(String path){
        File dir = new File(path);
        if(!dir.exists() || !dir.isDirectory()){
            throw new RuntimeException("Invalid Path");
        }

        return Arrays.stream(dir.listFiles())
                .map(f -> new FileItem(
                    f.getName(),
                    f.getAbsolutePath(),
                    f.isDirectory(),
                    f.isDirectory() ? 0 : f.length()
                )).toList();
    }

    @Override
    public boolean isValidPath(String path){
        return path.matches("^[A-Z]:\\\\.*");
    }
    
}
