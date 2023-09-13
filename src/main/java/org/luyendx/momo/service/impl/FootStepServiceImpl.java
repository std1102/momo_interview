package org.luyendx.momo.service.impl;

import lombok.AllArgsConstructor;
import org.luyendx.momo.common.DateTimeUtils;
import org.luyendx.momo.common.TimeType;
import org.luyendx.momo.dto.input.PageRequestDto;
import org.luyendx.momo.dto.output.FootStepUpdateInsertDto;
import org.luyendx.momo.dto.output.UserFootStep;
import org.luyendx.momo.persistence.domain.UserDayFootStep;
import org.luyendx.momo.persistence.domain.UserMonthFootStepSnapshot;
import org.luyendx.momo.persistence.domain.UserWeekFootStepSnapshot;
import org.luyendx.momo.persistence.repository.UserDayFootStepRepository;
import org.luyendx.momo.persistence.repository.UserMonthFootStepSnapshotRepository;
import org.luyendx.momo.persistence.repository.UserWeekFootStepSnapshotRepository;
import org.luyendx.momo.service.FootStepService;
import org.luyendx.momo.service.sub.FootStepSubService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FootStepServiceImpl implements FootStepService {

    private UserDayFootStepRepository userDayFootStepRepository;

    private UserMonthFootStepSnapshotRepository userMonthFootStepSnapshotRepository;

    private UserWeekFootStepSnapshotRepository userWeekFootStepSnapshotRepository;

    private FootStepSubService footStepSubService;

    @Override
    public FootStepUpdateInsertDto updateFootStep(Long userId, Integer footStep) {
        Date currentDate = new Date();
        Optional<UserDayFootStep> optionalUserDayFootStep = userDayFootStepRepository.getByUserIdAndDate(userId, currentDate);
        UserDayFootStep userDayFootStep;
        if (optionalUserDayFootStep.isPresent()) {
            userDayFootStep = optionalUserDayFootStep.get();
            userDayFootStep.setStepCount(userDayFootStep.getStepCount() + footStep);
        } else {
            userDayFootStep = UserDayFootStep.builder()
                    .userId(userId)
                    .stepCount(footStep)
                    .date(currentDate)
                    .build();
        }
        footStepSubService.pushFootStepDataToQueue(userDayFootStep);
        return new FootStepUpdateInsertDto(currentDate);
    }

    @Override
    public List<UserFootStep> getUserFootStep(Long userId, TimeType timeType, PageRequestDto pageRequestDto) {
        switch (timeType) {
            case DAY -> {
                return getUserFootStepByDay(userId, pageRequestDto);
            }
            case WEEK -> {
                return getUserFootStepByWeek(userId, pageRequestDto);
            }
            case MONTH -> {
                return getUserFootStepByMonth(userId, pageRequestDto);
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }

    /*
    * Tạo index ở các trường date thống kê và trường ghi lại số bước chân,
    * Đối với bảng ghi theo ngày, nên tạo partition theo ngày từng, từng ngày từng ngày một
    * */
    @Override
    public List<UserFootStep> getScoreBoard(TimeType timeType, PageRequestDto pageRequestDto) {
        switch (timeType) {
            case DAY -> {
                Date currentDate = new Date();
                return userDayFootStepRepository.getByDate(currentDate, PageRequest.of(
                                pageRequestDto.getPageNumber(), pageRequestDto.getPageSize(), Sort.by(Sort.Direction.DESC, "stepCount")))
                        .stream().map(e -> UserFootStep
                                .builder()
                                .userId(e.getUserId())
                                .startDate(e.getDate())
                                .footStep(e.getStepCount())
                                .build())
                        .toList();
            }
            case WEEK -> {
                Date startDayOfWeek = DateTimeUtils.getFirstDayOfWeek();
                return userWeekFootStepSnapshotRepository.getByDate(startDayOfWeek, PageRequest.of(
                                pageRequestDto.getPageNumber(), pageRequestDto.getPageSize(), Sort.by(Sort.Direction.DESC, "stepCount")))
                        .stream().map(e -> UserFootStep
                                .builder()
                                .userId(e.getUserId())
                                .startDate(e.getStartWeekDate())
                                .footStep(e.getStepCount())
                                .build())
                        .toList();
            }
            case MONTH -> {
                Date startDayOfMonth = DateTimeUtils.getFirstDayOfWeek();
                return userMonthFootStepSnapshotRepository.getByDate(startDayOfMonth, PageRequest.of(
                                pageRequestDto.getPageNumber(), pageRequestDto.getPageSize(), Sort.by(Sort.Direction.DESC, "stepCount")))
                        .stream().map(e -> UserFootStep
                                .builder()
                                .userId(e.getUserId())
                                .startDate(e.getStartMonthdate())
                                .footStep(e.getStepCount())
                                .build())
                        .toList();
            }
            default -> {
                return Collections.emptyList();
            }
        }
    }

    private List<UserFootStep> getUserFootStepByDay(Long userId, PageRequestDto pageRequestDto) {
        List<UserDayFootStep> userDayFootSteps = userDayFootStepRepository.getByUserIdAndDate(userId,
                PageRequest.of(pageRequestDto.getPageNumber(), pageRequestDto.getPageSize())
        );
        return userDayFootSteps.stream().map(ufs -> UserFootStep.builder()
                        .userId(ufs.getUserId())
                        .footStep(ufs.getStepCount())
                        .name("dummy name")
                        .startDate(ufs.getDate())
                        .build())
                .toList();
    }

    private List<UserFootStep> getUserFootStepByWeek(Long userId, PageRequestDto pageRequestDto) {
        List<UserWeekFootStepSnapshot> userDayFootSteps = userWeekFootStepSnapshotRepository.getByUserIdAndDate(userId,
                PageRequest.of(pageRequestDto.getPageNumber(), pageRequestDto.getPageSize())
        );
        return userDayFootSteps.stream().map(ufs -> UserFootStep.builder()
                        .userId(ufs.getUserId())
                        .footStep(ufs.getStepCount())
                        .name("dummy name")
                        .startDate(ufs.getStartWeekDate())
                        .build())
                .toList();
    }

    private List<UserFootStep> getUserFootStepByMonth(Long userId, PageRequestDto pageRequestDto) {
        List<UserMonthFootStepSnapshot> userDayFootSteps = userMonthFootStepSnapshotRepository.getByUserIdAndDate(userId,
                PageRequest.of(pageRequestDto.getPageNumber(), pageRequestDto.getPageSize())
        );
        return userDayFootSteps.stream().map(ufs -> UserFootStep.builder()
                        .userId(ufs.getUserId())
                        .footStep(ufs.getStepCount())
                        .name("dummy name")
                        .startDate(ufs.getStartMonthdate())
                        .build())
                .toList();
    }

}
