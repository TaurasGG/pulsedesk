package com.pulsedesk.service;

import com.pulsedesk.model.TicketCategory;
import com.pulsedesk.model.TicketPriority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiTicketResponse {

    private boolean isTicket;
    private String title;
    private TicketCategory category;
    private TicketPriority priority;
    private String summary;
}
