package com.urlShortener.urlShortener.controller;

import com.urlShortener.urlShortener.ApplicationProperties;
import com.urlShortener.urlShortener.models.PagedResult;
import com.urlShortener.urlShortener.models.ShortUrlDto;
import com.urlShortener.urlShortener.service.ShortUrlService;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ApplicationProperties applicationProperties;
    private ShortUrlService shortUrlService;

    public AdminController(ShortUrlService shortUrlService, ApplicationProperties applicationProperties) {
        this.shortUrlService = shortUrlService;
        this.applicationProperties = applicationProperties;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(name="pageNo", defaultValue = "1") Integer pageNo, Model model) {
        PagedResult<ShortUrlDto> allShortUrls = shortUrlService.getAllShortUrls(pageNo, applicationProperties.pageSize());
        model.addAttribute("shortUrls", allShortUrls);
        model.addAttribute("baseUrl", applicationProperties.baseUrl());
        model.addAttribute("paginationUrl","/admin/dashboard");
        return "admin-dashboard";
    }
}
