package com.univocity.parsers.CustomTests;

import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.input.DefaultCharAppender;
import com.univocity.parsers.common.input.EOFException;
import com.univocity.parsers.common.input.ExpandingCharAppender;
import com.univocity.parsers.common.input.DefaultCharInputReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpandingCharAppenderTest {

    private ExpandingCharAppender appender;

    @BeforeEach
    public void setUp() {
        appender = new ExpandingCharAppender(10, "empty", -1);
    }

    // Step A - Buffer Operations Tests

    @Test
    public void testAppendToPartiallyFilledBuffer() {
        for (int i = 0; i < 5; i++) appender.append('A');
        assertEquals(5, appender.length());
    }

    @Test
    public void testAppendToEmptyBuffer() {
        appender.append('B');
        assertEquals("B", appender.toString());
    }

    @Test
    public void testAppendNearMaxCapacityThrowsException() {
        ExpandingCharAppender nearMaxAppender = new ExpandingCharAppender(1000, "full", -1);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> nearMaxAppender.fill('X', 10000));
    }

    @Test
    public void testFullBufferIgnoredPadding() {
        for (int i = 0; i < 10; i++) appender.append('Z');
        appender.appendIgnoringPadding(' ', ' ');
        assertEquals("ZZZZZZZZZZ", appender.toString());
    }

    @Test
    public void testAppendMultipleTimes() {
        appender.append("Hello");
        appender.append(" World");
        assertEquals("Hello World", appender.toString());
    }

    @Test
    public void testAppendCharIgnoringWhitespace() {
        appender.appendIgnoringWhitespace(' ');
        appender.append('A');
        assertEquals(" ", appender.toString());
    }

    @Test
    public void testAppendIgnoringWhitespaceOnly() {
        appender.appendIgnoringWhitespace(' ');
        assertEquals("empty", appender.toString());
    }

    @Test
    public void testAppendValidCharacterToEmptyBuffer() {
        appender.append('A');
        assertEquals("A", appender.toString());
    }

    @Test
    public void testAppendBelowCapacityNoOverflow() {
        for (int i = 0; i < 9; i++) appender.append('B');
        assertEquals(9, appender.length());
    }

    @Test
    public void testAppendSpecialCharactersWithoutExpansion() {
        appender.append('\n');
        appender.append('\t');
        appender.append('\0');
        assertEquals("\n\t\0", appender.toString());
    }

    @Test
    public void testFillBuffer() {
        appender.fill('A', 10);
        assertEquals("AAAAAAAAAA", appender.toString());
    }

    @Test
    public void testFillShorterThanCapacity() {
        appender.fill('!', 5);
        assertEquals("!!!!!", appender.toString());
    }

    @Test
    public void testFillWithWhitespace() {
        appender.fill(' ', 10);
        assertEquals("          ", appender.toString());
    }

    @Test
    public void testPrependSingleCharacter() {
        appender.prepend('A');
        assertEquals("A", appender.toString());
    }

    @Test
    public void testPrependMultipleCharacters() {
        appender.prepend('A');
        appender.prepend('B');
        assertEquals("BA", appender.toString());
    }

    @Test
    public void testPrependCharArray() {
        appender.prepend(new char[]{'H', 'i'});
        assertEquals("Hi", appender.toString());
    }

    @Test
    public void testResetBuffer() {
        appender.append('X');
        appender.reset();
        assertEquals("empty", appender.toString());
    }

    @Test
    public void testAppendCharArray() {
        appender.append(new char[]{'H', 'e', 'l', 'l', 'o'});
        assertEquals("Hello", appender.toString());
    }

    @Test
    public void testAppendUntilMultipleStops() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("123,ABC;DEF"));
        appender.appendUntil('1', charInput, ',', ';');
        assertEquals("1123", appender.toString());
    }

    @Test
    public void testAppendStringRange() {
        appender.append("World", 1, 4);
        assertEquals("orl", appender.toString());
    }

    @Test
    public void testAppendEmptyString() {
        appender.append("");
        assertEquals("empty", appender.toString());
    }

    @Test
    public void testAppendUntilStopCharacter() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("ABCDE;"));
        appender.appendUntil('A', charInput, ';');
        assertEquals("AABCDE", appender.toString());
    }

    @Test
    public void testStopOnCommaCharacter() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("XYZ,123"));
        appender.appendUntil('B', charInput, ',');
        assertEquals("BXYZ", appender.toString());
    }

    @Test
    public void testNoStopCharacterWithExpansion() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("LOREMIPSUM"));
        assertThrows(EOFException.class, () -> appender.appendUntil('C', charInput, ' '));
    }

    @Test
    public void testAppendStopCharacterOutOfRange() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("ABC"));
        assertThrows(EOFException.class, () -> appender.appendUntil('E', charInput, ';'));
    }

    @Test
    public void testGetCharsAndReset() {
        appender.append('X');
        assertEquals("X", new String(appender.getCharsAndReset()));
        assertEquals("empty", appender.toString());
    }

    @Test
    public void testAppendIgnoringWhitespaceAndPadding() {
        appender.appendIgnoringWhitespaceAndPadding(' ', '_');
        appender.append('Q');
        assertEquals(" ", appender.toString());
    }

    @Test
    public void testResetWhitespaceCount() {
        appender.append(' ');
        appender.resetWhitespaceCount();
        assertEquals(0, appender.whitespaceCount());
    }

    @Test
    public void testAppendInt() {
        appender.append(100);
        assertEquals("d", appender.toString());
    }

    @Test
    public void testAppendUntilWithMultipleStopChars() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("XYZ;ABC,DEF"));
        appender.appendUntil('X', charInput, ';', ',');
        assertEquals("XXYZ", appender.toString());
    }

    // Additional Miscellaneous Tests

    @Test
    public void testPrependEmptyBuffer() {
        appender.prepend('!');
        assertEquals("!", appender.toString());
    }

    @Test
    public void testGetEmptyChars() {
        appender.reset();
        assertArrayEquals("empty".toCharArray(), appender.getCharsAndReset());
    }

    @Test
    public void testIndexOfWithNoMatch() {
        appender.append('X');
        assertEquals(-1, appender.indexOf('Z', 0));
    }

    @Test
    public void testIndexOfMatch() {
        appender.append('X');
        appender.append('Y');
        assertEquals(1, appender.indexOf('Y', 0));
    }

    @Test
    public void testLengthWithWhitespace() {
        appender.append('X');
        appender.appendIgnoringWhitespace(' ');
        assertEquals(1, appender.length());
    }

    @Test
    public void testAppendRangeFromChars() {
        appender.append("ABCDE".toCharArray(), 1, 3);
        assertEquals("BCD", appender.toString());
    }

    @Test
    public void testAppendStringWithWhitespaceAndPadding() {
        appender.appendIgnoringWhitespaceAndPadding(' ', ' ');
        appender.append("Hello World");
        assertEquals(" Hello Worl", appender.toString());
    }

    @Test
    void testExpandAndRetryAppendIgnoringWhitespace() {
        // Attempt to append more characters than initial buffer size to trigger expandAndRetry
        char[] charsToAppend = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        for (char ch : charsToAppend) {
            appender.appendIgnoringWhitespace(ch);
        }
        assertEquals("abcdefg", appender.toString());
    }

    @Test
    void testExpandAndRetryAppendIgnoringPadding() {
        char[] charsToAppend = new char[] { 'x', 'y', 'z', 'w', 'q', 'r', 's' };
        for (char ch : charsToAppend) {
            appender.appendIgnoringPadding(ch, 'p');
        }
        assertEquals("xyzwqrs", appender.toString());
    }

    @Test
    void testExpandAndRetryAppendIgnoringWhitespaceAndPadding() {
        char[] charsToAppend = new char[] { 'h', 'i', 'j', 'k', 'l', 'm', 'n' };
        for (char ch : charsToAppend) {
            appender.appendIgnoringWhitespaceAndPadding(ch, 'p');
        }
        assertEquals("hijklmn", appender.toString());
    }

    @Test
    void testExpandAndRetryFill() {
        appender.fill('x', 10); // Attempting to fill more than initial buffer size
        assertEquals("xxxxxxxxxx", appender.toString());
    }

    // Test expand(int additionalLength, double factor) indirectly
    @Test
    public void testIndirectExpandWithAdditionalLengthAndFactor() {
        appender.fill('A', 10);  // Fill initial buffer
        appender.append('B');    // Trigger expansion with additional length of 1 and factor of 1.5
        assertEquals("AAAAAAAAAAB", appender.toString());
    }

    @Test
    public void testIndirectExpandWithLargeFactor() {
        appender.fill('C', 10);
        for (int i = 0; i < 15; i++) {
            appender.append('D');
        }
        // Verify buffer has expanded to accommodate the additional characters
        assertTrue(appender.length() >= 25);
    }

    // Test expand()
    @Test
    public void testExpandWithoutAdditionalLength() {
        appender.fill('E', 10);  // Fill buffer to initial capacity
        appender.append('F');    // Should trigger expand()
        assertEquals("EEEEEEEEEEF", appender.toString());
    }

    // Test expand(int additionalLength)
    @Test
    public void testExpandWithAdditionalLength() {
        appender.fill('G', 10);  // Fill buffer
        appender.append("HIJKL");  // Adding more than capacity should trigger expand with additional length
        assertEquals("GGGGGGGGGGHIJKL", appender.toString());
    }

    // Test prepend(char ch1, char ch2)
    @Test
    public void testPrependTwoCharacters() {
        appender.append("Hello");
        appender.prepend('X', 'Y');
        assertEquals("XYHello", appender.toString());
    }

    @Test
    public void testPrependTwoCharsToEmptyBuffer() {
        appender.prepend('A', 'B');
        assertEquals("AB", appender.toString());
    }

    // Test append(DefaultCharAppender appender)
    @Test
    public void testAppendAnotherAppender() {
        ExpandingCharAppender otherAppender = new ExpandingCharAppender(5, "empty", -1);
        otherAppender.append("12345");
        appender.append(otherAppender);
        assertEquals("12345", appender.toString());
    }

    @Test
    public void testAppendAppenderWithOverflow() {
        ExpandingCharAppender otherAppender = new ExpandingCharAppender(20, "empty", -1);
        otherAppender.append("12345678901234567890"); // 20 characters to exceed default buffer
        appender.append(otherAppender);
        assertEquals("12345678901234567890", appender.toString());
    }

    // Test appendUntil(char ch, CharInput input, char stop1, char stop2, char stop3)
    @Test
    public void testAppendUntilWithThreeStops() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("ello, world!;End"));
        appender.appendUntil('H', charInput, ',', ';', '!');
        assertEquals("Hello", appender.toString());
    }

    @Test
    public void testAppendUntilWithNoStopFound() {
        DefaultCharInputReader charInput = new DefaultCharInputReader('\n', 1024, -1, true);
        charInput.start(new StringReader("NoStopCharactersHere"));
        assertThrows(EOFException.class, () -> appender.appendUntil('S', charInput, 'X', 'Y', 'Z'));
    }


}
