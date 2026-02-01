package com.pulsedesk.service;

import com.pulsedesk.model.TicketCategory;
import com.pulsedesk.model.TicketPriority;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiTicketResponse {

    @JsonProperty("isTicket")
    private boolean isTicket;
    private String title;
    private TicketCategory category;
    private TicketPriority priority;
    private String summary;
}
