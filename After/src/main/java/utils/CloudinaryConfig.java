package utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dweak2d2b",
                    "api_key",    "463683951693398",
                    "api_secret", "4wyNOWspPUUTsy6LqejmB5JFu3M"
            ));
        }
        return cloudinary;
    }
}