package com.heuron.backend.dto.response;

import com.heuron.backend.domain.Patient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientResponseDto {
    Long patientId;             // 환자의 pid
    String patientName;         // 환자이름
    String sexCd;               // 환자 성별 M/F
    int age;                    // 환자 나이
    String diseaseStatus;       // 질병유무  Y/N
    String patImgUrl;           // 이미지 경로


    public PatientResponseDto(Patient patient){
        this.patientId = patient.getPid();
        this.patientName = patient.getPtNm();
        this.sexCd = patient.getSexCd();
        this.age = patient.getAge();
        this.diseaseStatus = patient.getDisStat();
        this.patImgUrl = String.format("/api/v1/patients/%s/pat-image/%s",patient.getPid(), patient.getPatImgUrl());

    }
}
