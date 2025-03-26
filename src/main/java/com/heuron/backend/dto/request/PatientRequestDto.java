package com.heuron.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRequestDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상, 10자 이하여야 합니다.")
    String patientName;

    @Min(value = 0, message = "나이는 0세 이상 이어야 합니다")
    int age;

    @Pattern(regexp = "^[MF]$", message = "값은 'M' 또는 'F'만 입력 가능합니다.")
    String sexCd;

    @Pattern(regexp = "^[YN]$", message = "값은 'Y' 또는 'N'만 입력 가능합니다.")
    String diseaseStatus;
}
