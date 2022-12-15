package com.example.RestProject.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@RestController
@RequestMapping("/files")
public class FileController {

    private String directory = "/Users/macbookpro/Desktop/File";


    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile)
    {
        try
        {
            Files.copy(multipartFile.getInputStream(), Paths.get(directory).resolve(multipartFile.getOriginalFilename()));
            return ResponseEntity.ok("Upload file successfully: " + multipartFile.getOriginalFilename());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Could not upload the file:" + multipartFile.getOriginalFilename());
        }
    }

    @GetMapping(value = "/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename")String filename)
    {
        Path file = Paths.get(directory).resolve(filename);
        Resource resource = null;
        try
        {
            resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable())
            {
                throw new RuntimeException("Could not read the file.");
            }
        } catch (MalformedURLException e)
        {
            throw new RuntimeException("Error:" + e.getMessage());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }


}