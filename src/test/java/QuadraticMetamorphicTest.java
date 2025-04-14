import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 * Metamorphic Testing for the Quadratic Equation Solver
 *
 * This testing approach uses metamorphic relations to verify the correctness
 * of the solver without needing exact expected outputs.
 */
public class QuadraticMetamorphicTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    /**
     * Improved parse root values from the solver output
     * @param output The solver output string
     * @return Array of root values (1 or 2 elements)
     */
    private double[] parseRoots(String output) {
        if (output.contains("i")) {
            // Complex roots case - not handled in this simple parser
            return null;
        }

        Pattern pattern = Pattern.compile("x\\d = (-?\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(output);

        double[] roots = new double[2];
        int count = 0;

        while (matcher.find() && count < 2) {
            roots[count++] = Double.parseDouble(matcher.group(1));
        }

        if (count == 0) {
            return null; // No roots found
        } else if (count == 1) {
            // Only one root found (equal roots)
            return new double[] { roots[0] };
        } else {
            return roots;
        }
    }

    /**
     * Metamorphic Relation 1: Scaling Relation
     *
     * If (a,b,c) has roots r1 and r2, then (k*a, k*b, k*c) for k≠0 has the same roots
     */
    @Test
    public void testScalingRelation() {
        long startTime = System.nanoTime();

        // Source test case
        double a1 = 1;
        double b1 = -3;
        double c1 = 2;

        // Follow-up test case with scaling factor k = 2
        double k = 2;
        double a2 = a1 * k;
        double b2 = b1 * k;
        double c2 = c1 * k;

        try {
            // Solve original equation
            Quadratic.solveQuadratic(a1, b1, c1);
            String output1 = outputStream.toString();
            double[] roots1 = parseRoots(output1);
            outputStream.reset();

            // Solve scaled equation
            Quadratic.solveQuadratic(a2, b2, c2);
            String output2 = outputStream.toString();
            double[] roots2 = parseRoots(output2);

            // Verify roots are the same
            assertNotNull(roots1, "Failed to parse roots from first equation");
            assertNotNull(roots2, "Failed to parse roots from scaled equation");
            assertEquals(roots1.length, roots2.length, "Number of roots should be the same");

            // Sort roots for comparison if there are two
            if (roots1.length == 2) {
                if (roots1[0] > roots1[1]) {
                    double temp = roots1[0];
                    roots1[0] = roots1[1];
                    roots1[1] = temp;
                }
                if (roots2[0] > roots2[1]) {
                    double temp = roots2[0];
                    roots2[0] = roots2[1];
                    roots2[1] = temp;
                }
            }

            // Compare roots with small epsilon for floating point comparison
            double epsilon = 1e-10;
            for (int i = 0; i < roots1.length; i++) {
                assertEquals(roots1[i], roots2[i], epsilon,
                           "Roots should be identical after scaling");
            }

        } catch (NotEnoughPrecisionException e) {
            fail("Unexpected precision exception");
        }

        long endTime = System.nanoTime();
        System.out.println("Scaling relation test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 2: Reciprocal Relation
     *
     * If ax² + bx + c = 0 has roots r1 and r2, then cx² + bx + a = 0 has roots 1/r1 and 1/r2
     * (when r1, r2 ≠ 0)
     */
    @Test
    public void testReciprocalRelation() {
        long startTime = System.nanoTime();

        // Source test case with non-zero roots
        double a1 = 1;
        double b1 = -5;
        double c1 = 6;

        // Follow-up test case with coefficients reversed
        double a2 = c1;
        double b2 = b1;
        double c2 = a1;

        try {
            // Solve original equation
            Quadratic.solveQuadratic(a1, b1, c1);
            String output1 = outputStream.toString();
            double[] roots1 = parseRoots(output1);
            outputStream.reset();

            // Solve reciprocal equation
            Quadratic.solveQuadratic(a2, b2, c2);
            String output2 = outputStream.toString();
            double[] roots2 = parseRoots(output2);

            // Verify roots relation (r2 = 1/r1)
            assertNotNull(roots1, "Failed to parse roots from first equation");
            assertNotNull(roots2, "Failed to parse roots from reciprocal equation");
            assertEquals(roots1.length, roots2.length, "Number of roots should be the same");

            // Calculate expected reciprocal roots
            double[] expectedReciprocals = new double[roots1.length];
            for (int i = 0; i < roots1.length; i++) {
                expectedReciprocals[i] = 1.0 / roots1[i];
            }

            // Sort arrays for comparison
            if (roots1.length == 2) {
                if (expectedReciprocals[0] > expectedReciprocals[1]) {
                    double temp = expectedReciprocals[0];
                    expectedReciprocals[0] = expectedReciprocals[1];
                    expectedReciprocals[1] = temp;
                }
                if (roots2[0] > roots2[1]) {
                    double temp = roots2[0];
                    roots2[0] = roots2[1];
                    roots2[1] = temp;
                }
            }

            // Compare with small epsilon for floating point comparison
            double epsilon = 1e-10;
            for (int i = 0; i < roots2.length; i++) {
                assertEquals(expectedReciprocals[i], roots2[i], epsilon,
                           "Root should be reciprocal of original root");
            }

        } catch (NotEnoughPrecisionException e) {
            fail("Unexpected precision exception");
        }

        long endTime = System.nanoTime();
        System.out.println("Reciprocal relation test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 3: Root Sum and Product Relations
     *
     * For a quadratic ax² + bx + c = 0:
     * - Sum of roots = -b/a
     * - Product of roots = c/a
     */
    @Test
    public void testRootRelations() {
        long startTime = System.nanoTime();

        // Test cases with real roots (only for real roots)
        double[][] testCases = {
            {1, -5, 6},    // Roots: 2, 3
            {2, -7, 3},    // Roots: 3, 0.5
            {1, -1, -2},   // Roots: 2, -1
            {3, 6, 3}      // Roots: -1, -1 (equal roots)
        };

        for (double[] coeffs : testCases) {
            double a = coeffs[0];
            double b = coeffs[1];
            double c = coeffs[2];

            try {
                // Solve equation
                Quadratic.solveQuadratic(a, b, c);
                String output = outputStream.toString();
                double[] roots = parseRoots(output);
                outputStream.reset();

                // Verify root relations
                assertNotNull(roots, "Failed to parse roots");

                // Expected relations
                double expectedSum = -b / a;
                double expectedProduct = c / a;

                // Actual relations
                double actualSum;
                double actualProduct;

                if (roots.length == 1) {
                    // Equal roots case
                    actualSum = 2 * roots[0]; // Sum of two equal roots
                    actualProduct = roots[0] * roots[0]; // Product of two equal roots
                } else {
                    actualSum = roots[0] + roots[1];
                    actualProduct = roots[0] * roots[1];
                }

                // Verify with epsilon for floating point comparison
                double epsilon = 1e-10;
                assertEquals(expectedSum, actualSum, epsilon,
                           "Sum of roots should equal -b/a for a=" + a + ", b=" + b + ", c=" + c);
                assertEquals(expectedProduct, actualProduct, epsilon,
                           "Product of roots should equal c/a for a=" + a + ", b=" + b + ", c=" + c);

            } catch (NotEnoughPrecisionException e) {
                fail("Unexpected precision exception for a=" + a + ", b=" + b + ", c=" + c);
            }
        }

        long endTime = System.nanoTime();
        System.out.println("Root relations test execution time: " + (endTime - startTime) + " ns");
    }

    /**
     * Metamorphic Relation 4: Transformation Relation
     *
     * COMPLETELY REWRITTEN to use simple coefficients with well-known roots
     */
    @Test
    public void testTransformationRelation() {
        long startTime = System.nanoTime();

        // Use simple coefficients with well-known roots
        double a1 = 1;
        double b1 = -3; // sum of roots = 3
        double c1 = 2;  // product of roots = 2, roots are 1 and 2
        double h = 1;   // shift amount

        // Calculate expected roots after transformation
        double root1 = 1; // first root
        double root2 = 2; // second root
        double transformedRoot1 = root1 + h; // 2
        double transformedRoot2 = root2 + h; // 3

        // Calculate transformed coefficients manually
        // For roots r1+h and r2+h, the equation is:
        // a(x-(r1+h))(x-(r2+h)) = 0
        // Expanding: a(x^2 - (r1+h+r2+h)x + (r1+h)(r2+h)) = 0
        // So: a*x^2 - a*(r1+r2+2h)*x + a*(r1*r2 + h*r1 + h*r2 + h^2) = 0
        // Comparing with ax^2 + bx + c = 0:
        // b = -a*(r1+r2+2h)
        // c = a*(r1*r2 + h*r1 + h*r2 + h^2)
        double a2 = a1;
        double b2 = -a1 * (root1 + root2 + 2*h); // -a*(1+2+2*1) = -5
        double c2 = a1 * (root1*root2 + h*root1 + h*root2 + h*h); // 1*(2 + 1 + 2 + 1) = 6

        try {
            // Solve original equation
            Quadratic.solveQuadratic(a1, b1, c1);
            String output1 = outputStream.toString();
            double[] roots1 = parseRoots(output1);
            outputStream.reset();

            // Solve transformed equation with our calculated coefficients
            Quadratic.solveQuadratic(a2, b2, c2);
            String output2 = outputStream.toString();
            double[] roots2 = parseRoots(output2);

            // Verify both equations return proper roots
            assertNotNull(roots1, "Failed to parse roots from first equation");
            assertNotNull(roots2, "Failed to parse roots from transformed equation");
            assertEquals(2, roots1.length, "Original equation should have two distinct roots");
            assertEquals(2, roots2.length, "Transformed equation should have two distinct roots");

            // Sort roots for stable comparison
            Arrays.sort(roots1);
            Arrays.sort(roots2);

            // Verify original roots with tolerance
            assertEquals(root1, roots1[0], 0.0001, "First original root should be close to " + root1);
            assertEquals(root2, roots1[1], 0.0001, "Second original root should be close to " + root2);

            // Verify transformed roots with tolerance
            assertEquals(transformedRoot1, roots2[0], 0.0001, "First transformed root should be close to " + transformedRoot1);
            assertEquals(transformedRoot2, roots2[1], 0.0001, "Second transformed root should be close to " + transformedRoot2);

        } catch (NotEnoughPrecisionException e) {
            fail("Unexpected precision exception");
        }

        long endTime = System.nanoTime();
        System.out.println("Transformation relation test execution time: " + (endTime - startTime) + " ns");
    }


}