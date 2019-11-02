package com.tericcabrel.osiris.services;

import com.tericcabrel.osiris.configs.FileStorageProperties;
import com.tericcabrel.osiris.exceptions.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private Path fileStorageLocation;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(String uid, MultipartFile fingerprint, MultipartFile picture) {
        this.fileStorageLocation = Paths.get(this.fileStorageLocation.toAbsolutePath().toString() + "\\" + uid)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory for uid: " + uid, e);
        }

        // Normalize file name
        String fingerprintName = StringUtils.cleanPath(fingerprint.getOriginalFilename());
        String pictureName = StringUtils.cleanPath(picture.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fingerprintName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fingerprintName);
            }
            if (pictureName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + pictureName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocationFingerprint = this.fileStorageLocation.resolve(fingerprintName);
            Path targetLocationPicture = this.fileStorageLocation.resolve(pictureName);

            // this.fileStorageLocation.toAbsolutePath().
            Files.copy(fingerprint.getInputStream(), targetLocationFingerprint, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(picture.getInputStream(), targetLocationPicture, StandardCopyOption.REPLACE_EXISTING);

            return fingerprintName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fingerprintName + ". Please try again!", ex);
        }
    }
}
