package com.falag.youssef.web.dto;

import com.falag.youssef.domain.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull AccountStatus status) {
}
