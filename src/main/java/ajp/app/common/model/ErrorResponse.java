package ajp.app.common.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private List<ErrorMessage> errors;
}
