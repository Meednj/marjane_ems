package com.marjane.ems.DTO;

import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    String content,
    LocalDateTime createdAt,
    Long ticketId,
    Long authorId
) {}
