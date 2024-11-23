package ru.leymooo.botfilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;
import ru.leymooo.botfilter.packets.Player;
import ru.leymooo.botfilter.packets.PlayerPosition;
import ru.leymooo.botfilter.packets.PlayerPositionAndLook;
import ru.leymooo.botfilter.packets.TeleportConfirm;

/**
 * @author Leymooo
 */
@RequiredArgsConstructor
@ToString
public class MoveHandler extends PacketHandler
{

    private static final int TELEPORT_ID = 9876;

    @Getter
    protected final int version;

    public double x = 0;
    public double y = 0;
    public double z = 0;
    public boolean onGround = false;
    public int waitingTeleportId = TELEPORT_ID;

    public double lastY = 0;
    public int ticks = 0;

    private PlayerPositionAndLook lastCapturedPacket;


    @Override
    public void handle(Player player) throws Exception
    {
        this.onGround = player.isOnGround();
        if ( waitingTeleportId == TELEPORT_ID )
        {
            return;
        }
        onMove();
    }

    @Override
    public void handle(PlayerPosition pos) throws Exception
    {
        if ( waitingTeleportId == TELEPORT_ID )
        {
            return;
        }
        x = pos.getX();
        lastY = y;
        y = pos.getY();
        z = pos.getZ();
        onGround = pos.isOnGround();
        onMove();
    }

    @Override
    public void handle(PlayerPositionAndLook posRot) throws Exception
    {

        if ( version == ProtocolConstants.MINECRAFT_1_8 && waitingTeleportId == TELEPORT_ID && posRot.getX() == 7 && posRot.getY() == 450 && posRot.getZ() == 7 )
        {
            onTeleportConfirm();
        }

        if ( waitingTeleportId == TELEPORT_ID )
        {
            //discard packets if in teleport
            lastCapturedPacket = posRot;
            return;
        }

        x = posRot.getX();
        lastY = y;
        y = posRot.getY();
        z = posRot.getZ();
        onGround = posRot.isOnGround();
        onMove();
    }

    @Override
    public void handle(TeleportConfirm confirm) throws Exception
    {
        if ( confirm.getTeleportId() == waitingTeleportId )
        {
            onTeleportConfirm();
        }
    }

    private void onTeleportConfirm() throws Exception
    {
        ticks = 0;
        y = -1;
        lastY = -1;
        waitingTeleportId = -1;
        if ( version >= ProtocolConstants.MINECRAFT_1_21_2 && lastCapturedPacket != null )
        {
            handle( lastCapturedPacket );
        }
    }

    public void onMove()
    {
        throw new UnsupportedOperationException( "Method is not overrided" );
    }

    public static double getSpeed(int ticks)
    {
        return formatDouble( -( ( Math.pow( 0.98, ticks ) - 1 ) * 3.92 ) );
    }

    public static double formatDouble(double d)
    {
        return Math.floor( d * 100 ) / 100;
    }
}
