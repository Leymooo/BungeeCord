package net.md_5.bungee;

public class Bootstrap
{

    public static void main(String[] args) throws Exception
    {
        if ( Float.parseFloat( System.getProperty( "java.class.version" ) ) < 61.0 )
        {
            System.err.println( "*** ОШИБОЧКА *** БотФильтеру нужна Java 17. Установите её, что бы запустить сервер!" ); //BotFilter
            System.out.println( "Проверить версию: java -version" ); //BotFilter
            return;
        }

        BungeeCordLauncher.main( args );
    }
}
