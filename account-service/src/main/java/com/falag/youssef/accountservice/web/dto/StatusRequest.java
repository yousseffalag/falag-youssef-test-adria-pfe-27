package com.falag.youssef.accountservice.web.dto;

import com.falag.youssef.accountservice.domain.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull AccountStatus status) {
}
