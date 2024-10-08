package ajp.app.common.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseRequest<T> {
    private UUID requestId;
    private T payload;
    private String timestamp;
}
