package com.aidiary.diary.service.event;

import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.entity.UsersEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DailyDiaryCreateEvent extends ApplicationEvent {

    private final DiariesEntity diary;
    private final UsersEntity user;
    private final String content;

    public DailyDiaryCreateEvent(Object source, DiariesEntity diary, UsersEntity user, String content) {
        super(source);
        this.diary = diary;
        this.user = user;
        this.content = content;
    }

}
