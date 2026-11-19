package com.lumafeed.dto;

import lombok.Data;

/**
 * Request body used by LumenPilot to create a confirmed operations task.
 *
 * @author qzh
 */
@Data
public class OpsTaskCreateDTO {
    private String title;
    private String description;
    private String priority;
}
