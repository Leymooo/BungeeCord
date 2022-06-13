package ru.leymooo.botfilter.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.BungeeCord;
import ru.leymooo.botfilter.caching.CachedCaptcha;
import ru.leymooo.botfilter.caching.PacketUtils;
import ru.leymooo.botfilter.captcha.generator.CaptchaPainter;
import ru.leymooo.botfilter.captcha.generator.map.CraftMapCanvas;
import ru.leymooo.botfilter.captcha.generator.map.MapPalette;
import ru.leymooo.botfilter.packets.MapDataPacket;

/**
 * @author Leymooo
 */
@UtilityClass
public class CaptchaGeneration
{
    private static volatile boolean generation = false;

    public static synchronized void generateImages() throws CaptchaGenerationException
    {
        if ( generation )
        {
            throw new CaptchaGenerationException( "Капча уже генерируется!" );
        }

        generation = true;

        List<Font> fonts = Arrays.asList(
                new Font( Font.SANS_SERIF, Font.PLAIN, 50 ),
                new Font( Font.SERIF, Font.PLAIN, 50 ),
                new Font( Font.MONOSPACED, Font.BOLD, 50 ) );
        Thread thread = new Thread( () ->
        {
            try
            {
                //Перед началом генерации, нужно удалить старую капчу, освободив байтовый буфер.
                PacketUtils.captchas.clearCaptcha();
                BungeeCord.getInstance().getLogger().log( Level.INFO, "[BotFilter] " + ( BungeeCord.getInstance().isEnabled() ? "Начата генерация капчи в фоне." : "Генерация капчи продолжится параллельно с загрузкой BungeeCord." ) );
                ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors(),
                        new CaptchaThreadFactory() );
                CaptchaPainter painter = new CaptchaPainter();
                MapPalette.prepareColors();

                int captchaCount = 900;
                try
                {
                    String captchaCountString = System.getProperty( "captchaCount" );
                    if ( captchaCountString != null && !captchaCountString.isEmpty() )
                    {
                        captchaCount = Integer.parseInt( captchaCountString );
                    }
                } catch ( Exception ignored )
                {
                }

                if ( captchaCount <= 0 )
                {
                    captchaCount = 1;
                }

                List<CachedCaptcha.CaptchaHolder> holders = Collections.synchronizedList( new ArrayList<>() );

                for ( int i = 1; i <= captchaCount; i++ )
                {
                    executor.execute( () ->
                    {
                        try
                        {
                            Random rnd = ThreadLocalRandom.current();
                            String answer = randomAnswer( rnd );
                            BufferedImage image = painter.draw( fonts.get( rnd.nextInt( fonts.size() ) ), randomNotWhiteColor( rnd ), answer );
                            final CraftMapCanvas map = new CraftMapCanvas();
                            map.drawImage( 0, 0, image );
                            MapDataPacket packet = new MapDataPacket( 0, (byte) 0, map.getMapData() );
                            CachedCaptcha.CaptchaHolder holder = CachedCaptcha.createCaptchaPacket( packet, answer );
                            holders.add( holder );
                        } catch ( Throwable e )
                        {
                            //Прекращаем генерацию если случилась любая ошибка
                            e.printStackTrace();
                            executor.shutdownNow();
                        }
                    } );
                }

                long start = System.currentTimeMillis();
                ThreadPoolExecutor ex = (ThreadPoolExecutor) executor;
                while ( ex.getActiveCount() != 0 )
                {
                    //Отображаем прогресс генерации только после полной загрузки банжи, чтобы логи не путались с другими важными логами при включении
                    if ( BungeeCord.getInstance().isEnabled() )
                    {
                        BungeeCord.getInstance().getLogger().log( Level.INFO, "[BotFilter] Генерирую капчу [" + ( captchaCount - ex.getQueue().size() ) + "/" + captchaCount + "]" );
                    }

                    //Текущий список всех капч каждую секунду переобразуем в массив и вставляем, чтобы хоть какая та часть была доступна во время генерации
                    PacketUtils.captchas.setCaptchas( holders.toArray( new CachedCaptcha.CaptchaHolder[0] ) );
                    try
                    {
                        Thread.sleep( 1000L );
                    } catch ( InterruptedException ex1 )
                    {
                        BungeeCord.getInstance().getLogger().log( Level.WARNING, "[BotFilter] Не могу сгенерировать капчу. Выключаю банджу", ex1 );
                        System.exit( 0 );
                        return;
                    }
                }

                executor.shutdownNow();

                //Окончательно устанавливаем оставшиеся капчи
                PacketUtils.captchas.setCaptchas( holders.toArray( new CachedCaptcha.CaptchaHolder[0] ) );
                System.gc();
                BungeeCord.getInstance().getLogger().log( Level.INFO, "[BotFilter] Капча сгенерирована за {0} мс", System.currentTimeMillis() - start );
            } catch ( Exception e )
            {
                e.printStackTrace();
            } finally
            {
                generation = false;
            }
        } );

        thread.setName( "CaptchaGeneration-provider-thread" );
        thread.setPriority( Thread.MIN_PRIORITY );
        thread.start();
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
    //Для правильного именования потоков которые генерируют капчу и для создания низкого приоритета
    private static class CaptchaThreadFactory implements ThreadFactory
    {
        private static final AtomicInteger counter = new AtomicInteger();
        @Override
        public Thread newThread(Runnable task)
        {
            Thread thr = new Thread( task );
            thr.setPriority( Thread.MIN_PRIORITY );
            thr.setName( "CaptchaGenerator-thread-" + counter.incrementAndGet() );
            return thr;
        }
    }
}
