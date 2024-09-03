package com.aidiary.core.entity;

import com.aidiary.common.enums.DiaryWordType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_analysis_words")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DailyAnalysisWordsEntity extends BaseEntity {

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
  private DiaryWordType type;

  private String text;

  private Integer scale;

}
