package com.ditod.notes.web.auth.dto;

import com.ditod.notes.domain.verification.VerifyType;

public record VerifyRequestParams(String code, VerifyType type, String target) {}
