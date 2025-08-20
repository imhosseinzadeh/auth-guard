package com.imho.authguard.dto.response;

public record JsonResponse<D>(boolean success, String message, D data) {
}
