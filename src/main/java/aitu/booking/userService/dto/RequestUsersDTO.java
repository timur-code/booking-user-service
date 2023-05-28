package aitu.booking.userService.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RequestUsersDTO {
    private List<UUID> idList;
}
