package com.alexeykovzel.fi.features;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class ModelHandler {
    private final Model model;
    
    public ModelHandler(Model model) {
        this.model = model;
    }

    public ModelAndView getErrorPage(String status, String error, String message) {
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return new ModelAndView("error");
    }
}
