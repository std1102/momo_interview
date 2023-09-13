package org.luyendx.momo.endpoint;

import org.luyendx.momo.common.TimeType;
import org.luyendx.momo.dto.input.PageRequestDto;
import org.luyendx.momo.dto.output.FootStepUpdateInsertDto;
import org.luyendx.momo.dto.output.UserFootStep;
import org.luyendx.momo.service.FootStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping(value = "/api/footstep")
public class FootStepEndpoint {

    @Autowired
    private FootStepService footStepService;

    @PutMapping(value = "/{footStep}")
    public FootStepUpdateInsertDto updateFootStep(@PathVariable Integer footStep) {
        return footStepService.updateFootStep(new SecureRandom().nextLong(), footStep);
    }

    @GetMapping(value = "/user")
    public List<UserFootStep> getUserFootStep(@RequestParam Long userId, @RequestParam TimeType timeType, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
        return footStepService.getUserFootStep(1L, timeType, new PageRequestDto(pageSize, pageNumber));
    }

    @GetMapping(value = "/scoreboard")
    public List<UserFootStep> getScoreBoard(@RequestParam TimeType timeType,@RequestParam Integer pageSize,@RequestParam Integer pageNumber) {
        return footStepService.getScoreBoard(timeType, new PageRequestDto(pageSize, pageNumber));
    }

}
