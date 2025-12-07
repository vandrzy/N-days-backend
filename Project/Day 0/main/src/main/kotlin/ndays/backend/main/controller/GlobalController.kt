package ndays.backend.main.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class GlobalController {    
    @GetMapping("/api/public/hello")
    fun publicApi(): String{
        return "Hello in public"
    }

    @GetMapping("/api/secret/hello")
    fun secretApi(): String{
        return "Hello in secret"
    }
}