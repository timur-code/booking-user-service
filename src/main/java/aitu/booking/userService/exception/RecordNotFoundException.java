package aitu.booking.userService.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordNotFoundException extends ApiException {

    public RecordNotFoundException() {
        super(404, "resource.not-found");
    }

    public RecordNotFoundException(Object id) {
        super(404, "resource.not-found-by-id", id);
    }
}
