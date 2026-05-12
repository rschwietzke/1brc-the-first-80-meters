package org.onebrc.benchmarkviewer.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentQueryString")
    public String addQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return (queryString != null && !queryString.isEmpty()) ? "?" + queryString : "";
    }
}
