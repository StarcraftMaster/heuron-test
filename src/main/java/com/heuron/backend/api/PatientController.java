package com.heuron.backend.api;

import com.heuron.backend.domain.Patient;
import com.heuron.backend.dto.request.PatientRequestDto;
import com.heuron.backend.dto.response.PatientResponseDto;
import com.heuron.backend.repository.PatientRepository;
import com.heuron.backend.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;

@RequestMapping("/api/v1/")
@RestController
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @GetMapping("patients/{pid}")
    public PatientResponseDto getPatients(@PathVariable long pid){
        return new PatientResponseDto(patientService.findPatientByPid(pid));
    }

    @PostMapping(value = "patients", consumes =  {"multipart/form-data"})
    public ResponseEntity<PatientResponseDto> patient(
            @Valid @RequestPart PatientRequestDto patInfoRequest,
            @RequestPart(required = false) MultipartFile patImg) throws Exception {

        if (!isValidImage(patImg)) {
            throw new HttpMediaTypeNotSupportedException("이미지 파일은 JPEG 또는 PNG만 허용됩니다.");
        }

        Patient patient = patientService.savePatient(patInfoRequest, patImg);
        PatientResponseDto rspDto = new PatientResponseDto(patient);

        return new ResponseEntity(rspDto, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "patients/{pid}")
    public ResponseEntity patient(@PathVariable long pid) throws IOException {

        patientService.deletePatient(pid);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("patients/{pid}/pat-image/{patImg}")
    public ResponseEntity<Resource> getPatientsPatImg(@PathVariable Long pid, @PathVariable String patImg) throws MalformedURLException, FileNotFoundException {
        String type = FilenameUtils.getExtension(patImg);

        return ResponseEntity.ok()
               .contentType(MediaType.valueOf("image/"+type))
               .body(patientService.findPatientImg(pid, patImg));
    }

    // 실제 Content-Type을 image/jpeg  image/png 설정하고 데이터를 다른 형식으로 보낼경우에 대비하여 검사
    private boolean isValidImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[8]; // PNG는 8바이트까지 필요함
            int bytesRead = inputStream.read(header);

            if (bytesRead < 2) return false; // 최소한 2바이트는 읽어야 함

            // JPEG 검사 (FF D8)
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                return true;
            }

            // PNG 검사 (89 50 4E 47 0D 0A 1A 0A)
            byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            if (bytesRead >= 8 && Arrays.equals(Arrays.copyOf(header, 8), pngHeader)) {
                return true;
            }
        }
        return false;
    }
}
