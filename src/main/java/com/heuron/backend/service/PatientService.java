package com.heuron.backend.service;

import com.heuron.backend.domain.Patient;
import com.heuron.backend.dto.request.PatientRequestDto;
import com.heuron.backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final String IMG_DIR =  System.getProperty("user.dir") + File.separator + "images";
    private final String IMG_DEL_DIR =  System.getProperty("user.dir") + File.separator + "images_delete";

    public Patient findPatientByPid(Long pid){
        // 삭제할 대상이 있는지 검사
        return patientRepository.findByPidAndRmvYn(pid,"N")
                .orElseThrow(() ->  new EntityNotFoundException("회원이 존재하지 않습니다."));
    }

    public Resource findPatientImg(long pid, String imgUrl) throws MalformedURLException, FileNotFoundException {
        String fileDir = IMG_DIR + File.separator + pid + File.separator + imgUrl;

        Path imagePath = Paths.get(fileDir).normalize();
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new FileNotFoundException("이미지를 찾을 수 없습니다: " + imgUrl);
        }
    }

    @Transactional
    public Patient savePatient(PatientRequestDto patReqDto, MultipartFile imgFile) throws IOException {
        // 환자 정보 저장
        Patient patient = new Patient();
        patient.setPtNm(patReqDto.getPatientName());
        patient.setAge(patReqDto.getAge());
        patient.setSexCd(patReqDto.getSexCd());
        patient.setDisStat(patReqDto.getDiseaseStatus());
        patientRepository.save(patient);

        String imageDir = IMG_DIR +  File.separator + patient.getPid();

        patient.setPatImgUrl(saveImageFile(imgFile, imageDir));
        patientRepository.save(patient);

        return patient;
    }

    @Transactional
    public void deletePatient(Long pid) throws IOException {
        // 삭제할 대상이 있는지 검사
        Patient patient = patientRepository.findById(pid)
                .orElseThrow(() ->  new EntityNotFoundException("회원이 존재하지 않습니다."));

        // 이미지 삭제
        deleteFolder(IMG_DIR + File.separator + pid + File.separator + patient.getPatImgUrl(),
                IMG_DEL_DIR + File.separator + pid + File.separator + patient.getPatImgUrl()) ;

        // 회원정보 삭제(실제 삭제가 아닌 변경)
        patient.setRmvYn("Y");

        patientRepository.save(patient);
    }

    private String saveImageFile(MultipartFile imgFile, String imageDir) throws IOException {
        // 파일 확장자 추출
        String originalFilename = imgFile.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        // 저장할 파일 경로
        String image = "patient" + fileExtension;
        String fileDir = imageDir + File.separator + image;
        Path path = Paths.get(fileDir);

        // 디렉토리 존재 여부 확인 후 없으면 생성
        File directory = new File(imageDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 저장
        Files.copy(imgFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return image;
    }

    private void deleteFolder(String imageDir, String desDirPath) throws IOException {
        File srcDir = new File(imageDir);
        File desDir = new File(desDirPath);

        if(srcDir.exists()){
            FileUtils.moveFile(srcDir,desDir);
        }
    }
}
