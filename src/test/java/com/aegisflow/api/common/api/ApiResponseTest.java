package com.aegisflow.api.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void successResponseCarriesDataAndTimestamp() {
        ApiResponse<String> response = ApiResponse.success("created", "ok");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("created");
        assertThat(response.data()).isEqualTo("ok");
        assertThat(response.error()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }
}
