package services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import utils.CloudinaryConfig;

import java.io.File;
import java.util.Map;

public class CloudinaryService {

    private final Cloudinary cloudinary = CloudinaryConfig.getInstance();

    // ===== UPLOAD fichier =====
    public String uploadFile(File file) {
        try {
            Map result = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                    "folder", "after-travel/documents",
                    "resource_type", "auto" // supporte PDF, image, etc.
            ));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== SUPPRIMER fichier =====
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}