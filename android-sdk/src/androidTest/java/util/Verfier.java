package util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

import static org.mockito.Matchers.argThat;

public class Verfier {

    public static List hasSize(final int expected) {
        return argThat(new BaseMatcher<List>() {

            public int actual;

            @Override
            public boolean matches(Object o) {
                List other = (List) o;
                actual = other.size();
                return actual == expected;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("size was not %d, it was %d", expected, actual));
            }
        });
    }
}
