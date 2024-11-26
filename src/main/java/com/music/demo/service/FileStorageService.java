package com.music.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path coverStorageLocation;
    private final Path audioStorageLocation;

    public FileStorageService(@Value("${file.cover-dir}") String coverDir,
                              @Value("${file.audio-dir}") String audioDir)
    {
        this.coverStorageLocation = Paths.get(coverDir).toAbsolutePath().normalize();
        this.audioStorageLocation = Paths.get(audioDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.coverStorageLocation);
            Files.createDirectories(this.audioStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create directories for file storage.", ex);
        }
    }

    public String storeCover(MultipartFile file) {
        return storeFile(file, coverStorageLocation);
    }

    public String storeAudio(MultipartFile file) {
        return storeFile(file, audioStorageLocation);
    }

    private String storeFile(MultipartFile file, Path storageLocation) {
        // 获取文件的原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成唯一的文件名
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        // 保存文件
        try {
            Path targetLocation = storageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), ex);
        }
    }

    /**
     * 删除封面文件
     */
    public boolean deleteCover(String filename) {
        return deleteFile(coverStorageLocation, filename);
    }

    /**
     * 删除音频文件
     */
    public boolean deleteAudio(String filename) {
        return deleteFile(audioStorageLocation, filename);
    }

    /**
     * 通用文件删除方法
     */
    private boolean deleteFile(Path storageLocation, String filename) {
        try {
            Path filePath = storageLocation.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + filename, ex);
        }
    }
}
