CREATE TABLE patient (
    pid BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 환자 id (자동 증가)
    pt_nm VARCHAR(255) NOT NULL,            -- 환자 이름
    sex_cd VARCHAR(10),                     -- 환자 성별
    age INT,                                -- 환자 나이
    dis_stat VARCHAR(255),                  -- 질병 여부
    pat_img_url VARCHAR(255),               -- 환자 사진 URL
    rmv_yn CHAR(1) DEFAULT 'N',             -- 삭제 여부 (기본값 'N')
    frst_rgdt TIMESTAMP NOT NULL,           -- 생성일자 (자동 생성)
    last_uddt TIMESTAMP,                    -- 수정일자 (자동 업데이트)
    CONSTRAINT UK_patient_pid UNIQUE (pid)  -- 환자 id를 유니크로 설정 (이것은 보통 pk로 처리됨)
);

CREATE SEQUENCE PATIENT_SEQ START WITH 1 INCREMENT BY 1;