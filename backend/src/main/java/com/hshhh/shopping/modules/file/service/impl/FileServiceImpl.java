package com.hshhh.shopping.modules.file.service.impl;

import com.hshhh.shopping.modules.file.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads/}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        File dest = new File(uploadDir + File.separator + newFilename);
        if (!dest.isAbsolute()) {
            dest = new File(System.getProperty("user.dir"), uploadDir + File.separator + newFilename);
        }

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            // Return relative path instead of absolute URL for better portability
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}
