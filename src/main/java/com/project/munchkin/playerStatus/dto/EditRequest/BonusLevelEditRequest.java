package com.project.munchkin.playerStatus.dto.EditRequest;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonusLevelEditRequest {
    Long playerStatusId;
    Long newValue;
}
