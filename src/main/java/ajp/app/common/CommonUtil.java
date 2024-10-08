package ajp.app.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommonUtil {
    private CommonUtil() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectReader objectReader() {
        return mapper.reader();
    }

    public static ObjectWriter objectWriter() {
        return mapper.writer();
    }

    public static <T> Map<String, Object> toMap(T data) {
        return Optional.ofNullable(data)
                .map(d -> mapper.convertValue(d, mapType()))
                .orElseGet(HashMap::new);
    }

    public static <T> T convertValue(Map<String, Object> data, Class<T> type) {
        try {
            return mapper.convertValue(data, type);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    public static <U, V> V convertValue(U data, Class<V> type) {
        try {
            return mapper.convertValue(data, type);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    public static <T> TypeReference<Map<String, Object>> mapType() {
        return new TypeReference<>() {
        };
    }

}
