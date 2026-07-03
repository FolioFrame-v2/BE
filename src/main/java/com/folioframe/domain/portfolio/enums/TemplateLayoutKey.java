package com.folioframe.domain.portfolio.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 프론트엔드 TemplateMeta.vibe("minimal" | "editorial" | "terminal" | "playful")와 1:1 대응.
// FE와 그대로 맞물리도록 JSON 직렬화/역직렬화 값은 소문자로 노출한다.
public enum TemplateLayoutKey {
    MINIMAL, EDITORIAL, TERMINAL, PLAYFUL;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static TemplateLayoutKey fromJson(String value) {
        return TemplateLayoutKey.valueOf(value.toUpperCase());
    }
}
