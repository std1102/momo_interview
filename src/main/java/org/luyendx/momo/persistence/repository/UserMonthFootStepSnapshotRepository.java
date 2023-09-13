package org.luyendx.momo.persistence.repository;

import org.luyendx.momo.persistence.domain.UserMonthFootStepSnapshot;
import org.luyendx.momo.persistence.domain.UserWeekFootStepSnapshot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface UserMonthFootStepSnapshotRepository extends JpaRepository<UserMonthFootStepSnapshot, Long> {

    @Query("select udf from UserMonthFootStepSnapshot udf where udf.userId = :userId")
    List<UserMonthFootStepSnapshot> getByUserIdAndDate(Long userId, Pageable pageable);

    @Query("select udf from UserMonthFootStepSnapshot udf where udf.startMonthdate = :date")
    List<UserMonthFootStepSnapshot> getByDate(Date date, Pageable pageable);

    @Query("select udf from UserMonthFootStepSnapshot udf where udf.userId in :userIds and udf.startMonthdate = :date")
    List<UserMonthFootStepSnapshot> getByUserIdsAndStartWeekDate(Collection<Long> userIds, Date date);

}

