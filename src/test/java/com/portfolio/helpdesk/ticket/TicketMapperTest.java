package com.portfolio.helpdesk.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.helpdesk.ticket.dto.TicketResponse;
import com.portfolio.helpdesk.user.User;
import com.portfolio.helpdesk.user.UserMapperImpl;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TicketMapperTest {

    private final TicketMapper mapper = new TicketMapperImpl(new UserMapperImpl());

    @Test
    void mapsNewTicketWithCreatorSummaryAndNoAssignee() {
        User creator = new User("Akhilesh B", "akhilesh@example.com", "not-a-real-hash");
        ReflectionTestUtils.setField(creator, "id", 7L);

        Ticket ticket = new Ticket("VPN disconnects every 10 minutes",
                "Since Monday my VPN drops and the laptop becomes slow.",
                TicketPriority.HIGH, TicketCategory.NETWORK, creator);
        ReflectionTestUtils.setField(ticket, "id", 42L);
        ReflectionTestUtils.setField(ticket, "createdAt", Instant.parse("2026-09-23T10:15:30Z"));

        TicketResponse response = mapper.toResponse(ticket);

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.title()).isEqualTo("VPN disconnects every 10 minutes");
        assertThat(response.status()).isEqualTo(TicketStatus.OPEN);   // BR-1, set by the constructor
        assertThat(response.priority()).isEqualTo(TicketPriority.HIGH);
        assertThat(response.category()).isEqualTo(TicketCategory.NETWORK);
        assertThat(response.createdBy().id()).isEqualTo(7L);
        assertThat(response.createdBy().fullName()).isEqualTo("Akhilesh B");
        assertThat(response.assignedTo()).isNull();                   // unassigned maps to null, no NPE
        assertThat(response.createdAt()).isEqualTo(Instant.parse("2026-09-23T10:15:30Z"));
        assertThat(response.resolvedAt()).isNull();
    }

    @Test
    void nullTicketMapsToNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}