package com.aidiary.user.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public abstract class DiaryRequestBundle {

    public record DiariesOfMonthGetRequest(
          Integer year,
          Integer month,
          Integer date
    ){

    }

    public record DiaryCreateRequest(
         @JsonFormat(pattern = "yyyy-MM-dd")
         LocalDate entryDate,
         String content
    ){}

    public record DiaryUpdateRequest(
            String content
    ){}

}
