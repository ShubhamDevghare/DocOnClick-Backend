package com.lakshit.doconclick.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryFileUploadService {

    private final Cloudinary cloudinary;

    public CloudinaryFileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Generic upload method that handles both images and documents
    public String uploadFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            
            // Check if it's an image
            if (contentType != null && contentType.startsWith("image/")) {
                return uploadImage(file);
            } else {
                // For PDFs, text files, and other documents
                return uploadDocument(file);
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    // Image upload (existing method)
    public String uploadImage(MultipartFile file) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", "doconclick/users",
                "public_id", "user_" + UUID.randomUUID()
                // default resource_type is image
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    // Document upload (renamed and improved from uploadPdf)
    public String uploadDocument(MultipartFile file) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", "doconclick/reports",
                "public_id", "report_" + UUID.randomUUID(),
                "resource_type", "raw" // This is crucial for non-image files
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Document upload failed: " + e.getMessage(), e);
        }
    }

    // Helper method to get file extension
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    // Image delete (existing method)
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            String[] parts = imageUrl.split("/");
            String publicIdWithExtension = parts[parts.length - 1];
            String publicId = publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf('.'));

            String folder = "doconclick/users";
            String fullPublicId = folder + "/" + publicId;

            cloudinary.uploader().destroy(fullPublicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete old image from Cloudinary", e);
        }
    }

    // Document delete method
    public void deleteDocument(String documentUrl) {
        if (documentUrl == null || documentUrl.isBlank()) return;

        try {
            String[] parts = documentUrl.split("/");
            String publicIdWithExtension = parts[parts.length - 1];
            
            String folder = "doconclick/reports";
            String fullPublicId = folder + "/" + publicIdWithExtension;

            Map<String, Object> options = ObjectUtils.asMap("resource_type", "raw");
            cloudinary.uploader().destroy(fullPublicId, options);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete document from Cloudinary", e);
        }
    }

    // Generic delete method
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        
        try {
            // Determine if it's an image or document based on folder structure
            if (fileUrl.contains("/doconclick/users/")) {
                deleteImage(fileUrl);
            } else if (fileUrl.contains("/doconclick/reports/")) {
                deleteDocument(fileUrl);
            } else {
                // Fallback - try as document first, then as image
                try {
                    deleteDocument(fileUrl);
                } catch (Exception e) {
                    deleteImage(fileUrl);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}
