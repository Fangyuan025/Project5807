import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import com.fastaccess.tfl.helper.DateHelper;
import com.fastaccess.tfl.helper.DateHelper.DateFormats;

/**
 * Metamorphic Testing for the DateHelper class
 * 
 * This testing approach verifies the correctness of date operations by defining
 * metamorphic relations between different operations.
 */
public class DateHelperMetamorphicTest {

    /**
     * Metamorphic Relation 1: Format Conversion Symmetry
     * <p>
     * Converting from Format A to B, then from B to A should yield the original date
     */
    @Test
    public void testFormatConversionSymmetry() {
        long startTime = System.nanoTime();

        // Source test case with a valid date
        String originalDate = "2024-02-29";
        DateFormats sourceFormat = DateFormats.D_YYYYMMDD;
        DateFormats intermediateFormat = DateFormats.S_DDMMYYYY;

        // Step 1: Convert from source to intermediate
        long dateInMillis = DateHelper.parseDate(originalDate, sourceFormat);
        String intermediateDate = DateHelper.getDesiredFormat(intermediateFormat, dateInMillis);

        // Step 2: Convert back from intermediate to source
        long intermediateDateInMillis = DateHelper.parseDate(intermediateDate, intermediateFormat);
        String finalDate = DateHelper.getDesiredFormat(sourceFormat, intermediateDateInMillis);

        // Verify: A -> B -> A should equal A
        assertEquals(originalDate, finalDate,
                "Converting " + originalDate + " to " + intermediateFormat +
                        " and back to " + sourceFormat + " should yield the original date");

        long endTime = System.nanoTime();
        System.out.println("Format conversion symmetry test execution time: " + (endTime - startTime) + " ns");
    }

    static class DateFormatPair {
        DateFormats format1;
        DateFormats format2;
        String testName;

        DateFormatPair(DateFormats format1, DateFormats format2, String testName) {
            this.format1 = format1;
            this.format2 = format2;
            this.testName = testName;
        }
    }

    static Stream<DateFormatPair> formatPairProvider() {
        return Stream.of(
                new DateFormatPair(DateFormats.D_YYYYMMDD, DateFormats.S_DDMMYYYY, "Dash-YYYY to Slash-DDMM"),
                new DateFormatPair(DateFormats.S_YYYYMMDD, DateFormats.D_DDMMYYYY, "Slash-YYYY to Dash-DDMM"),
                new DateFormatPair(DateFormats.D_YYMMDD, DateFormats.S_YYYYMMDD, "YY to YYYY format")
        );
    }

    @ParameterizedTest
    @MethodSource("formatPairProvider")
    public void testMultipleFormatConversionSymmetry(DateFormatPair pair) {
        long startTime = System.nanoTime();

        // Create a calendar instance for Feb 29, 2024 (leap year date)
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.FEBRUARY, 29);
        long timeInMillis = calendar.getTimeInMillis();

        // Format the date in the first format
        String dateInFormat1 = DateHelper.getDesiredFormat(pair.format1, timeInMillis);

        // Convert to second format
        long parsedMillis = DateHelper.parseDate(dateInFormat1, pair.format1);
        String dateInFormat2 = DateHelper.getDesiredFormat(pair.format2, parsedMillis);

        // Convert back to first format
        long parsedMillis2 = DateHelper.parseDate(dateInFormat2, pair.format2);
        String dateBackInFormat1 = DateHelper.getDesiredFormat(pair.format1, parsedMillis2);

        // Verify the round-trip conversion
        assertEquals(dateInFormat1, dateBackInFormat1,
                "Round-trip conversion failed for test: " + pair.testName);

        long endTime = System.nanoTime();
        System.out.println("Format conversion test '" + pair.testName +
                "' execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 2: Calendar Addition Consistency
     * <p>
     * Adding days to a date should be consistent across different date formats
     */
    @Test
    public void testCalendarAdditionConsistency() {
        long startTime = System.nanoTime();

        // Source dates in different formats
        String date1 = "2024-02-29";
        String date2 = "29/02/2024";
        DateFormats format1 = DateFormats.D_YYYYMMDD;
        DateFormats format2 = DateFormats.S_DDMMYYYY;

        // Number of days to add
        int daysToAdd = 10;

        // Method 1: Parse date1, add days, then format
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(DateHelper.parseDate(date1, format1));
        cal1.add(Calendar.DAY_OF_MONTH, daysToAdd);
        String result1 = DateHelper.getDesiredFormat(format1, cal1.getTimeInMillis());

        // Method 2: Parse date2, add days, then format
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(DateHelper.parseDate(date2, format2));
        cal2.add(Calendar.DAY_OF_MONTH, daysToAdd);
        String result2 = DateHelper.getDesiredFormat(format2, cal2.getTimeInMillis());

        // Method 3: Calculate expected date manually
        Calendar cal3 = Calendar.getInstance();
        cal3.set(2024, Calendar.FEBRUARY, 29);
        cal3.add(Calendar.DAY_OF_MONTH, daysToAdd);
        SimpleDateFormat sdf1 = new SimpleDateFormat(format1.getDateFormat(), Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format2.getDateFormat(), Locale.ENGLISH);
        String expected1 = sdf1.format(cal3.getTime());
        String expected2 = sdf2.format(cal3.getTime());

        // Verify results
        assertEquals(expected1, result1, "Date addition result should match expected for format1");
        assertEquals(expected2, result2, "Date addition result should match expected for format2");

        // Convert between formats for further verification
        long result1Millis = DateHelper.parseDate(result1, format1);
        long result2Millis = DateHelper.parseDate(result2, format2);

        // The dates should represent the same point in time (within a day - ignoring time)
        assertTrue(Math.abs(result1Millis - result2Millis) < 24 * 60 * 60 * 1000,
                "Date results from different formats should be within 24 hours");

        long endTime = System.nanoTime();
        System.out.println("Calendar addition consistency test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 3: Date Difference Consistency
     * <p>
     * Calculating days between two dates should be consistent regardless of the format
     */
    @Test
    public void testDateDifferenceConsistency() {
        long startTime = System.nanoTime();

        // Source dates
        String startDate1 = "2024-02-01";
        String endDate1 = "2024-02-29";
        DateFormats format1 = DateFormats.D_YYYYMMDD;

        String startDate2 = "01/02/2024";
        String endDate2 = "29/02/2024";
        DateFormats format2 = DateFormats.S_DDMMYYYY;

        // Calculate days between dates using format1
        Long daysDiff1 = DateHelper.getDaysBetweenTwoDate(startDate1, endDate1, format1);

        // Calculate days between dates using format2
        Long daysDiff2 = DateHelper.getDaysBetweenTwoDate(startDate2, endDate2, format2);

        // Verify that the difference is the same
        assertNotNull(daysDiff1, "Days difference should not be null for format1");
        assertNotNull(daysDiff2, "Days difference should not be null for format2");
        assertEquals(daysDiff1, daysDiff2, "Days difference should be the same regardless of format");

        // Calculate expected difference manually
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.FEBRUARY, 1);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.FEBRUARY, 29);
        long diffMillis = cal1.getTimeInMillis() - cal2.getTimeInMillis();
        long expectedDays = diffMillis / (24 * 60 * 60 * 1000);

        // Verify that the calculated difference matches expected
        assertEquals(expectedDays, daysDiff1.longValue(), "Days difference should match expected value");

        long endTime = System.nanoTime();
        System.out.println("Date difference consistency test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 4: Transitive Date Conversion
     * <p>
     * If Format A can be converted to Format B, and Format B can be converted to Format C,
     * then Format A should be convertible to Format C directly
     */
    @Test
    public void testTransitiveDateConversion() {
        long startTime = System.nanoTime();

        // Define three formats
        DateFormats formatA = DateFormats.D_YYYYMMDD;
        DateFormats formatB = DateFormats.S_DDMMYYYY;
        DateFormats formatC = DateFormats.D_YYMMDD;

        // Source date in Format A
        String dateA = "2024-02-29";

        // Convert A to B
        long dateMillis = DateHelper.parseDate(dateA, formatA);
        String dateB = DateHelper.getDesiredFormat(formatB, dateMillis);

        // Convert B to C
        long dateBMillis = DateHelper.parseDate(dateB, formatB);
        String dateC_via_B = DateHelper.getDesiredFormat(formatC, dateBMillis);

        // Convert A directly to C
        String dateC_direct = DateHelper.getDesiredFormat(formatC, dateMillis);

        // Verify that the two paths produce the same result
        assertEquals(dateC_direct, dateC_via_B,
                "Converting directly from A to C should give the same result as A to B to C");

        long endTime = System.nanoTime();
        System.out.println("Transitive date conversion test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 5: Date Parsing Consistency
     * <p>
     * Converting a timestamp to a formatted date and parsing it back should yield the same timestamp
     */
    @Test
    public void testDateParsingConsistency() {
        long startTime = System.nanoTime();

        // Source timestamp - current time with day precision
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long originalTimestamp = cal.getTimeInMillis();

        // Test with multiple formats
        DateFormats[] formatsToTest = {
                DateFormats.D_YYYYMMDD,
                DateFormats.S_DDMMYYYY,
                DateFormats.D_YYMMDD
        };

        for (DateFormats format : formatsToTest) {
            // Format the timestamp
            String formattedDate = DateHelper.getDesiredFormat(format, originalTimestamp);

            // Parse it back
            long parsedTimestamp = DateHelper.parseDate(formattedDate, format);

            // Get calendar instances for comparison (ignoring time parts)
            Calendar originalCal = Calendar.getInstance();
            originalCal.setTimeInMillis(originalTimestamp);
            originalCal.set(Calendar.HOUR_OF_DAY, 0);
            originalCal.set(Calendar.MINUTE, 0);
            originalCal.set(Calendar.SECOND, 0);
            originalCal.set(Calendar.MILLISECOND, 0);

            Calendar parsedCal = Calendar.getInstance();
            parsedCal.setTimeInMillis(parsedTimestamp);
            parsedCal.set(Calendar.HOUR_OF_DAY, 0);
            parsedCal.set(Calendar.MINUTE, 0);
            parsedCal.set(Calendar.SECOND, 0);
            parsedCal.set(Calendar.MILLISECOND, 0);

            // Verify same day
            assertEquals(originalCal.get(Calendar.YEAR), parsedCal.get(Calendar.YEAR),
                    "Year should match after format/parse with " + format);
            assertEquals(originalCal.get(Calendar.MONTH), parsedCal.get(Calendar.MONTH),
                    "Month should match after format/parse with " + format);
            assertEquals(originalCal.get(Calendar.DAY_OF_MONTH), parsedCal.get(Calendar.DAY_OF_MONTH),
                    "Day should match after format/parse with " + format);
        }

        long endTime = System.nanoTime();
        System.out.println("Date parsing consistency test execution time: " + (endTime - startTime) + " ns");
    }

}