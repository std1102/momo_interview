package org.luyendx.momo.persistence.repository;

import org.luyendx.momo.persistence.domain.UserDayFootStep;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDayFootStepRepository extends JpaRepository<UserDayFootStep, Long> {

    @Query("select udf from UserDayFootStep udf where udf.userId = :userId and udf.date = :date")
    Optional<UserDayFootStep> getByUserIdAndDate(Long userId, Date date);

    @Query("select udf from UserDayFootStep udf where udf.userId = :userId")
    List<UserDayFootStep> getByUserIdAndDate(Long userId, Pageable pageable);

    @Query("select udf from UserDayFootStep udf where udf.date between :startDate and :endDate")
    List<UserDayFootStep> getUserDayFootStepByDate(Date startDate, Date endDate, Pageable pageable);

    @Query("select udf from UserDayFootStep udf where udf.date = :date")
    List<UserDayFootStep> getByDate(Date date, Pageable pageable);

}
