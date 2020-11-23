package org.example.app.bookmark.testutils;

public class TestUtils {

    /**
     * Delimiter string.
     */
    private static final String testCaseDelimiter = "------------------------------------------------------------------";


    /**
     * Prints test header.
     * Should be called in method annotated with @Before.
     *
     * @param testName to print
     */
    public static void printTestHeader(String testName) {
        System.out.println(testCaseDelimiter + "\nStarting: " + testName + "\n" + testCaseDelimiter + "\n");
    }

    /**
     * Prints test footer.
     * Should be called in method annotated with @After.
     */
    public static void printTestFooter() {
        System.out.println(testCaseDelimiter + "\n");
    }

    /**
     * Prints information for test.
     *
     * @param info to print.
     */
    public static void printTestInfo(String info) {
        System.out.println("TEST INFO: " + info);
    }
}
