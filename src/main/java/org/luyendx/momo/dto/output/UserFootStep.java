package org.luyendx.momo.dto.output;

import lombok.*;
import org.luyendx.momo.common.TimeType;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserFootStep {

    private Long userId;
    private String name;
    private Integer footStep;
    private Date startDate;
    private TimeType statisticType;

}
