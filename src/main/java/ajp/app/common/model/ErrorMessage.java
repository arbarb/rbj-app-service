package ajp.app.common.model;

import lombok.Data;

@Data
public class ErrorMessage {
    private String field;
    private String code;
    private String message;
}
