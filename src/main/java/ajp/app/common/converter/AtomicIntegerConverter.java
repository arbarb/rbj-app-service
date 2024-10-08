package ajp.app.common.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerConverter implements Converter<Integer, AtomicInteger> {

    @Override
    public AtomicInteger convert(Integer source) {
        return null;
    }

    @Override
    public <U> Converter<Integer, U> andThen(Converter<? super AtomicInteger, ? extends U> after) {
        return Converter.super.andThen(after);
    }

}
