package com.tericcabrel.osiris.controllers;

import com.tericcabrel.osiris.configs.FileStorageProperties;
import com.tericcabrel.osiris.models.Response;
import com.tericcabrel.osiris.services.FileStorageService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class FingerprintController {
    private FileStorageProperties fileStorageProperties;

    private FileStorageService fileStorageService;

    public FingerprintController(FileStorageService fileStorageService, FileStorageProperties fileStorageProperties) {
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
    }

    @PostMapping("/api/fingerprints")
    public Response uploadFile(
            @RequestParam("picture") MultipartFile picture,
            @RequestParam("fingerprint") MultipartFile fingerprint,
            @RequestParam("uid") String uid
    ) {
        String fileName = fileStorageService.storeFile(uid, picture, fingerprint);

        return new Response(fileName, picture.getContentType(), picture.getSize());
    }

    @RequestMapping(value = "/picture", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public void getImage(@RequestParam(name = "uid") String uid, HttpServletResponse response) throws IOException {
        ClassPathResource imgFile = new ClassPathResource("uploads/" + uid + "/" + (uid + ".png").toString());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(imgFile.getInputStream(), response.getOutputStream());
    }
}
