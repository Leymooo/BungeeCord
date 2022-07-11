package ru.leymooo.botfilter.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

//Оригинал: https://github.com/filoghost/FCommons/blob/master/src/main/java/me/filoghost/fcommons/Colors.java
@UtilityClass
public class ColorsUtils
{
    private static final char ALT_COLOR_CHAR = '&';
    private static final CharArray ALT_COLOR_CODES = new CharArray( "0123456789AaBbCcDdEeFfKkLlMmNnOoRr" );
    private static final CharArray ALT_HEX_CODES = new CharArray( "0123456789AaBbCcDdEeFf" );
    private static final int ALT_HEX_COLOR_LENGTH = 6;
    public static String serializeTextWithColorToJson(String text)
    {
        return ComponentSerializer.toString( TextComponent.fromLegacyText( colorize( text ) ) );
    }
    public static String colorize(String string)
    {
        if ( isEmpty( string ) || string.indexOf( ALT_COLOR_CHAR ) < 0 )
        {
            return string;
        }

        StringBuilder result = new StringBuilder( string.length() );

        int i = 0;
        while ( i < string.length() )
        {
            char currentChar = string.charAt( i );

            if ( currentChar == ALT_COLOR_CHAR && i + 1 < string.length() )
            {
                char nextChar = string.charAt( i + 1 );

                if ( nextChar == '#' && isAltHexColor( string, i + 2 ) )
                {
                    result.append( ChatColor.COLOR_CHAR );
                    result.append( 'x' );
                    translateAltHexColor( string, i + 2, result );

                    i += 2 + ALT_HEX_COLOR_LENGTH; // Skip prefix and hex string
                    continue;
                }

                if ( ALT_COLOR_CODES.contains( nextChar ) )
                {
                    result.append( ChatColor.COLOR_CHAR );
                    result.append( Character.toLowerCase( nextChar ) );

                    i += 2; // Skip color char and color code
                    continue;
                }
            }

            // Normal char
            result.append( currentChar );
            i++;
        }

        return result.toString();
    }

    private static boolean isEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    private static boolean isAltHexColor(String string, int beginIndex)
    {
        if ( string.length() - beginIndex < ALT_HEX_COLOR_LENGTH )
        {
            return false;
        }

        for ( int i = 0; i < ALT_HEX_COLOR_LENGTH; i++ )
        {
            char hexCode = string.charAt( beginIndex + i );
            if ( !ALT_HEX_CODES.contains( hexCode ) )
            {
                return false;
            }
        }

        return true;
    }

    private static void translateAltHexColor(String string, int beginIndex, StringBuilder output)
    {
        for ( int i = 0; i < ALT_HEX_COLOR_LENGTH; i++ )
        {
            char hexCode = string.charAt( beginIndex + i );
            output.append( ChatColor.COLOR_CHAR );
            output.append( Character.toLowerCase( hexCode ) );
        }
    }

    private static class CharArray
    {
        private final char[] chars;
        CharArray(String chars)
        {
            this.chars = chars.toCharArray();
        }

        boolean contains(char c)
        {
            for ( char element : chars )
            {
                if ( c == element )
                {
                    return true;
                }
            }
            return false;
        }
    }
}
