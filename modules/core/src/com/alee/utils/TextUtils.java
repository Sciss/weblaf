/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alee.utils;

import com.alee.api.jdk.Function;
import com.alee.api.jdk.Supplier;
import com.alee.managers.language.LM;
import com.alee.utils.compare.Filter;
import com.alee.utils.xml.ColorConverter;
import com.alee.utils.xml.InsetsConverter;
import com.alee.utils.xml.PointConverter;
import com.alee.utils.xml.RectangleConverter;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

/**
 * This class provides a set of utilities to work with various text usage cases.
 *
 * @author Mikle Garin
 */
public final class TextUtils
{
    /**
     * Separators used to determine words in text.
     */
    private static final List<Character> textSeparators = CollectionUtils.asList (
            ' ', '.', ',', ':', ';', '/', '|', '{', '}', '[', ']', '(', ')', '<', '>',
            '\\', '\n', '\t', '\'', '\'', '-', '+', '*', '%', '$', '#', '@', '!', '~', '^', '&', '?'
    );

    /**
     * Text provider for any type of objects.
     */
    private static final Function<Object, String> simpleTextProvider = new Function<Object, String> ()
    {
        @Override
        public String apply ( final Object object )
        {
            return object != null ? object.toString () : "null";
        }
    };

    /**
     * Default ID part length.
     */
    private static final int idPartLength = 5;

    /**
     * Default ID prefix.
     */
    private static final String defaultIdPrefix = "WebLaF";

    /**
     * Default ID suffix.
     */
    private static final String defaultIdSuffix = "ID";

    /**
     * Default separator.
     */
    private static final String defaultSeparator = ",";

    /**
     * Preferred system text lines separator cache.
     */
    private static String systemLineSeparator;

    /**
     * Private constructor to avoid instantiation.
     */
    private TextUtils ()
    {
        throw new UtilityException ( "Utility classes are not meant to be instantiated" );
    }

    /**
     * Returns preferred system text lines separator.
     *
     * @return preferred system text lines separator
     */
    public static String getSystemLineSeparator ()
    {
        if ( systemLineSeparator == null )
        {
            try
            {
                systemLineSeparator = System.getProperty ( "line.separator" );
            }
            catch ( final SecurityException ignored )
            {
                // Ignore possible security exception
            }
            if ( systemLineSeparator == null )
            {
                systemLineSeparator = "\n";
            }
        }
        return systemLineSeparator;
    }

    /**
     * Returns message formatted with common string representations of the provided objects.
     *
     * @param text    message to format
     * @param objects objects to use for message formatting
     * @return message formatted with common string representations of the provided objects
     */
    public static String format ( final String text, final Object... objects )
    {
        final Object[] data = new Object[ objects != null ? objects.length : 0 ];
        if ( objects != null )
        {
            for ( int i = 0; i < objects.length; i++ )
            {
                data[ i ] = toString ( objects[ i ] );
            }
        }
        return String.format ( text, data );
    }

    /**
     * Returns text with replaced character at the specified index.
     *
     * @param text      text to replace character in
     * @param index     index of the character to replace
     * @param character replacement character
     * @return text with replaced character at the specified index
     */
    public static String replace ( final String text, final int index, final char character )
    {
        final StringBuilder sb = new StringBuilder ( text );
        sb.setCharAt ( index, character );
        return sb.toString ();
    }

    /**
     * Returns common string representation of the specified object.
     * Unlike the common {@link Object#toString()} method it will not throw {@link java.lang.NullPointerException} and returns slightly
     * different results depending on the object class type. For most it will still return the same result as {@link Object#toString()}.
     *
     * @param object object to return common string representation for
     * @return common string representation of the specified object
     */
    public static String toString ( final Object object )
    {
        if ( object == null )
        {
            return "null";
        }
        else if ( object instanceof String )
        {
            return ( String ) object;
        }
        else
        {
            return object.toString ();
        }
    }

    /**
     * Returns whether the first {@link String} equals to second one.
     * This method will compare {@link String} even if they are null without throwing any exceptions.
     *
     * @param string      {@link String} to compare
     * @param compareWith {@link String} to compare with
     * @return {@code true} if the first {@link String} equals second one, {@code false} otherwise
     */
    @SuppressWarnings ( "StringEquality" )
    public static boolean equals ( final String string, final String compareWith )
    {
        return string == compareWith || string != null && string.equals ( compareWith );
    }

    /**
     * Returns whether the first {@link String} equals to second one ignoring case.
     * This method will compare {@link String} even if they are null without throwing any exceptions.
     *
     * @param string      {@link String} to compare
     * @param compareWith {@link String} to compare with
     * @return {@code true} if the first {@link String} equals second one ignoring case, {@code false} otherwise
     */
    @SuppressWarnings ( "StringEquality" )
    public static boolean equalsIgnoreCase ( final String string, final String compareWith )
    {
        return string == compareWith || string != null && string.equalsIgnoreCase ( compareWith );
    }

    /**
     * Returns list of strings based on single pattern parsed using different number values in range.
     *
     * @param pattern values pattern
     * @param from    range start
     * @param to      range end
     * @return list of strings based on single pattern parsed using different number values in range
     */
    public static List<String> numbered ( final String pattern, final int from, final int to )
    {
        final List<String> list = new ArrayList<String> ( Math.abs ( from - to ) );
        if ( from < to )
        {
            for ( int i = from; i <= to; i++ )
            {
                list.add ( format ( pattern, i ) );
            }
        }
        else
        {
            for ( int i = from; i >= to; i-- )
            {
                list.add ( format ( pattern, i ) );
            }
        }
        return list;
    }

    /**
     * Returns text with all line breaks removed.
     *
     * @param text text to remove line breaks from
     * @return text with all line breaks removed
     */
    public static String removeLineBreaks ( final String text )
    {
        return text.replaceAll ( "\\r\\n|\\r|\\n", "" );
    }

    /**
     * Returns text with all spacings removed.
     *
     * @param text text to remove spacings from
     * @return text with all spacings removed
     */
    public static String removeSpacings ( final String text )
    {
        return text.replaceAll ( "[ \\t]", "" );
    }

    /**
     * Returns first number found in text.
     *
     * @param text text to search through
     * @return first found number
     */
    public static Integer findFirstNumber ( final String text )
    {
        final StringBuilder sb = new StringBuilder ( "" );
        for ( int j = 0; j < text.length (); j++ )
        {
            final char ch = text.charAt ( j );
            if ( Character.isDigit ( ch ) )
            {
                sb.append ( ch );
            }
            else if ( sb.length () > 0 )
            {
                break;
            }
        }
        return Integer.parseInt ( sb.toString () );
    }

    /**
     * Returns a word from text at the specified location.
     *
     * @param text     text to retrieve the word from
     * @param location word location
     * @return word
     */
    public static String getWord ( final String text, final int location )
    {
        final String word;
        if ( 0 <= location && location < text.length () )
        {
            if ( !textSeparators.contains ( text.charAt ( location ) ) )
            {
                int wordStart = location;
                int wordEnd = location;

                // At the word start
                while ( wordEnd < text.length () - 1 && !textSeparators.contains ( text.charAt ( wordEnd ) ) )
                {
                    wordEnd++;
                }

                // At the word end
                while ( wordStart > 0 && !textSeparators.contains ( text.charAt ( wordStart - 1 ) ) )
                {
                    wordStart--;
                }

                word = wordStart == wordEnd ? null : text.substring ( wordStart, wordEnd );
            }
            else
            {
                // There is no word at the specified location
                word = null;
            }
        }
        else
        {
            // Location is outside of the text boundaries
            word = null;
        }
        return word;
    }

    /**
     * Returns index of the first character in the word at the specified location.
     *
     * @param text     text to retrieve the word start index from
     * @param location word location
     * @return index of the first character in the word at the specified location
     */
    public static int getWordStart ( final String text, final int location )
    {
        final int start;
        if ( 0 <= location && location < text.length () )
        {
            if ( !textSeparators.contains ( text.charAt ( location ) ) )
            {
                int wordStart = location;
                while ( wordStart > 0 && !textSeparators.contains ( text.charAt ( wordStart - 1 ) ) )
                {
                    wordStart--;
                }
                start = wordStart;
            }
            else
            {
                // Location points at a separator character
                start = -1;
            }
        }
        else
        {
            // Location is outside of the text boundaries
            start = -1;
        }
        return start;
    }

    /**
     * Returns a word end index at the specified location.
     *
     * @param text     text to retrieve the word end index from
     * @param location word location
     * @return word end index
     */
    public static int getWordEnd ( final String text, final int location )
    {
        final int end;
        if ( 0 <= location && location < text.length () )
        {
            if ( !textSeparators.contains ( text.charAt ( location ) ) )
            {
                int wordEnd = location;
                while ( wordEnd < text.length () && !textSeparators.contains ( text.charAt ( wordEnd ) ) )
                {
                    wordEnd++;
                }
                end = wordEnd;
            }
            else
            {
                // Location points at a separator character
                end = -1;
            }
        }
        else
        {
            // Location is outside of the text boundaries
            end = -1;
        }
        return end;
    }

    /**
     * Returns begin index of last word in the specified text.
     *
     * @param string text to process
     * @return begin index of last word in the specified text
     */
    public static int findLastRowWordStartIndex ( final String string )
    {
        boolean spaceFound = false;
        boolean skipSpace = true;
        for ( int i = string.length () - 1; i >= 0; i-- )
        {
            final char c = string.charAt ( i );
            if ( !spaceFound && !skipSpace )
            {
                if ( c == ' ' || c == '\t' || c == '\r' || c == '\n' )
                {
                    spaceFound = true;
                }
            }
            else
            {
                if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
                {
                    if ( spaceFound )
                    {
                        return i;
                    }
                    skipSpace = false;
                }
            }
        }
        return -1;
    }

    /**
     * Returns begin index of first word after specified index.
     *
     * @param string text to process
     * @param from   index to start search from
     * @return begin index of first word after specified index
     */
    public static int findFirstWordFromIndex ( final String string, final int from )
    {
        for ( int i = from; i < string.length (); i++ )
        {
            final char c = string.charAt ( i );
            if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns last index of the first word end.
     *
     * @param string text to process
     * @return last index of the first word end
     */
    public static int findFirstRowWordEndIndex ( final String string )
    {
        boolean spaceFound = false;
        for ( int i = 0; i < string.length (); i++ )
        {
            final char c = string.charAt ( i );
            if ( !spaceFound )
            {
                if ( c == ' ' || c == '\t' || c == '\r' || c == '\n' )
                {
                    spaceFound = true;
                }
            }
            else
            {
                if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
                {
                    return i;
                }
            }
        }
        return string.length ();
    }

    /**
     * Returns text with first lines removed.
     *
     * @param text  text to crop
     * @param count lines count to crop
     * @return cropped text
     */
    public static String removeFirstLines ( final String text, final int count )
    {
        int found = 0;
        int index = 0;
        while ( found < count )
        {
            index = text.indexOf ( "\n", index );
            if ( index != -1 )
            {
                index += 1;
                found++;
            }
            else
            {
                return "";
            }
        }
        return text.substring ( index );
    }

    /**
     * Returns point extracted from text.
     *
     * @param text text to extract point from
     * @return extracted point
     */
    public static Point parsePoint ( final String text )
    {
        return parsePoint ( text, "," );
    }

    /**
     * Returns point extracted from text.
     *
     * @param text      text to extract point from
     * @param separator point values separator
     * @return extracted point
     */
    public static Point parsePoint ( final String text, final String separator )
    {
        final String[] parts = text.split ( separator );
        return parts.length == 2 ? new Point ( Integer.parseInt ( parts[ 0 ].trim () ), Integer.parseInt ( parts[ 1 ].trim () ) ) : null;
    }

    /**
     * Returns text with all control symbols removed.
     * This method works faster than simple replaceAll("\\p{Cntrl}", "").
     *
     * @param text text to modify
     * @return text without control symbols
     */
    public static String removeControlSymbols ( final String text )
    {
        final int length = text.length ();
        final char[] oldChars = new char[ length ];
        text.getChars ( 0, length, oldChars, 0 );
        int newLen = 0;
        for ( int j = 0; j < length; j++ )
        {
            final char ch = oldChars[ j ];
            if ( ch >= ' ' )
            {
                oldChars[ newLen ] = ch;
                newLen++;
            }
        }
        return text;
    }

    /**
     * Returns shortened text.
     *
     * @param text      text to shorten
     * @param maxLength maximum shortened text length
     * @param addDots   add dots at the end of the text when shortened
     * @return shortened text
     */
    public static String shortenText ( final String text, final int maxLength, final boolean addDots )
    {
        return text.length () <= maxLength ? text :
                text.substring ( 0, maxLength > 3 && addDots ? maxLength - 3 : maxLength ) + ( addDots ? "..." : "" );
    }

    /**
     * Returns shortened text.
     *
     * @param text      text to shorten
     * @param maxLength maximum shortened text length
     * @param addDots   add dots at the end of the text when shortened
     * @return shortened text
     */
    public static String shortenTextEnd ( final String text, final int maxLength, final boolean addDots )
    {
        return text.length () <= maxLength ? text :
                ( addDots ? "..." : "" ) + text.substring ( text.length () - ( maxLength > 3 && addDots ? maxLength - 3 : maxLength ) );
    }

    /**
     * Returns a list of text parts split using specified separator.
     *
     * @param string text to split
     * @return list of split parts
     */
    public static List<String> stringToList ( final String string )
    {
        return stringToList ( string, defaultSeparator );
    }

    /**
     * Returns a list of text parts split using specified separator.
     *
     * @param string    text to split
     * @param separator text parts separator
     * @return list of split parts
     */
    public static List<String> stringToList ( final String string, final String separator )
    {
        final List<String> strings = new ArrayList<String> ();
        if ( string != null )
        {
            final StringTokenizer tokenizer = new StringTokenizer ( string, separator, false );
            while ( tokenizer.hasMoreTokens () )
            {
                strings.add ( tokenizer.nextToken ().trim () );
            }
        }
        return strings;
    }

    /**
     * Returns a list of integer parts split using specified separator.
     *
     * @param string text to split
     * @return list of split parts
     */
    public static List<Integer> stringToIntList ( final String string )
    {
        return stringToIntList ( string, defaultSeparator );
    }

    /**
     * Returns a list of integer parts split using specified separator.
     *
     * @param string    text to split
     * @param separator text parts separator
     * @return list of split parts
     */
    public static List<Integer> stringToIntList ( final String string, final String separator )
    {
        final List<String> stringList = stringToList ( string, separator );
        if ( stringList != null )
        {
            final List<Integer> intList = new ArrayList<Integer> ( stringList.size () );
            for ( final String s : stringList )
            {
                intList.add ( Integer.parseInt ( s ) );
            }
            return intList;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns a list of float parts split using specified separator.
     *
     * @param string text to split
     * @return list of split parts
     */
    public static List<Float> stringToFloatList ( final String string )
    {
        return stringToFloatList ( string, defaultSeparator );
    }

    /**
     * Returns a list of float parts split using specified separator.
     *
     * @param string    text to split
     * @param separator text parts separator
     * @return list of split parts
     */
    public static List<Float> stringToFloatList ( final String string, final String separator )
    {
        final List<String> stringList = stringToList ( string, separator );
        if ( stringList != null )
        {
            final List<Float> intList = new ArrayList<Float> ( stringList.size () );
            for ( final String s : stringList )
            {
                intList.add ( Float.parseFloat ( s ) );
            }
            return intList;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns single text combined from list of elements using default separator.
     *
     * @param list list to combine into single text
     * @param <T>  elements type
     * @return single text combined from list of elements using default separator
     */
    public static <T> String listToString ( final List<T> list )
    {
        return listToString ( list, defaultSeparator );
    }

    /**
     * Returns single text combined from list of elements using specified separator.
     *
     * @param list      list to combine into single text
     * @param separator elements separator
     * @param <T>       elements type
     * @return single text combined from list of elements using specified separator
     */
    public static <T> String listToString ( final List<T> list, final String separator )
    {
        return listToString ( list, separator, ( Function<T, String> ) simpleTextProvider );
    }

    /**
     * Returns single text combined from list of elements using specified separator.
     *
     * @param list         list to combine into single text
     * @param separator    elements separator
     * @param textProvider {@link Function} providing text
     * @param <T>          elements type
     * @return single text combined from list of elements using specified separator
     */
    public static <T> String listToString ( final List<T> list, final String separator, final Function<T, String> textProvider )
    {
        return listToString ( list, separator, textProvider, null );
    }

    /**
     * Returns single text combined from list of elements using specified separator.
     *
     * @param list         list to combine into single text
     * @param separator    elements separator
     * @param textProvider {@link Function} providing text
     * @param filter       {@link Filter} for elements
     * @param <T>          elements type
     * @return single text combined from list of elements using specified separator
     */
    public static <T> String listToString ( final List<T> list, final String separator, final Function<T, String> textProvider,
                                            final Filter<T> filter )
    {
        if ( CollectionUtils.notEmpty ( list ) )
        {
            final StringBuilder stringBuilder = new StringBuilder ();
            boolean hasPreviouslyAccepted = false;
            for ( final T object : list )
            {
                if ( filter == null || filter.accept ( object ) )
                {
                    if ( hasPreviouslyAccepted )
                    {
                        stringBuilder.append ( separator );
                    }
                    stringBuilder.append ( textProvider.apply ( object ) );
                    hasPreviouslyAccepted = true;
                }
            }
            return stringBuilder.toString ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns single text combined from array of elements using default separator.
     *
     * @param array array to combine into single text
     * @param <T>   elements type
     * @return single text combined from array of elements using default separator
     */
    public static <T> String arrayToString ( final T... array )
    {
        return arrayToString ( defaultSeparator, array );
    }

    /**
     * Returns single text combined from array of elements using specified separator.
     *
     * @param separator elements separator
     * @param array     array to combine into single text
     * @param <T>       elements type
     * @return single text combined from array of elements using specified separator
     */
    public static <T> String arrayToString ( final String separator, final T... array )
    {
        return arrayToString ( separator, simpleTextProvider, array );
    }

    /**
     * Returns single text combined from array of elements using specified separator.
     *
     * @param separator    elements separator
     * @param textProvider {@link Function} providing text
     * @param array        array to combine into single text
     * @param <T>          elements type
     * @return single text combined from array of elements using specified separator
     */
    public static <T> String arrayToString ( final String separator, final Function<T, String> textProvider, final T... array )
    {
        return arrayToString ( separator, textProvider, null, array );
    }

    /**
     * Returns single text combined from array of elements using specified separator.
     *
     * @param separator    elements separator
     * @param textProvider {@link Function} providing text
     * @param filter       {@link Filter} for elements
     * @param array        array to combine into single text
     * @param <T>          elements type
     * @return single text combined from array of elements using specified separator
     */
    public static <T> String arrayToString ( final String separator, final Function<T, String> textProvider, final Filter<T> filter,
                                             final T... array )
    {
        if ( array != null && array.length > 0 )
        {
            final StringBuilder stringBuilder = new StringBuilder ();
            boolean hasPreviouslyAccepted = false;
            for ( final T object : array )
            {
                if ( filter == null || filter.accept ( object ) )
                {
                    if ( hasPreviouslyAccepted )
                    {
                        stringBuilder.append ( separator );
                    }
                    stringBuilder.append ( textProvider.apply ( object ) );
                    hasPreviouslyAccepted = true;
                }
            }
            return stringBuilder.toString ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Converts array of enumeration constants into string with list of enumeration constants and returns it.
     *
     * @param enumArray enumeration constants array
     * @param <E>       enumeration type
     * @return string with list of enumeration constants
     */
    public static <E extends Enum<E>> String enumArrayToString ( final E... enumArray )
    {
        return enumArrayToString ( defaultSeparator, enumArray );
    }

    /**
     * Converts array of enumeration constants into string with list of enumeration constants and returns it.
     *
     * @param separator text parts separator
     * @param enumArray enumeration constants array
     * @param <E>       enumeration type
     * @return string with list of enumeration constants
     */
    public static <E extends Enum<E>> String enumArrayToString ( final String separator, final E... enumArray )
    {
        if ( enumArray != null && enumArray.length > 0 )
        {
            final int end = enumArray.length - 1;
            final StringBuilder stringBuilder = new StringBuilder ();
            for ( int i = 0; i <= end; i++ )
            {
                stringBuilder.append ( enumArray[ i ] );
                stringBuilder.append ( i != end ? separator : "" );
            }
            return stringBuilder.toString ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Converts list of enumeration constants into string with list of enumeration constants and returns it.
     *
     * @param enumList enumeration constants list
     * @param <E>      enumeration type
     * @return string with list of enumeration constants
     */
    public static <E extends Enum<E>> String enumListToString ( final List<E> enumList )
    {
        return enumListToString ( enumList, defaultSeparator );
    }

    /**
     * Converts list of enumeration constants into string with list of enumeration constants and returns it.
     *
     * @param enumList  enumeration constants list
     * @param separator text parts separator
     * @param <E>       enumeration type
     * @return string with list of enumeration constants
     */
    public static <E extends Enum<E>> String enumListToString ( final List<E> enumList, final String separator )
    {
        if ( enumList != null && enumList.size () > 0 )
        {
            final int end = enumList.size () - 1;
            final StringBuilder stringBuilder = new StringBuilder ();
            for ( int i = 0; i <= end; i++ )
            {
                stringBuilder.append ( enumList.get ( i ) );
                stringBuilder.append ( i != end ? separator : "" );
            }
            return stringBuilder.toString ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Converts string with list of enumeration constants into real list of enumeration constants and returns it.
     *
     * @param enumString enumeration constants string list
     * @param enumClass  enumeration class
     * @param <E>        enumeration type
     * @return list of enumeration constants
     */
    public static <E extends Enum<E>> List<E> enumStringToList ( final String enumString, final Class<E> enumClass )
    {
        return enumStringToList ( enumString, enumClass, defaultSeparator );
    }

    /**
     * Converts string with list of enumeration constants into real list of enumeration constants and returns it.
     *
     * @param enumString enumeration constants string list
     * @param enumClass  enumeration class
     * @param separator  text parts separator
     * @param <E>        enumeration type
     * @return list of enumeration constants
     */
    public static <E extends Enum<E>> List<E> enumStringToList ( final String enumString, final Class<E> enumClass, final String separator )
    {
        final List<E> enumerations;
        if ( enumString != null )
        {
            final StringTokenizer tokenizer = new StringTokenizer ( enumString, separator, false );
            enumerations = new ArrayList<E> ();
            while ( tokenizer.hasMoreTokens () )
            {
                enumerations.add ( Enum.valueOf ( enumClass, tokenizer.nextToken ().trim () ) );
            }
        }
        else
        {
            enumerations = new ArrayList<E> ( 0 );
        }
        return enumerations;
    }

    /**
     * Converts and returns specified parts which are not null into single string.
     *
     * @param separator separator to place between parts
     * @param parts     parts to unite
     * @return united non-null parts
     */
    public static String unite ( final String separator, final String... parts )
    {
        if ( parts != null && parts.length > 0 )
        {
            final StringBuilder sb = new StringBuilder ();
            boolean hasPrevious = false;
            for ( final String part : parts )
            {
                if ( !isEmpty ( part ) )
                {
                    if ( hasPrevious )
                    {
                        sb.append ( separator );
                    }
                    sb.append ( part );
                    hasPrevious = true;
                }
            }
            return sb.toString ();
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns specified text length.
     *
     * @param text text to check
     * @return specified text length
     */
    public static int length ( final String text )
    {
        return text != null ? text.length () : 0;
    }

    /**
     * Returns whether or not specified text is {@code null} or empty.
     *
     * @param text text to check
     * @return {@code true} if specified text is {@code null} or empty, {@code false} otherwise
     */
    public static boolean isEmpty ( final String text )
    {
        return text == null || text.isEmpty ();
    }

    /**
     * Returns whether or not specified text is {@code null} or empty.
     *
     * @param text text to check
     * @return {@code true} if specified text is not {@code null} or empty, {@code false} otherwise
     */
    public static boolean notEmpty ( final String text )
    {
        return !isEmpty ( text );
    }

    /**
     * Returns whether or not specified text is {@code null} or empty excluding linebreaks and whitespaces.
     *
     * @param text text to check
     * @return {@code true} if specified text is {@code null} or empty excluding linebreaks and whitespaces, {@code false} otherwise
     */
    public static boolean isBlank ( final String text )
    {
        return text == null || text.isEmpty () || removeLineBreaks ( text ).trim ().isEmpty ();
    }

    /**
     * Returns whether or not specified text is {@code null} or empty excluding linebreaks and whitespaces.
     *
     * @param text text to check
     * @return {@code true} if specified text is not {@code null} or empty excluding linebreaks and whitespaces, {@code false} otherwise
     */
    public static boolean notBlank ( final String text )
    {
        return !isBlank ( text );
    }

    /**
     * Checks that the specified text is not {@code null} or empty and throws a {@link NullPointerException} if it is.
     *
     * @param text text to check for being {@code null} or empty
     * @return text if not {@code null} or empty
     * @throws NullPointerException if text is {@code null} or empty
     */
    public static String requireNonEmpty ( final String text )
    {
        return requireNonEmpty ( text, "Text must not be empty" );
    }

    /**
     * Checks that the specified text is not {@code null} or empty and throws a {@link NullPointerException} if it is.
     *
     * @param text    text to check for being {@code null} or empty
     * @param message detailed message used in {@link NullPointerException}
     * @return text if not {@code null} or empty
     * @throws NullPointerException if text is {@code null} or empty
     */
    public static String requireNonEmpty ( final String text, final String message )
    {
        if ( text == null )
        {
            throw new NullPointerException (
                    LM.contains ( message ) ? LM.get ( message ) : message
            );
        }
        return text;
    }

    /**
     * Checks that the specified text is not {@code null} or empty and throws a {@link RuntimeException} if it is.
     *
     * @param text              text to check for being {@code null} or empty
     * @param exceptionSupplier {@link Supplier} for a customized {@link RuntimeException}
     * @return text if not {@code null} or empty
     * @throws RuntimeException if text is {@code null} or empty
     */
    public static String requireNonEmpty ( final String text, final Supplier<RuntimeException> exceptionSupplier )
    {
        if ( text == null )
        {
            throw exceptionSupplier.get ();
        }
        return text;
    }

    /**
     * Creates new string filled with specified amount of same characters.
     *
     * @param character character to fill string with
     * @param length    string length
     * @return new string filled with specified amount of same characters
     */
    public static String createString ( final String character, final int length )
    {
        return createString ( character.charAt ( 0 ), length );
    }

    /**
     * Creates new string filled with specified amount of same characters.
     *
     * @param character character to fill string with
     * @param length    string length
     * @return new string filled with specified amount of same characters
     */
    public static String createString ( final char character, final int length )
    {
        final char[] characters = new char[ length ];
        Arrays.fill ( characters, character );
        return new String ( characters );
    }

    /**
     * Replaces all occurrences of str found in the specified text with provided text.
     *
     * @param text       text to replace string occurrences in
     * @param ignoreCase whether should ignore case while searching for occurrences or not
     * @param str        text to replace
     * @param replacer   text replacement {@link Function}
     * @return text with replaced occurrences of the specified str
     */
    public static String replaceAll ( final String text, final boolean ignoreCase, final String str,
                                      final Function<String, String> replacer )
    {
        final String exp = ignoreCase ? str.toLowerCase ( Locale.ROOT ) : str;
        int match = 0;
        int prev = 0;
        final StringBuilder builder = new StringBuilder ( text.length () );
        for ( int i = 0; i < text.length (); i++ )
        {
            final char ch = text.charAt ( i );
            if ( exp.charAt ( match ) == ( ignoreCase ? Character.toLowerCase ( ch ) : ch ) )
            {
                match++;
            }
            else if ( exp.charAt ( 0 ) == ( ignoreCase ? Character.toLowerCase ( ch ) : ch ) )
            {
                match = 1;
            }
            else
            {
                match = 0;
            }
            if ( match == exp.length () )
            {
                final int start = i - exp.length () + 1;
                final String part = text.substring ( start, start + exp.length () );
                builder.append ( text.substring ( prev, start ) );
                builder.append ( replacer.apply ( part ) );
                prev = start + exp.length ();
                match = 0;
            }
            else if ( i == text.length () - 1 )
            {
                builder.append ( text.substring ( prev ) );
            }
        }
        return builder.toString ();
    }

    /**
     * Returns random ID with default prefix and suffix.
     *
     * @return ID
     */
    public static String generateId ()
    {
        return generateId ( null, null );
    }

    /**
     * Returns random ID with specified prefix and default suffix.
     *
     * @param prefix id prefix
     * @return ID
     */
    public static String generateId ( final String prefix )
    {
        return generateId ( prefix, null );
    }

    /**
     * Returns random ID with specified prefix and suffix.
     *
     * @param prefix id prefix
     * @param suffix id suffix
     * @return ID
     */
    public static String generateId ( final String prefix, final String suffix )
    {
        return ( prefix == null ? defaultIdPrefix : prefix ) + "-" + generateId ( idPartLength ) + "-" + generateId ( idPartLength ) + "-" +
                generateId ( idPartLength ) + "-" + generateId ( idPartLength ) + "-" + ( suffix == null ? defaultIdSuffix : suffix );
    }

    /**
     * Returns randomly generated ID part with specified length.
     *
     * @param length part length in symbols
     * @return ID part
     */
    public static String generateId ( final int length )
    {
        final StringBuilder stringBuilder = new StringBuilder ( length );
        for ( int i = 0; i < length; i++ )
        {
            char next = 0;
            int range = 10;
            switch ( MathUtils.random ( 3 ) )
            {
                case 0:
                {
                    next = '0';
                    range = 10;
                    break;
                }
                case 1:
                {
                    next = 'a';
                    range = 26;
                    break;
                }
                case 2:
                {
                    next = 'A';
                    range = 26;
                    break;
                }
            }
            stringBuilder.append ( ( char ) ( MathUtils.random ( range ) + next ) );
        }
        return stringBuilder.toString ();
    }

    /**
     * Returns settings combined into a single key.
     * This method might be useful for generating complex cache keys.
     *
     * @param settings settings to combine
     * @return key for the specified shape settings
     */
    public static String getSettingsKey ( final Object... settings )
    {
        final StringBuilder stringBuilder = new StringBuilder ();
        for ( final Object object : settings )
        {
            if ( stringBuilder.length () > 0 )
            {
                stringBuilder.append ( ";" );
            }
            if ( object != null )
            {
                if ( object.getClass ().isArray () )
                {
                    final int length = Array.getLength ( object );
                    for ( int i = 0; i < length; i++ )
                    {
                        if ( i > 0 )
                        {
                            stringBuilder.append ( ";" );
                        }
                        stringBuilder.append ( getSettingsKey ( Array.get ( object, i ) ) );
                    }
                }
                else if ( object instanceof Collection )
                {
                    final Collection collection = ( Collection ) object;
                    stringBuilder.append ( getSettingsKey ( collection.toArray () ) );
                }
                else
                {
                    stringBuilder.append ( getSettingKey ( object ) );
                }
            }
            else
            {
                stringBuilder.append ( getSettingKey ( null ) );
            }
        }
        return stringBuilder.toString ();
    }

    /**
     * Returns setting string representation.
     *
     * @param setting setting to be converted
     * @return setting string representation
     */
    private static String getSettingKey ( final Object setting )
    {
        if ( setting == null )
        {
            return "null";
        }
        else if ( setting instanceof Insets )
        {
            return InsetsConverter.insetsToString ( ( Insets ) setting );
        }
        else if ( setting instanceof Rectangle )
        {
            return RectangleConverter.rectangleToString ( ( Rectangle ) setting );
        }
        else if ( setting instanceof Point )
        {
            return PointConverter.pointToString ( ( Point ) setting );
        }
        else if ( setting instanceof Color )
        {
            return ColorConverter.colorToString ( ( Color ) setting );
        }
        else
        {
            return setting.toString ();
        }
    }
}