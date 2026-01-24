package com.urlShortener.urlShortener.service;

import com.urlShortener.urlShortener.ApplicationProperties;
import com.urlShortener.urlShortener.entities.ShortUrl;
import com.urlShortener.urlShortener.models.CreateShortUrlCmd;
import com.urlShortener.urlShortener.models.PagedResult;
import com.urlShortener.urlShortener.models.ShortUrlDto;
import com.urlShortener.urlShortener.repositories.ShortUrlRepository;
import com.urlShortener.urlShortener.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private final UserRepository userRepository;
    private ShortUrlRepository shortUrlRepository;
    private EntityMapper entityMapper;
    private final ApplicationProperties applicationProperties;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, EntityMapper entityMapper, ApplicationProperties applicationProperties, UserRepository userRepository) {
        this.entityMapper = entityMapper;
        this.shortUrlRepository = shortUrlRepository;
        this.applicationProperties = applicationProperties;
        this.userRepository = userRepository;
    }

    public PagedResult<ShortUrlDto> getAllPublicShortUrls(int pageNo, int pageSize) {

        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findAllPublicUrls(pageable).map(entityMapper::toShortUrlDto);

        return PagedResult.from(shortUrlDtoPage);
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd createShortUrlCmd) {
        if(applicationProperties.validateUrl())
        {
            boolean isValid = UrlValidator.isUrlExists(createShortUrlCmd.originalUrl());
            if(!isValid)
            {
                throw new IllegalArgumentException("The provided URL is not valid or reachable: " + createShortUrlCmd.originalUrl());
            }
        }
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setOriginalUrl(createShortUrlCmd.originalUrl());
        shortUrl.setClickCount(0L);
        if(createShortUrlCmd.userId()==null)
        {
            shortUrl.setIsPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(applicationProperties.defaultExpiryInDays(), java.time.temporal.ChronoUnit.DAYS));
        }
        else {
            shortUrl.setCreatedBy(userRepository.findById(createShortUrlCmd.userId()).orElseThrow());
            shortUrl.setIsPrivate(createShortUrlCmd.isPrivate()!=null ? createShortUrlCmd.isPrivate() : false);
            shortUrl.setExpiresAt(createShortUrlCmd.expirationInDays()!=null ? Instant.now().plus(createShortUrlCmd.expirationInDays(), java.time.temporal.ChronoUnit.DAYS) : Instant.now().plus(applicationProperties.defaultExpiryInDays(), java.time.temporal.ChronoUnit.DAYS));

        }

        shortUrl.setShortKey(generateUniqueShortKey());

        shortUrlRepository.save(shortUrl);

        return entityMapper.toShortUrlDto(shortUrl);
    }

    public String generateShortKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortKey = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * characters.length());
            shortKey.append(characters.charAt(index));
        }
        return shortKey.toString();
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    @Transactional
    public Optional<ShortUrlDto> getOriginalUrlByShortKey(String shortKey, Long userId) {
        Optional<ShortUrl> shortUrlOpt = shortUrlRepository.findByShortKey(shortKey);
        if(shortUrlOpt.isEmpty())
        {
            return Optional.empty();
        }
        ShortUrl shortUrl = shortUrlOpt.get();
        if(shortUrl.getIsPrivate()!=null && shortUrl.getIsPrivate()
        && (shortUrl.getCreatedBy()!=null && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)))
        {
            return Optional.empty();
        }
        if(shortUrlOpt.isPresent())
        {
            shortUrl.setClickCount(shortUrl.getClickCount() + 1);
            shortUrlRepository.save(shortUrl);
        }
        return shortUrlOpt.map(entityMapper::toShortUrlDto);
    }

    @Transactional
    public PagedResult<ShortUrlDto> getUserShortUrls(Long userId, int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findByCreatedById(userId,pageable).map(entityMapper::toShortUrlDto);

        return PagedResult.from(shortUrlDtoPage);
    }

    private Pageable getPageable(int pageNo, int pageSize) {
        pageNo = pageNo>1 ? pageNo-1:0;
        return PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));
    }

    @Transactional
    public void deleteShortUrls(List<Long> shortUrlIds, Long userId) {
        if(shortUrlIds!=null&&!shortUrlIds.isEmpty()&&userId!=null)
        {
            shortUrlRepository.deleteByIdInAndCreatedById(shortUrlIds,userId);
        }
    }

    public PagedResult<ShortUrlDto> getAllShortUrls(int pageNo, int pageSize)
    {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);

        return PagedResult.from(shortUrlDtoPage);
    }
}
