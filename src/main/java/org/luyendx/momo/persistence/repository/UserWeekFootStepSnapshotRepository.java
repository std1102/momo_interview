package org.luyendx.momo.persistence.repository;

import org.luyendx.momo.persistence.domain.UserWeekFootStepSnapshot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface UserWeekFootStepSnapshotRepository extends JpaRepository<UserWeekFootStepSnapshot, Long> {

    @Query("select udf from UserWeekFootStepSnapshot udf where udf.userId = :userId")
    List<UserWeekFootStepSnapshot> getByUserIdAndDate(Long userId, Pageable pageable);

    @Query("select udf from UserWeekFootStepSnapshot udf where udf.userId in :userIds and udf.startWeekDate = :date")
    List<UserWeekFootStepSnapshot> getByUserIdsAndStartWeekDate(Collection<Long> userIds, Date date);

    @Query("select udf from UserWeekFootStepSnapshot udf where udf.startWeekDate between :startDate and :endDate")
    List<UserWeekFootStepSnapshot> getUserDayFootStepByDate(Date startDate, Date endDate, Pageable pageable);

    @Query("select udf from UserWeekFootStepSnapshot udf where udf.startWeekDate = :date")
    List<UserWeekFootStepSnapshot> getByDate(Date date, Pageable pageable);

}
