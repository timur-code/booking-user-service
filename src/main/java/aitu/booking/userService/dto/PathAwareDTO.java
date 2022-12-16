package aitu.booking.userService.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class PathAwareDTO {
    private String path;
}
