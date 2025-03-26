package com.heuron.backend.domain;

import com.heuron.backend.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_seq_generator")
    @SequenceGenerator(name = "patient_seq_generator", sequenceName = "PATIENT_SEQ", allocationSize = 1)
    @Column
    private Long pid;           // 환자 id

    @Column
    private String ptNm;        // 환자 이름

    @Column
    private String sexCd;       // 환자 성별

    @Column
    private Integer Age;        // 환자 나이

    @Column
    private String disStat;     // 질병여부

    @Column
    private String patImgUrl;   // 환자 사진

    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String rmvYn;       // 삭제 여부(기본값 N)

    @PrePersist
    public void prePersist() {
        if (this.rmvYn == null) {
            this.rmvYn = "N";
        }
    }

}
