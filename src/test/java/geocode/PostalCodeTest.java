package geocode;

import static geocode.kdtree.KDTree.EARTH_RADIUS_IN_KM;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PostalCodeTest {

    @Test
    public void givenPostalCodeRowWithAccuracy_constructor_createsPostalCode() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        assert fileStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        String firstRow = reader.readLine();
        PostalCode postalCode = new PostalCode(firstRow);
        assertPostalCode(postalCode, "US", "99553", "Akutan", "Alaska",
                "AK", "Aleutians East", "013", "", "",
                54.143, -165.7854, 1);
    }

    @Test
    public void givenPostalCodeRowWithNoAccuracy_constructor_createsPostalCode() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        assert fileStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        reader.readLine();
        String secondRow = reader.readLine();
        PostalCode postalCode = new PostalCode(secondRow);
        assertPostalCode(postalCode, "US", "09001", "APO AA", "", "",
                "", "", "", "", 38.1105, 15.6613,
                0);
    }

    @Test
    public void givenLatitudeLongitude_constructor_createsPostalCode() {
        PostalCode postalCode = new PostalCode(38.1105, 15.6613);
        assertPostalCode(postalCode, null, null, "Search", null,
                null, null, null, null, null, 38.1105,
                15.6613, 0);
    }

    @Test
    public void givenPostalCode_squaredDistance_returnSquaredDistanceBetweenPostalCodes() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        assert fileStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        String firstRow = reader.readLine();
        PostalCode akutanAK = new PostalCode(firstRow);
        PostalCode coldBayAK = new PostalCode(55.1858, -162.7211);
        double distanceInKm = Math.sqrt(akutanAK.squaredDistance(coldBayAK)) * EARTH_RADIUS_IN_KM;
        assertThat(Math.round(distanceInKm), is(Math.round(228.6)));
    }

    @Test
    public void givenPostalCode_distance_returnDistanceBetweenPostalCodes() throws IOException {
        InputStream fileStream = ClassLoader.getSystemResourceAsStream("US.txt");
        assert fileStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        String firstRow = reader.readLine();
        PostalCode akutanAK = new PostalCode(firstRow);
        PostalCode coldBayAK = new PostalCode(55.1858, -162.7211);
        double distanceInKm = akutanAK.distance(coldBayAK, EARTH_RADIUS_IN_KM);
        assertThat(Math.round(distanceInKm), is(Math.round(228.6)));
    }

    private void assertPostalCode(PostalCode postalCode, String countryCode, String postal, String placeName,
                                  String adminName1, String adminCode1, String adminName2, String adminCode2,
                                  String adminName3, String adminCode3, double latitude, double longitude,
                                  int accuracy) {
        assertThat(postalCode.countryCode, is(countryCode));
        assertThat(postalCode.postalCode, is(postal));
        assertThat(postalCode.placeName, is(placeName));
        assertThat(postalCode.adminName1, is(adminName1));
        assertThat(postalCode.adminCode1, is(adminCode1));
        assertThat(postalCode.adminName2, is(adminName2));
        assertThat(postalCode.adminCode2, is(adminCode2));
        assertThat(postalCode.adminName3, is(adminName3));
        assertThat(postalCode.adminCode3, is(adminCode3));
        assertThat(postalCode.latitude, is(latitude));
        assertThat(postalCode.longitude, is(longitude));
        assertThat(postalCode.accuracy, is(accuracy));
        assertThat(postalCode.toString(), is(placeName));
        assertThat(postalCode.point[0], is(cos(toRadians(postalCode.latitude)) * cos(toRadians(postalCode.longitude))));
        assertThat(postalCode.point[1], is(cos(toRadians(postalCode.latitude)) * sin(toRadians(postalCode.longitude))));
        assertThat(postalCode.point[2], is(sin(toRadians(postalCode.latitude))));
    }
}
