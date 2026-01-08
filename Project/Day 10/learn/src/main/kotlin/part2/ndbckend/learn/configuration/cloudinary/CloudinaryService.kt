package part2.ndbckend.learn.configuration.cloudinary

import com.cloudinary.Cloudinary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class CloudinaryService (
    private val cloudinary: Cloudinary
) {

    fun uploadImage(
        file: MultipartFile,
        folder: String = "default"
    ): CloudinaryUploadResponse{
        val result = cloudinary.uploader().upload(
            file.bytes,
            mapOf(
                "folder" to folder,
                "resource_type" to "image"
            )
        )

        return CloudinaryUploadResponse(
            publicId = result["public_id"] as String,
            secureUrl = result["secure_url"] as String
        )
    }

    fun deleteImage(
        publicId: String
    ){
        cloudinary.uploader().destroy(
            publicId,
            mapOf(
                "resource_type" to "image"
            )
        )
    }
}