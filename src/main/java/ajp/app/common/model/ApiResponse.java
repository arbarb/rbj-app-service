package ajp.app.common.model;

import lombok.Data;

import java.util.List;

import static ajp.app.common.DateUtil.currentLocalDateTime;
import static ajp.app.common.DateUtil.formatDateTime;

@Data
public class ApiResponse<T> {
    private T data;
    private String status;
    private String timestamp;
    private List<ErrorMessage> errors;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    public static <T> ApiResponse<T> from(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setStatus(SUCCESS);
        response.setTimestamp(formatDateTime(currentLocalDateTime()));
        response.setErrors(null);
        return response;
    }

    public static ApiResponse<Void> from(ErrorMessage errorMessage) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(ERROR);
        response.setTimestamp(formatDateTime(currentLocalDateTime()));
        response.setErrors(List.of(errorMessage));
        return response;
    }

    public static ApiResponse<Void> from(List<ErrorMessage> errorMessages) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(ERROR);
        response.setTimestamp(formatDateTime(currentLocalDateTime()));
        response.setErrors(errorMessages);
        return response;
    }

}
