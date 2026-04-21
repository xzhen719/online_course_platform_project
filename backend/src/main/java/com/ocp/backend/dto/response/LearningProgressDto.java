package com.ocp.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressDto {
    private boolean isCourseCompleted;
    private Integer courseProgressPercent;
    // 課程首次完成後發放的優惠券代碼
    private String couponCode;
}
