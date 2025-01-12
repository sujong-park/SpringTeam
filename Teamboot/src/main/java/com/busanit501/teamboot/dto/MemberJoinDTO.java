package com.busanit501.teamboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinDTO {
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;
}
