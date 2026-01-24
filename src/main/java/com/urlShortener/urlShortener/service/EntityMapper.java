package com.urlShortener.urlShortener.service;

import com.urlShortener.urlShortener.entities.ShortUrl;
import com.urlShortener.urlShortener.entities.User;
import com.urlShortener.urlShortener.models.ShortUrlDto;
import com.urlShortener.urlShortener.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        UserDto userDto = null;
        if(shortUrl.getCreatedBy()!=null)
        {
            userDto = toUserDto(shortUrl.getCreatedBy());
        }

        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );

    }

    private UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}
