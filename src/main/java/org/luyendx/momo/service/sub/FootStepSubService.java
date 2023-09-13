package org.luyendx.momo.service.sub;

import org.apache.commons.lang3.time.DateUtils;
import org.luyendx.momo.common.DateTimeUtils;
import org.luyendx.momo.persistence.domain.UserDayFootStep;
import org.luyendx.momo.persistence.domain.UserMonthFootStepSnapshot;
import org.luyendx.momo.persistence.domain.UserWeekFootStepSnapshot;
import org.luyendx.momo.persistence.repository.UserDayFootStepRepository;
import org.luyendx.momo.persistence.repository.UserMonthFootStepSnapshotRepository;
import org.luyendx.momo.persistence.repository.UserWeekFootStepSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/*
 * Class này dùng để chia lượng data lớn thành các thành phần nhỏ hơn
 * Điều này giúp tăng performace cũng như giảm lượng memory cần sử dụng
 * Trong điều kiện thực tế, ta có thể implement bằng cách sử dụng một hệ thống quêu / cache tập trung và
 * Có cơ chế thích hợp để xử lý việc hoạt động trông môi trường bất đồng bộ của nhiều service
 * */

@Service
public class FootStepSubService {

    private static final Integer MAX_BATCH_SIZE = 1000;

    private static final Queue<UserDayFootStep> waitingQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    private UserDayFootStepRepository userDayFootStepRepository;

    @Autowired
    private UserWeekFootStepSnapshotRepository userWeekFootStepSnapshotRepository;

    @Autowired
    private UserMonthFootStepSnapshotRepository userMonthFootStepSnapshotRepository;

    public void pushFootStepDataToQueue(UserDayFootStep userFootStep) {
        waitingQueue.add(userFootStep);
    }

    /*
     * Để tăng tính real time cho các job thống kê có thể tăng tần suất chạy của job, và quản lý việc đồng bộ dữ liệu
     * khi thao tác với database, có thể sử dụng cơ chế mutual exlusive để theo dõi lock cx như unlock với record
     * */

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void insertJob() {
        List<UserDayFootStep> dataFromQueue = new LinkedList<>();
        for (int i = 0; i < MAX_BATCH_SIZE; i++) {
            UserDayFootStep userFootStep = waitingQueue.poll();
            if (userFootStep == null) {
                break;
            }
            dataFromQueue.add(userFootStep);
        }
        userDayFootStepRepository.saveAll(dataFromQueue);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * 0")
    public void statisticFootStepPerWeek() {
        Date startDate = DateTimeUtils.getFirstDayOfWeek();
        Date endTime = DateUtils.addDays(startDate, 7);
        List<UserDayFootStep> userDayFootSteps;
        int pageNumber = 1;
        do {
            Pageable pageable = PageRequest.of(pageNumber, MAX_BATCH_SIZE);
            userDayFootSteps = userDayFootStepRepository.getUserDayFootStepByDate(startDate, endTime, pageable);
            Map<Long, Integer> userStepCountMap = userDayFootSteps.stream().collect(
                    Collectors.toMap(UserDayFootStep::getUserId, UserDayFootStep::getStepCount, Integer::sum));
            List<UserWeekFootStepSnapshot> userWeekFootStepSnapshots = userWeekFootStepSnapshotRepository.getByUserIdsAndStartWeekDate(
                    userStepCountMap.keySet(),
                    startDate
            );
            if (userWeekFootStepSnapshots.isEmpty()) {
                userWeekFootStepSnapshotRepository.saveAll(userStepCountMap.entrySet().stream().map(
                        e -> UserWeekFootStepSnapshot.builder()
                                .stepCount(e.getValue())
                                .userId(e.getKey())
                                .startWeekDate(startDate)
                                .build()
                ).toList());
            } else {
                List<UserWeekFootStepSnapshot> preInsertData = userStepCountMap.entrySet().stream().map(
                        e -> {
                            UserWeekFootStepSnapshot temp = userWeekFootStepSnapshots.stream().filter(snapshotE -> snapshotE.getUserId().equals(e.getKey())).findFirst().orElse(null);
                            if (temp == null) {
                                return UserWeekFootStepSnapshot.builder()
                                        .stepCount(e.getValue())
                                        .userId(e.getKey())
                                        .startWeekDate(startDate)
                                        .build();
                            } else {
                                temp.setStepCount(temp.getStepCount() + e.getValue());
                                return temp;
                            }
                        }
                ).toList();
                userWeekFootStepSnapshotRepository.saveAll(preInsertData);
            }
            pageNumber++;
        } while (userDayFootSteps.size() < MAX_BATCH_SIZE || userDayFootSteps.isEmpty());
    }


    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")
    public void statisticFootStepPerMonth() {
        Date startDate = DateTimeUtils.getFirstDayOfWeek();
        Date endTime = DateUtils.addDays(startDate, 7);
        List<UserWeekFootStepSnapshot> userWeekFootSteps;
        int pageNumber = 1;
        do {
            Pageable pageable = PageRequest.of(pageNumber, MAX_BATCH_SIZE);
            userWeekFootSteps = userWeekFootStepSnapshotRepository.getUserDayFootStepByDate(startDate, endTime, pageable);
            Map<Long, Integer> userStepCountMap = userWeekFootSteps.stream().collect(
                    Collectors.toMap(UserWeekFootStepSnapshot::getUserId, UserWeekFootStepSnapshot::getStepCount, Integer::sum));
            List<UserMonthFootStepSnapshot> userMonthFootStepSnapshots = userMonthFootStepSnapshotRepository.getByUserIdsAndStartWeekDate(
                    userStepCountMap.keySet(),
                    startDate
            );
            if (userMonthFootStepSnapshots.isEmpty()) {
                userMonthFootStepSnapshotRepository.saveAll(userStepCountMap.entrySet().stream().map(
                        e -> UserMonthFootStepSnapshot.builder()
                                .stepCount(e.getValue())
                                .userId(e.getKey())
                                .startMonthdate(startDate)
                                .build()
                ).toList());
            } else {
                List<UserMonthFootStepSnapshot> preInsertData = userStepCountMap.entrySet().stream().map(
                        e -> {
                            UserMonthFootStepSnapshot temp = userMonthFootStepSnapshots.stream().filter(snapshotE -> snapshotE.getUserId().equals(e.getKey())).findFirst().orElse(null);
                            if (temp == null) {
                                return UserMonthFootStepSnapshot.builder()
                                        .stepCount(e.getValue())
                                        .userId(e.getKey())
                                        .startMonthdate(startDate)
                                        .build();
                            } else {
                                temp.setStepCount(temp.getStepCount() + e.getValue());
                                return temp;
                            }
                        }
                ).toList();
                userMonthFootStepSnapshotRepository.saveAll(preInsertData);
            }
            pageNumber++;
        } while (userWeekFootSteps.size() < MAX_BATCH_SIZE || userWeekFootSteps.isEmpty());
    }

}
