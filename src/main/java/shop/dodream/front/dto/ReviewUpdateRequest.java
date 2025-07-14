package shop.dodream.front.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Data
public class ReviewUpdateRequest {
    private Long reviewId;
    private Integer rating;
    private String content;
    private Set<String> originalImageSet = Set.of();
    private Set<String> deletedImageSet = Set.of();
    private Set<String> images;

    public ReviewUpdateRequest computeImages() {
        Set<String> result = new HashSet<>(originalImageSet);
        result.removeAll(deletedImageSet);
        this.images = result;
        return this;
    }

    public void setOriginalImages(String originalImages) {
        this.originalImageSet = parseImageString(originalImages);
    }

    public void setDeletedImages(String deletedImages) {
        this.deletedImageSet = parseImageString(deletedImages);
    }

    private Set<String> parseImageString(String input) {
        if (input == null || input.isBlank()) return Collections.emptySet();
        return Arrays.stream(input.split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
