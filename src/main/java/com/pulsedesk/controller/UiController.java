package com.pulsedesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that serves the web UI.
 */
@Controller
public class UiController {

    /**
     * Serves the main dashboard page.
     *
     * @return Thymeleaf template name for the index page.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
