package gytis.courier;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestTest {
    List<String> strings = List.of("vienas", "du");
    List<Integer> integers = List.of(1, 2, 3);


    @Test
    public void test() {
        System.out.println(Optional.ofNullable(first(strings)));
        System.out.println(Optional.ofNullable(first(integers)));

        Function<String, Integer> converter = s -> s.length();
        System.out.println(describe(strings, converter));
    }

    private <T> T first(List<T> list) {
        return list.getFirst();
    }

    private  <T, R> List<R> describe(List<T> list, Function<T, R> function) {
        List<R> rList = new ArrayList<>();
        for (T lis : list) {
            rList.add(function.apply(lis));
        }

        return rList;
    }
}
