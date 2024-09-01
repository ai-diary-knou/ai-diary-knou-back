package com.aidiary.auth.dto;

import lombok.Builder;

@Builder
public record JwtTokenInfo(Long userId, String email, String nickname){}
