package com.urlShortener.urlShortener.repositories;

import com.urlShortener.urlShortener.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl,Long> {

    public List<ShortUrl> findByIsPrivateIsFalseOrderByCreatedAtDesc();

    //@Query("select su from ShortUrl su where su.isPrivate = false order by su.createdAt desc")
    //@EntityGraph( attributePaths = {"createdBy"})
    //@Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false order by su.createdAt desc")
    @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false")
    public Page<ShortUrl> findAllPublicUrls(Pageable pageable);

    boolean existsByShortKey(String shortKey);

    Optional<ShortUrl> findByShortKey(String shortKey);

    Page<ShortUrl> findByCreatedById(Long userId, Pageable pageable);

    Long id(Long id);

    @Modifying
    void deleteByIdInAndCreatedById(List<Long> ids, Long userId);

    @Query("select su from ShortUrl su left join fetch su.createdBy")
    Page<ShortUrl> findAllShortUrls(Pageable pageable);
}
