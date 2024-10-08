package ajp.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface BaseEnum<T> {

    String getCode();

    @JsonProperty
    T getValue();

}
