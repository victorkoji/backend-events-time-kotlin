package io.eventstime.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class HomeController {
    @GetMapping("/swagger-ui")
    fun redirectToDocs(): RedirectView {
        val redirectView = RedirectView()
        redirectView.url = "/swagger-ui.html"
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY)
        return redirectView
    }

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello"
    }
}
