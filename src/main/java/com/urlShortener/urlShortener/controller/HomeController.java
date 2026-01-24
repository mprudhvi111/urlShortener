package com.urlShortener.urlShortener.controller;

import com.urlShortener.urlShortener.ApplicationProperties;
import com.urlShortener.urlShortener.dtos.CreateShortUrlForm;
import com.urlShortener.urlShortener.entities.ShortUrl;
import com.urlShortener.urlShortener.entities.User;
import com.urlShortener.urlShortener.exceptions.ShortUrlExpiredException;
import com.urlShortener.urlShortener.exceptions.ShortUrlNotFoundException;
import com.urlShortener.urlShortener.models.CreateShortUrlCmd;
import com.urlShortener.urlShortener.models.PagedResult;
import com.urlShortener.urlShortener.models.ShortUrlDto;
import com.urlShortener.urlShortener.service.SecurityUtils;
import com.urlShortener.urlShortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private ShortUrlService shortUrlService;
    private final ApplicationProperties applicationProperties;
    private SecurityUtils securityUtils;

    public HomeController(ShortUrlService shortUrlService, ApplicationProperties applicationProperties, SecurityUtils securityUtils) {
        this.shortUrlService = shortUrlService;
        this.applicationProperties = applicationProperties;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/")
    public String home(@RequestParam(name= "pageNo", defaultValue="1") Integer pageNo,
                       Model model) {

        User user = securityUtils.getUser();

        this.addShortUrlDataToModel(model, pageNo);
        model.addAttribute("title", "URL Shortener Application");
        model.addAttribute("paginationUrl","/");
        model.addAttribute("createShortUrlForm", new CreateShortUrlForm("",false, null));
        return "index";
    }

    @PostMapping("/short-url")
    public String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm createShortUrlForm,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model)
    {
        if(bindingResult.hasErrors())
        {
            this.addShortUrlDataToModel(model, 1);
            model.addAttribute("title", "URL Shortener Application");
            return "index";
        }
        try {
            Long userId = securityUtils.getUserId();

            CreateShortUrlCmd createShortUrlCmd = new CreateShortUrlCmd(createShortUrlForm.originalUrl(), createShortUrlForm.isPrivate(), createShortUrlForm.expirationInDays(), userId);
            shortUrlService.createShortUrl(createShortUrlCmd);
            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully for: " + createShortUrlForm.originalUrl());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create Short URL for: " + createShortUrlForm.originalUrl());
        }
        return "redirect:/";
    }

    private void addShortUrlDataToModel(Model model, int pageNo)
    {
        PagedResult<ShortUrlDto> shortUrlList = shortUrlService.getAllPublicShortUrls(pageNo, applicationProperties.pageSize());
        model.addAttribute("baseUrl", applicationProperties.baseUrl());
        model.addAttribute("shortUrls", shortUrlList);
    }

    @GetMapping("/s/{shortKey}")
    public String redirectToOriginalUrl(@PathVariable String shortKey)
    {
        Long userId = securityUtils.getUserId();
        Optional<ShortUrlDto> shortUrlDto = shortUrlService.getOriginalUrlByShortKey(shortKey,userId);
        if(shortUrlDto.isEmpty())
        {
            throw new ShortUrlNotFoundException("Short URL not found for key: " + shortKey);
        }
        if(shortUrlDto.isPresent() && shortUrlDto.get().expiresAt().isBefore(java.time.Instant.now()))
        {
            throw new ShortUrlExpiredException("Short URL has expired for key: " + shortKey);
        }
        return "redirect:" + shortUrlDto.get().originalUrl();
    }

    @GetMapping("/my-urls")
    public String showMyUrls(@RequestParam(name = "pageNo", defaultValue="1") Integer pageNo,
                             Model model)
    {
        Long userId = securityUtils.getUserId();
        PagedResult<ShortUrlDto> myShortUrlsList = shortUrlService.getUserShortUrls(userId,pageNo, applicationProperties.pageSize());
        model.addAttribute("baseUrl", applicationProperties.baseUrl());
        model.addAttribute("shortUrls", myShortUrlsList);
        model.addAttribute("paginationUrl","/my-urls");
        return "my-urls";
    }

    @PostMapping("/delete-urls")
    public String deleteSelectedUrls(@RequestParam("ids") List<Long> selectedUrlIds,
                                     RedirectAttributes redirectAttributes)
    {
        if(selectedUrlIds==null || selectedUrlIds.isEmpty())
        {
            redirectAttributes.addFlashAttribute("errorMessage", "No URLs selected for deletion.");
            return "redirect:/my-urls";
        }
        try {
            Long userId = securityUtils.getUserId();
            shortUrlService.deleteShortUrls(selectedUrlIds, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Selected URLs deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete selected URLs.");
        }
        return "redirect:/my-urls";
    }

    @GetMapping("/login")
    public String loginPage() {
        return  "login";
    }
}
