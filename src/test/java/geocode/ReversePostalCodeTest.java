package geocode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipInputStream;

public class ReversePostalCodeTest {

    @Test
    public void givenPostalCodeZipFile_constructor_buildsKdTree() throws IOException {
        ZipInputStream fileStream = new ZipInputStream(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(
                "US.zip")));
        ReversePostalCode reversePostalCode = new ReversePostalCode(fileStream);
        assertThat(reversePostalCode.nearestPostalCode(39.0955, -94.5844, null).postalCode,
                is("64121"));
    }

    @Test
    public void givenPostalCodeTextFile_constructor_buildsKdTree() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        ReversePostalCode reversePostalCode = new ReversePostalCode(fileStream);
        assertThat(reversePostalCode.nearestPostalCode(39.0955, -94.5844, null).postalCode,
                is("64121"));
    }

    @Test
    public void givenLocationInGreenlandWithMaxDistance2_findNearestPostalCode_shouldReturnNoPostalCode() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        ReversePostalCode reversePostalCode = new ReversePostalCode(fileStream);
        assertNull(reversePostalCode.nearestPostalCode(78.695697, -41.337372, 2D));
    }

    @Test
    public void givenLocationInGreenlandWithNoMaxDistance_findNearestPostalCode_shouldReturnNearestPostalCode() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        ReversePostalCode reversePostalCode = new ReversePostalCode(fileStream);
        assertThat(reversePostalCode.nearestPostalCode(78.695697, -41.337372, null).postalCode,
                is("09704"));
    }

    @Test
    public void givenLocationInKCWithMaxDistance2_findNearestPostalCode_shouldReturnNearestPostalCode() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        ReversePostalCode reversePostalCode = new ReversePostalCode(fileStream);
        assertThat(reversePostalCode.nearestPostalCode(39.0955, -94.5844, 2D).postalCode,
                is("64121"));
    }
}
