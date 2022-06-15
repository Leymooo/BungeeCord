package ru.leymooo.botfilter.captcha;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.BungeeCord;
import ru.leymooo.botfilter.caching.CachedCaptcha;
import ru.leymooo.botfilter.caching.PacketUtils;
import ru.leymooo.botfilter.captcha.generator.map.MapPalette;
import ru.leymooo.botfilter.config.Settings;

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
        Thread thread = new Thread( CaptchaGeneration::generateCaptchas );
        thread.setName( "CaptchaGenerationProvider-thread" );
        thread.setPriority( Thread.MIN_PRIORITY );
        thread.start();
    }
    private static void generateCaptchas()
    {
        try
        {
            List<Font> fonts = Arrays.asList(
                    new Font( Font.SANS_SERIF, Font.PLAIN, 50 ),
                    new Font( Font.SERIF, Font.PLAIN, 50 ),
                    new Font( Font.MONOSPACED, Font.BOLD, 50 ) );
            //Перед началом генерации, нужно удалить старую капчу, освободив байтовый буфер.
            PacketUtils.captchas.clear();
            BungeeCord.getInstance().getLogger().log( Level.INFO, "[BotFilter] " + ( BungeeCord.getInstance().isEnabled() ? "Начата генерация капчи в фоне." : "Генерация капчи продолжится параллельно с загрузкой BungeeCord." ) );
            ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors(),
                    new ThreadFactoryBuilder()
                            .setPriority( Thread.MIN_PRIORITY )
                            .setNameFormat( "CaptchaGenerationTask-thread-%d" )
                            .build() );
            MapPalette.prepareColors();

            int captchaCount = Settings.IMP.CAPTCHA.COUNT;

            if ( captchaCount <= 0 )
            {
                captchaCount = 1;
            }

            List<CachedCaptcha.CaptchaHolder> holders = Collections.synchronizedList( new ArrayList<>() );

            for ( int i = 1; i <= captchaCount; i++ )
            {
                executor.execute( new CaptchaGenerationTask( executor, fonts, holders ) );
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

                //Текущий список всех капч каждую секунду вставляем в новый список для гарантии потокобезопасности.
                PacketUtils.captchas.setCaptchas( new ArrayList<>( holders ) );
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
            PacketUtils.captchas.setCaptchas( new ArrayList<>( holders ) );
            System.gc();
            BungeeCord.getInstance().getLogger().log( Level.INFO, "[BotFilter] Капча сгенерирована за {0} мс", System.currentTimeMillis() - start );
        } catch ( Exception e )
        {
            e.printStackTrace();
        } finally
        {
            generation = false;
        }
    }
}
