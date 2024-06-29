package ru.leymooo.botfilter.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.ByteTag;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.DoubleTag;
import se.llbit.nbt.FloatTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.LongTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

//Author: CatCoder, https://vk.com/catcoder
//Updated by: BoomEaro
@RequiredArgsConstructor
@Getter
public class Dimension
{
    static CompoundTag damageType;
    static CompoundTag damageType1_20;
    static CompoundTag damageType1_21;

    static
    {
        try
        {
            damageType = (CompoundTag) CompoundTag.read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.19.4.nbt" ) ) ) ) ).get( "" );
            damageType1_20 = (CompoundTag) CompoundTag.read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.20.nbt" ) ) ) ) ).get( "" );
            damageType1_21 = (CompoundTag) CompoundTag.read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.20.nbt" ) ) ) ) ).get( "" );

            ListTag list = damageType1_21.get( "value" ).asList();
            CompoundTag campfire = new CompoundTag();
            campfire.add( "name", new StringTag( "minecraft:campfire" ));
            campfire.add( "id", new IntTag( 44 ));

            CompoundTag element = new CompoundTag();
            element.add("scaling", new StringTag( "when_caused_by_living_non_player" ));
            element.add("message_id", new StringTag( "inFire" ));
            element.add("exhaustion", new FloatTag( 0.1f ));
            campfire.add( "element", element );
            list.add( campfire );
        } catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static Dimension OVERWORLD = new Dimension( "minecraft:overworld", 0, 0, false, true, 0.0f,
        "minecraft:infiniburn_overworld", false, true, true,
        "minecraft:overworld", true, 0, 0,
        256, 1.0f, false, false, 0, 256, Arrays.asList( Biome.PLAINS, Biome.SWAMP, Biome.SWAMP_HILLS ) );
    public static Dimension THE_NETHER = new Dimension( "minecraft:the_nether", -1, 1, false, true, 0.0f,
        "minecraft:infiniburn_nether", false, true, true,
        "minecraft:the_nether", true, 0, 0,
        256, 1.0f, false, false, 0, 256, Arrays.asList( Biome.NETHER_WASTES ) );
    public static Dimension THE_END = new Dimension( "minecraft:the_end", 1, 2, false, true, 0.0f,
        "minecraft:infiniburn_end", false, true, true,
        "minecraft:the_end", true, 0, 0,
        256, 1.0f, false, false, 0, 256, Arrays.asList( Biome.THE_END ) );
    private final String key;
    private final int dimensionId;
    private final int id;

    private final boolean piglinSafe;

    private final boolean natural;
    private final float ambientLight;

    private final String infiniburn;
    private final boolean respawnAnchorWorks;
    private final boolean hasSkylight;
    private final boolean bedWorks;
    private final String effects;
    private final boolean hasRaids;
    private final int monster_spawn_light_level;
    private final int monster_spawn_block_light_limit;
    private final int logicalHeight;
    private final float coordinateScale;
    private final boolean ultrawarm;
    private final boolean hasCeiling;

    private final int minY;
    private final int height;

    private final List<Biome> biomes;

    @SneakyThrows
    public Tag getFullCodec(int protocolVersion)
    {
        CompoundTag attributes = encodeAttributes( protocolVersion );

        if ( protocolVersion <= ProtocolConstants.MINECRAFT_1_16_1 )
        {
            CompoundTag dimensions = new CompoundTag();
            dimensions.add( "dimension", new ListTag( Tag.TAG_COMPOUND, Collections.singletonList( attributes ) ) );

            return new NamedTag( "", dimensions );
        }

        CompoundTag dimensionData = new CompoundTag();

        dimensionData.add( "name", new StringTag( key ) );
        dimensionData.add( "id", new IntTag( id ) );
        dimensionData.add( "element", attributes );

        CompoundTag dimensions = new CompoundTag();
        dimensions.add( "type", new StringTag( "minecraft:dimension_type" ) );
        dimensions.add( "value", new ListTag( Tag.TAG_COMPOUND, Collections.singletonList( dimensionData ) ) );

        CompoundTag root = new CompoundTag();
        root.add( "minecraft:dimension_type", dimensions );
        root.add( "minecraft:worldgen/biome", createBiomeRegistry() );

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19_4 )
        {

            CompoundTag damage =  protocolVersion >= ProtocolConstants.MINECRAFT_1_20 ? damageType1_20 : damageType;

            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_21) {
                damage = damageType1_21;
            }


            root.add( "minecraft:damage_type",  damage);
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            root.add( "minecraft:chat_type", createChatRegistry( protocolVersion ) );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21 )
        {
            root.add( "minecraft:painting_variant", createPaintingVariant( protocolVersion ) );
            root.add( "minecraft:wolf_variant", createWoldVariant( protocolVersion ) );
        }

        return protocolVersion >= ProtocolConstants.MINECRAFT_1_20_2 ? root : new NamedTag( "", root );
    }

    public Tag getAttributes(int protocolVersion)
    {
        return new NamedTag( "", encodeAttributes( protocolVersion ) );
    }

    private CompoundTag encodeAttributes(int protocolVersion)
    {
        Map<String, SpecificTag> attributes = new HashMap<>();

        // 1.16 - 1.16.1
        attributes.put( "name", new StringTag( key ) );
        //
        attributes.put( "natural", new ByteTag( natural ? 1 : 0 ) );
        attributes.put( "has_skylight", new ByteTag( hasSkylight ? 1 : 0 ) );
        attributes.put( "has_ceiling", new ByteTag( hasCeiling ? 1 : 0 ) );
        // 1.16 - 1.16.1
        attributes.put( "fixed_time", new LongTag( 10_000 ) );
        attributes.put( "shrunk", new ByteTag( 0 ) );
        //
        attributes.put( "ambient_light", new FloatTag( ambientLight ) );
        attributes.put( "ultrawarm", new ByteTag( ultrawarm ? 1 : 0 ) );
        attributes.put( "has_raids", new ByteTag( hasRaids ? 1 : 0 ) );
        attributes.put( "respawn_anchor_works", new ByteTag( respawnAnchorWorks ? 1 : 0 ) );
        attributes.put( "bed_works", new ByteTag( bedWorks ? 1 : 0 ) );
        attributes.put( "piglin_safe", new ByteTag( piglinSafe ? 1 : 0 ) );
        attributes.put( "infiniburn", new StringTag( infiniburn ) );
        attributes.put( "logical_height", new ByteTag( logicalHeight ) );

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2 )
        {
            attributes.remove( "name" ); // removed
            attributes.remove( "fixed_time" ); // removed
            attributes.remove( "shrunk" ); // removed

            attributes.put( "effects", new StringTag( effects ) ); // added
            attributes.put( "coordinate_scale", new FloatTag( coordinateScale ) ); // added
        }

        attributes.put( "height", new IntTag( height ) );
        attributes.put( "min_y", new IntTag( minY ) );
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_18_2 )
        {
            attributes.put( "infiniburn", new StringTag( "#" + infiniburn ) ); // added
        }

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            attributes.put( "monster_spawn_light_level", new IntTag( monster_spawn_light_level ) );
            attributes.put( "monster_spawn_block_light_limit", new IntTag( monster_spawn_block_light_limit ) );
        }
        CompoundTag tag = new CompoundTag();

        for ( Map.Entry<String, SpecificTag> entry : attributes.entrySet() )
        {
            tag.add( entry.getKey(), entry.getValue() );
        }

        return tag;
    }

    private CompoundTag createBiomeRegistry()
    {
        CompoundTag root = new CompoundTag();
        root.add( "type", new StringTag( "minecraft:worldgen/biome" ) );
        List<CompoundTag> biomes = new ArrayList<>();
        for ( Biome biome : this.biomes )
        {
            biomes.add( encodeBiome( biome ) );
        }
        root.add( "value", new ListTag( Tag.TAG_COMPOUND, biomes ) );
        return root;
    }

    private CompoundTag createChatRegistry(int version)
    {

        CompoundTag root = new CompoundTag();
        root.add( "type", new StringTag( "minecraft:chat_type" ) );
        CompoundTag systemChat = new CompoundTag();
        systemChat.add( "name", new StringTag( "minecraft:system" ) );
        systemChat.add( "id", new IntTag( 1 ) );
        CompoundTag element = new CompoundTag();


        CompoundTag chat = new CompoundTag();
        if ( version >= ProtocolConstants.MINECRAFT_1_19_1 )
        {
            chat.add( "style", new CompoundTag() );
            chat.add( "translation_key", new StringTag( "chat.type.system" ) );
            chat.add( "parameters", new ListTag( Tag.TAG_STRING, Arrays.asList( new StringTag( "sender" ), new StringTag( "content" ) ) ) );
        }

        element.add( "chat", chat );
        CompoundTag narration = new CompoundTag();
        if ( version >= ProtocolConstants.MINECRAFT_1_19_1 )
        {
            narration.add( "style", new CompoundTag() );
            narration.add( "translation_key", new StringTag( "chat.type.system.narrate" ) );
            narration.add( "parameters", new ListTag( Tag.TAG_STRING, Arrays.asList( new StringTag( "sender" ), new StringTag( "content" ) ) ) );
        } else
        {
            narration.add( "priority", new StringTag( "system" ) );
        }
        element.add( "narration", narration );
        systemChat.add( "element", element );
        root.add( "value", new ListTag( Tag.TAG_COMPOUND, Arrays.asList( systemChat ) ) );
        return root;
    }


    private CompoundTag createPaintingVariant(int version)
    {

        CompoundTag root = new CompoundTag();
        root.add( "type", new StringTag( "minecraft:painting_variant" ) );
        CompoundTag alban = new CompoundTag();
        alban.add( "name", new StringTag( "minecraft:alban" ) );
        alban.add( "id", new IntTag( 0 ) );

        CompoundTag paintingVariant = new CompoundTag();
        paintingVariant.add("width", new IntTag( 1 ));
        paintingVariant.add("height", new IntTag( 1 ));
        paintingVariant.add("asset_id", new StringTag( "minecraft:alban" ));

        alban.add( "element", paintingVariant );

        root.add( "value", new ListTag( Tag.TAG_COMPOUND, Arrays.asList( alban ) ) );
        return root;
    }

    private CompoundTag createWoldVariant(int version)
    {

        CompoundTag root = new CompoundTag();
        root.add( "type", new StringTag( "minecraft:wolf_variant" ) );
        CompoundTag ashen = new CompoundTag();
        ashen.add( "name", new StringTag( "minecraft:ashen" ) );
        ashen.add( "id", new IntTag( 0 ) );

        CompoundTag ashenProperties = new CompoundTag();
        ashenProperties.add("wild_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen" ));
        ashenProperties.add("tame_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen_tame" ));
        ashenProperties.add("angry_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen_angry" ));
        ashenProperties.add("biomes", new ListTag(Tag.TAG_STRING, Arrays.asList( new StringTag( "minecraft:plains" ) ) ));

        ashen.add( "element", ashenProperties );

        root.add( "value", new ListTag( Tag.TAG_COMPOUND, Arrays.asList( ashen ) ) );
        return root;
    }


    private CompoundTag encodeBiome(Biome biome)
    {
        CompoundTag biomeTag = new CompoundTag();

        biomeTag.add( "name", new StringTag( biome.getName() ) );
        biomeTag.add( "id", new IntTag( biome.getId() ) );

        CompoundTag element = new CompoundTag();
        element.add( "precipitation", new StringTag( biome.getPrecipitation() ) );

        element.add( "has_precipitation", new ByteTag( biome.getPrecipitation().equals( "none" ) ? 0 : 1 ) );

        element.add( "depth", new FloatTag( biome.getDepth() ) );
        element.add( "temperature", new FloatTag( biome.getTemperature() ) );
        element.add( "scale", new FloatTag( biome.getScale() ) );
        element.add( "downfall", new FloatTag( biome.getDownfall() ) );
        element.add( "category", new StringTag( biome.getCategory() ) );

        CompoundTag effects = new CompoundTag();
        effects.add( "sky_color", new IntTag( biome.getSky_color() ) );
        effects.add( "water_fog_color", new IntTag( biome.getWater_color() ) );
        effects.add( "fog_color", new IntTag( biome.getFog_color() ) );
        effects.add( "water_color", new IntTag( biome.getWater_color() ) );
        if ( biome.getGrass_color_modiefer() != null )
        {
            effects.add( "grass_color_modifier", new StringTag( biome.getGrass_color_modiefer() ) );
        }
        if ( biome.getFoliage_color() != Integer.MIN_VALUE )
        {
            effects.add( "foliage_color", new IntTag( biome.getFoliage_color() ) );
        }

        CompoundTag moodSound = new CompoundTag();
        moodSound.add( "tick_delay", new IntTag( biome.getTick_delay() ) );
        moodSound.add( "offset", new DoubleTag( biome.getOffset() ) );
        moodSound.add( "block_search_extent", new IntTag( biome.getBlock_search_extent() ) );
        moodSound.add( "sound", new StringTag( biome.getSound() ) );

        effects.add( "mood_sound", moodSound );

        element.add( "effects", effects );
        biomeTag.add( "element", element );
        return biomeTag;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Biome
    {
        PLAINS( "minecraft:plains", 0, "rain", 0.125f, 0.8f, 0.05f,
            0.4f, "plains", 7907327, 329011, 12638463,
            4159204, 6000, 2.0d, 8, "minecraft:ambient.cave",
            null, Integer.MIN_VALUE ),
        SWAMP( "minecraft:swamp", 1, "rain", -0.2f, 0.8f, 0.1f, 0.9f,
            "swamp", 7907327, 2302743, 12638463, 6388580,
            6000, 2.0d, 8, "minecraft:ambient.cave", "swamp",
            6975545 ),
        SWAMP_HILLS( "minecraft:swamp_hills", 2, "rain", -0.1f, 0.8f, 0.3f,
            0.9f, "swamp", 7907327, 2302743, 12638463,
            6388580, 6000, 2.0d, 8, "minecraft:ambient.cave",
            "swamp", 6975545 ),
        NETHER_WASTES( "minecraft:nether_wastes", 3, "none", 0.1f, 2.0f, 0.2f,
            0.0f, "nether", 7254527, 329011, 3344392,
            4159204, 6000, 2.0d, 8, "minecraft:ambient.cave",
            "swamp", 6975545 ),
        THE_END( "minecraft:the_end", 4, "none", 0.1f, 0.5f, 0.2f,
            0.5f, "the_end", 7907327, 10518688, 12638463,
            4159204, 6000, 2.0d, 8, "minecraft:ambient.cave",
            "swamp", 6975545 );
        private final String name;
        private final int id;
        //elements
        private final String precipitation;
        private final float depth;
        private final float temperature;
        private final float scale;
        private final float downfall;
        private final String category;
        //effects
        private final int sky_color;
        private final int water_fog_color;
        private final int fog_color;
        private final int water_color;
        //mood sound
        private final int tick_delay;
        private final double offset;
        private final int block_search_extent;
        private final String sound;
        private final String grass_color_modiefer;
        private final int foliage_color;
    }
}
