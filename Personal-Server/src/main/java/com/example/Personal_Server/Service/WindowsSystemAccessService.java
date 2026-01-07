package com.example.Personal_Server.Service;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    // @Override
    // public boolean isValidPath(String path){
    //     return path.matches("^[A-Z]:\\\\.*");
    // }

    @Override
    public boolean isValidPath(String path){
        try{
            if(!path.matches("^[A-Z]:\\\\.*"))
                return false;

            Path p = Paths.get(path);
            Path root = p.getRoot();

            return root != null && Files.exists(root);
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public Resource loadFileAsResource(String path){
        try{
            Path filePath = Paths.get(path).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists() && resource.isReadable()) return resource;
        }catch(Exception e){
            throw new RuntimeException("File Not readable");
        }
        throw new RuntimeException("File not found");
    }
    
    @Override
    public void saveFile(String targetDir, MultipartFile file) throws IOException{
        Path dir = Paths.get(targetDir).normalize();
        if(!Files.exists(dir) || !Files.isDirectory(dir))
            throw new RuntimeException("Invalid Target Directory");

        Path target = dir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }
}
