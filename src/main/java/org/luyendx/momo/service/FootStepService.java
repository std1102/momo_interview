package org.luyendx.momo.service;

import org.luyendx.momo.common.TimeType;
import org.luyendx.momo.dto.input.PageRequestDto;
import org.luyendx.momo.dto.output.FootStepUpdateInsertDto;
import org.luyendx.momo.dto.output.UserFootStep;

import java.util.List;

public interface FootStepService {
    FootStepUpdateInsertDto updateFootStep(Long userId, Integer footStep);

    List<UserFootStep> getUserFootStep(Long userId, TimeType timeType, PageRequestDto pageRequestDto);

    List<UserFootStep> getScoreBoard(TimeType timeType, PageRequestDto pageRequestDto);
}
