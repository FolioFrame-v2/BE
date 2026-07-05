package com.folioframe.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageSendReqDTO(
        @NotBlank String content
) {}
