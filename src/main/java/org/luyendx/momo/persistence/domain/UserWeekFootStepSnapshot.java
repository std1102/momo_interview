package org.luyendx.momo.persistence.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWeekFootStepSnapshot {
    @Id
    private Long id;
    private Long userId;
    private Integer stepCount;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedTime;
    @Temporal(TemporalType.DATE)
    private Date startWeekDate;
}
