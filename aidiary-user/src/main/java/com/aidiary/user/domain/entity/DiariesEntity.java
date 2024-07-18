package com.aidiary.user.domain.entity;

import com.aidiary.common.enums.DiaryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity(name = "diaries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DiariesEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity user;
    private String content;
    private LocalDate entryDate;
    @Enumerated(EnumType.STRING)
    private DiaryStatus status;

    public void updateStatus(DiaryStatus status) {
        this.status = status;
    }
}
