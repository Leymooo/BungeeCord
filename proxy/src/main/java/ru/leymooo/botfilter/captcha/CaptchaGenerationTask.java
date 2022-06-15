package ru.leymooo.botfilter.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.leymooo.botfilter.caching.CachedCaptcha;
import ru.leymooo.botfilter.captcha.generator.CaptchaPainter;
import ru.leymooo.botfilter.captcha.generator.map.CraftMapCanvas;
import ru.leymooo.botfilter.captcha.generator.map.MapPalette;
import ru.leymooo.botfilter.packets.MapDataPacket;

@Data
@AllArgsConstructor
public class CaptchaGenerationTask implements Runnable
{
    private final ExecutorService executor;
    private final CaptchaPainter painter;
    private final List<Font> fonts;
    private final List<CachedCaptcha.CaptchaHolder> holders;
    @Override
    public void run()
    {
        try
        {
            Random rnd = ThreadLocalRandom.current();
            String answer = randomAnswer( rnd );
            BufferedImage image = this.painter.draw( this.fonts.get( rnd.nextInt( this.fonts.size() ) ), randomNotWhiteColor( rnd ), answer );
            final CraftMapCanvas map = new CraftMapCanvas();
            map.drawImage( 0, 0, image );
            MapDataPacket packet = new MapDataPacket( 0, (byte) 0, map.getMapData() );
            CachedCaptcha.CaptchaHolder holder = CachedCaptcha.createCaptchaPacket( packet, answer );
            this.holders.add( holder );
        } catch ( Throwable e )
        {
            //Прекращаем генерацию если случилась любая ошибка
            e.printStackTrace();
            this.executor.shutdownNow();
        }
    }

    private static Color randomNotWhiteColor(Random rnd)
    {
        Color color = MapPalette.colors[rnd.nextInt( MapPalette.colors.length )];

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        if ( r == 255 && g == 255 && b == 255 )
        {
            return randomNotWhiteColor( rnd );
        }
        if ( r == 220 && g == 220 && b == 220 )
        {
            return randomNotWhiteColor( rnd );
        }
        if ( r == 199 && g == 199 && b == 199 )
        {
            return randomNotWhiteColor( rnd );
        }
        if ( r == 255 && g == 252 && b == 245 )
        {
            return randomNotWhiteColor( rnd );
        }
        if ( r == 220 && g == 217 && b == 211 )
        {
            return randomNotWhiteColor( rnd );
        }
        if ( r == 247 && g == 233 && b == 163 )
        {
            return randomNotWhiteColor( rnd );
        }
        return color;
    }

    private static String randomAnswer(Random rnd)
    {
        if ( rnd.nextBoolean() )
        {
            return Integer.toString( rnd.nextInt( ( 99999 - 10000 ) + 1 ) + 10000 );
        } else
        {
            return Integer.toString( rnd.nextInt( ( 9999 - 1000 ) + 1 ) + 1000 );
        }
    }
}
