package part2.ndbckend.learn.configuration.cloudinary

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cloudinary")
class CloudinaryProperties {
    lateinit var cloudName: String
    lateinit var apiKey: String
    lateinit var apiSecret: String
}
