package com.aidiary.user.domain.entity;

import com.aidiary.common.enums.DiarySentenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_analysis_sentences")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DailyAnalysisSentencesEntity extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UsersEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_id")
  private DiariesEntity diary;

  @Enumerated(EnumType.STRING)
  private DiarySentenceType type;
  private String content;

}
