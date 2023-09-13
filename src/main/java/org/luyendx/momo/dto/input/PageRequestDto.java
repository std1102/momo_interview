package org.luyendx.momo.dto.input;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageRequestDto {
    private int pageSize;
    private int pageNumber;
}
