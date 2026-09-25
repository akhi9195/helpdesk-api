package com.portfolio.helpdesk.ticket;

import com.portfolio.helpdesk.common.config.MapStructConfig;
import com.portfolio.helpdesk.ticket.dto.TicketResponse;
import com.portfolio.helpdesk.user.UserMapper;
import org.mapstruct.Mapper;

/**
 * Entity -> response only. Requests become entities through the Ticket constructor,
 * which enforces BR-1, so there is deliberately no toEntity() here.
 */
@Mapper(config = MapStructConfig.class, uses = UserMapper.class)
interface TicketMapper {

    TicketResponse toResponse(Ticket ticket);
}