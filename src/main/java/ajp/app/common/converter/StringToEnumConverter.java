package ajp.app.common.converter;

import ajp.app.model.BaseEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter<T> implements Converter<String, BaseEnum<T>> {

    @Override
    public BaseEnum<T> convert(String source) {
        return null;
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super BaseEnum<T>, ? extends U> after) {
        return Converter.super.andThen(after);
    }

}
