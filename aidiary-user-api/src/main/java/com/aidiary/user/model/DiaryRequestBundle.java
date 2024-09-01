package com.aidiary.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Objects;

public abstract class DiaryRequestBundle {

    public record DiariesOfMonthGetRequest(
          Integer year,
          Integer month,
          Integer date
    ){

        public DiariesOfMonthGetRequest {
            LocalDate currentDate = LocalDate.now();
            year = Objects.nonNull(year) ? year : currentDate.getYear();
            month = Objects.nonNull(month) ? month : currentDate.getMonthValue();
            date = Objects.nonNull(date) ? date : currentDate.getDayOfMonth();
        }

        public LocalDate getSelectedDate(){
            return LocalDate.of(year, month, date);
        }
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
