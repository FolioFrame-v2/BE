package com.folioframe.domain.chat.dto.request;

import com.folioframe.domain.chat.enums.ChatOriginType;
import jakarta.validation.constraints.NotNull;

public record ChatRoomCreateReqDTO(
        @NotNull ChatOriginType originType,
        @NotNull Long originId
) {}
