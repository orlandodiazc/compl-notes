package com.ditod.notes.utils;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class ImageUtils {
    private final HttpHeaders responseHeaders;

    public ImageUtils() {
        this.responseHeaders = new HttpHeaders();
        this.responseHeaders.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
    }

    public HttpHeaders getImageResponseHeaders(UUID imageId, String contentType, int contentLength) {
        responseHeaders.setContentType(MediaType.parseMediaType(contentType));
        responseHeaders.setContentLength(contentLength);
        responseHeaders.setContentDisposition(
                ContentDisposition.builder("inline").filename(imageId.toString()).build());
        return responseHeaders;
    }
}
