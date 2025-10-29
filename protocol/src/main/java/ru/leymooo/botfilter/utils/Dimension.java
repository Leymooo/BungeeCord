package ru.leymooo.botfilter.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.nbt.NamedTag;
import net.md_5.bungee.nbt.Tag;
import net.md_5.bungee.nbt.TypedTag;
import net.md_5.bungee.nbt.limit.NBTLimiter;
import net.md_5.bungee.nbt.type.ByteTag;
import net.md_5.bungee.nbt.type.CompoundTag;
import net.md_5.bungee.nbt.type.DoubleTag;
import net.md_5.bungee.nbt.type.FloatTag;
import net.md_5.bungee.nbt.type.IntTag;
import net.md_5.bungee.nbt.type.ListTag;
import net.md_5.bungee.nbt.type.LongTag;
import net.md_5.bungee.nbt.type.StringTag;
import net.md_5.bungee.protocol.ProtocolConstants;


//Author: CatCoder, https://vk.com/catcoder
//Updated by: BoomEaro
@RequiredArgsConstructor
@Getter
public class Dimension
{
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
    static CompoundTag damageType;
    static CompoundTag damageType1_20;
    static CompoundTag damageType1_21;
    static CompoundTag damageType1_21_2;

    static
    {
        try
        {
            damageType = (CompoundTag) read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.19.4.nbt" ) ) ) ) );
            damageType1_20 = (CompoundTag) read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.20.nbt" ) ) ) ) );
            damageType1_21 = (CompoundTag) read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.20.nbt" ) ) ) ) );

            damageType1_21_2 = (CompoundTag) read(
                new DataInputStream( new BufferedInputStream( new GZIPInputStream( Dimension.class.getResourceAsStream( "/damage-types-1.20.nbt" ) ) ) ) );


            appendElementsDamageType( damageType1_21, ProtocolConstants.MINECRAFT_1_21 );
            appendElementsDamageType( damageType1_21_2, ProtocolConstants.MINECRAFT_1_21_2 );

        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

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
    //TODO: ambient_light for END dimension

    private static CompoundTag read(DataInputStream dataInputStream) throws Exception
    {
        NamedTag namedTag = new NamedTag();
        namedTag.read( dataInputStream, new NBTLimiter( 1 << 22 ) );
        return (CompoundTag) namedTag.getTag();
    }

    private static void appendElementsDamageType(CompoundTag compoundTag, int version)
    {
        ListTag list = (ListTag) compoundTag.get( "value" );
        CompoundTag campfire = new CompoundTag(new LinkedHashMap<>());
        campfire.put( "name", new StringTag( "minecraft:campfire" ) );
        campfire.put( "id", new IntTag( 44 ) );

        CompoundTag campfireData = new CompoundTag(new LinkedHashMap<>());
        campfireData.put( "scaling", new StringTag( "when_caused_by_living_non_player" ) );
        campfireData.put( "message_id", new StringTag( "inFire" ) );
        campfireData.put( "exhaustion", new FloatTag( 0.1f ) );
        campfire.put( "element", campfireData );

        list.getValue().add( campfire );


        if ( version >= ProtocolConstants.MINECRAFT_1_21_2 )
        {
            CompoundTag enderpearl = new CompoundTag(new LinkedHashMap<>());
            enderpearl.put( "name", new StringTag( "minecraft:ender_pearl" ) );
            enderpearl.put( "id", new IntTag( 45 ) );

            CompoundTag maceSmash = new CompoundTag(new LinkedHashMap<>());
            maceSmash.put( "name", new StringTag( "minecraft:mace_smash" ) );
            maceSmash.put( "id", new IntTag( 46 ) );

            CompoundTag enderpearlData = new CompoundTag(new LinkedHashMap<>());
            enderpearlData.put( "scaling", new StringTag( "when_caused_by_living_non_player" ) );
            enderpearlData.put( "message_id", new StringTag( "fall" ) );
            enderpearlData.put( "exhaustion", new FloatTag( 0.0f ) );

            CompoundTag maceSmashData = new CompoundTag(new LinkedHashMap<>());
            maceSmashData.put( "scaling", new StringTag( "when_caused_by_living_non_player" ) );
            maceSmashData.put( "message_id", new StringTag( "mace_smash" ) );
            maceSmashData.put( "exhaustion", new FloatTag( 0.1f ) );

            enderpearl.put( "element", enderpearlData );
            maceSmash.put( "element", maceSmashData );
            list.getValue().add( enderpearl );
            list.getValue().add( maceSmash );
        }
    }

    @SneakyThrows
    public Tag getFullCodec(int protocolVersion)
    {
        CompoundTag attributes = encodeAttributes( protocolVersion );

        if ( protocolVersion <= ProtocolConstants.MINECRAFT_1_16_1 )
        {
            CompoundTag dimensions = new CompoundTag(new LinkedHashMap<>());
            dimensions.put( "dimension", new ListTag( Collections.singletonList( attributes ), Tag.COMPOUND ) );

            return new NamedTag( "", dimensions );
        }

        CompoundTag dimensionData = new CompoundTag(new LinkedHashMap<>());

        dimensionData.put( "name", new StringTag( key ) );
        dimensionData.put( "id", new IntTag( id ) );
        dimensionData.put( "element", attributes );

        CompoundTag dimensions = new CompoundTag(new LinkedHashMap<>());
        dimensions.put( "type", new StringTag( "minecraft:dimension_type" ) );
        dimensions.put( "value", new ListTag( Collections.singletonList( dimensionData ), Tag.COMPOUND ) );

        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "minecraft:dimension_type", dimensions );
        root.put( "minecraft:worldgen/biome", createBiomeRegistry() );

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19_4 )
        {

            CompoundTag damage = protocolVersion >= ProtocolConstants.MINECRAFT_1_20 ? damageType1_20 : damageType;

            if ( protocolVersion == ProtocolConstants.MINECRAFT_1_21 )
            {
                damage = damageType1_21;
            }
            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21_2 )
            {
                damage = damageType1_21_2;
            }


            root.put( "minecraft:damage_type", damage );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_19 )
        {
            root.put( "minecraft:chat_type", createChatRegistry( protocolVersion ) );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21 )
        {
            root.put( "minecraft:painting_variant", createPaintingVariant( protocolVersion ) );
            root.put( "minecraft:wolf_variant", createWoldVariant( protocolVersion ) );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21_5 )
        {
            root.put( "minecraft:frog_variant", createEntityVariant( protocolVersion, "minecraft:frog_variant", "frog", true, "warm" ) );
            root.put( "minecraft:cat_variant", createEntityVariant( protocolVersion, "minecraft:cat_variant", "cat", false, "black" ) );
            root.put( "minecraft:pig_variant", createEntityVariant( protocolVersion, "minecraft:pig_variant", "pig", true, "temperate" ) );
            root.put( "minecraft:cow_variant", createEntityVariant( protocolVersion, "minecraft:cow_variant", "cow", true, "temperate" ) );
            root.put( "minecraft:minecraft:chicken_variant", createEntityVariant( protocolVersion, "minecraft:chicken_variant", "chicken", true, "temperate" ) );
            root.put( "minecraft:wolf_sound_variant", createWoldSoundVariant( protocolVersion ) );

        }

        return protocolVersion >= ProtocolConstants.MINECRAFT_1_20_2 ? root : new NamedTag( "", root );
    }

    public Tag getAttributes(int protocolVersion)
    {
        return new NamedTag( "", encodeAttributes( protocolVersion ) );
    }

    private CompoundTag encodeAttributes(int protocolVersion)
    {
        Map<String, TypedTag> attributes = new HashMap<>();

        // 1.16 - 1.16.1
        attributes.put( "name", new StringTag( key ) );
        //
        attributes.put( "natural", new ByteTag( (byte) ( natural ? 1 : 0 ) ) );
        attributes.put( "has_skylight", new ByteTag( (byte) ( hasSkylight ? 1 : 0 ) ) );
        attributes.put( "has_ceiling", new ByteTag( (byte) ( hasCeiling ? 1 : 0 ) ) );
        // 1.16 - 1.16.1
        attributes.put( "fixed_time", new LongTag( 10_000 ) );
        attributes.put( "shrunk", new ByteTag( (byte) 0 ) );
        //
        attributes.put( "ambient_light", new FloatTag( ambientLight ) );
        attributes.put( "ultrawarm", new ByteTag( (byte) ( ultrawarm ? 1 : 0 ) ) );
        attributes.put( "has_raids", new ByteTag( (byte) ( hasRaids ? 1 : 0 ) ) );
        attributes.put( "respawn_anchor_works", new ByteTag( (byte) ( respawnAnchorWorks ? 1 : 0 ) ) );
        attributes.put( "bed_works", new ByteTag( (byte) ( bedWorks ? 1 : 0 ) ) );
        attributes.put( "piglin_safe", new ByteTag( (byte) ( piglinSafe ? 1 : 0 ) ) );
        attributes.put( "infiniburn", new StringTag( infiniburn ) );
        attributes.put( "logical_height", new ByteTag( (byte) logicalHeight ) );

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

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21_6 && key.equals( "minecraft:overworld" ) )
        {
            attributes.put( "cloud_height", new IntTag( 192 ) );
        }

        CompoundTag tag = new CompoundTag(new LinkedHashMap<>());

        for ( Map.Entry<String, TypedTag> entry : attributes.entrySet() )
        {
            tag.put( entry.getKey(), entry.getValue() );
        }

        return tag;
    }

    private CompoundTag createBiomeRegistry()
    {
        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( "minecraft:worldgen/biome" ) );
        List<TypedTag> biomes = new ArrayList<>();
        for ( Biome biome : this.biomes )
        {
            biomes.add( encodeBiome( biome ) );
        }
        root.put( "value", new ListTag( biomes, Tag.COMPOUND ) );
        return root;
    }

    private CompoundTag createChatRegistry(int version)
    {

        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( "minecraft:chat_type" ) );
        CompoundTag systemChat = new CompoundTag(new LinkedHashMap<>());
        systemChat.put( "name", new StringTag( "minecraft:system" ) );
        systemChat.put( "id", new IntTag( 1 ) );
        CompoundTag element = new CompoundTag(new LinkedHashMap<>());


        CompoundTag chat = new CompoundTag(new LinkedHashMap<>());
        if ( version >= ProtocolConstants.MINECRAFT_1_19_1 )
        {
            chat.put( "style", new CompoundTag(new LinkedHashMap<>()) );
            chat.put( "translation_key", new StringTag( "chat.type.system" ) );
            chat.put( "parameters", new ListTag( Arrays.asList( new StringTag( "sender" ), new StringTag( "content" ) ), Tag.STRING ) );
        }

        element.put( "chat", chat );
        CompoundTag narration = new CompoundTag(new LinkedHashMap<>());
        if ( version >= ProtocolConstants.MINECRAFT_1_19_1 )
        {
            narration.put( "style", new CompoundTag(new LinkedHashMap<>()) );
            narration.put( "translation_key", new StringTag( "chat.type.system.narrate" ) );
            narration.put( "parameters", new ListTag( Arrays.asList( new StringTag( "sender" ), new StringTag( "content" ) ), Tag.STRING ) );
        } else
        {
            narration.put( "priority", new StringTag( "system" ) );
        }
        element.put( "narration", narration );
        systemChat.put( "element", element );
        root.put( "value", new ListTag( Arrays.asList( systemChat ), Tag.COMPOUND ) );
        return root;
    }


    private CompoundTag createPaintingVariant(int version)
    {

        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( "minecraft:painting_variant" ) );
        CompoundTag alban = new CompoundTag(new LinkedHashMap<>());
        alban.put( "name", new StringTag( "minecraft:alban" ) );
        alban.put( "id", new IntTag( 0 ) );

        CompoundTag paintingVariant = new CompoundTag(new LinkedHashMap<>());
        paintingVariant.put( "width", new IntTag( 1 ) );
        paintingVariant.put( "height", new IntTag( 1 ) );
        paintingVariant.put( "asset_id", new StringTag( "minecraft:alban" ) );

        alban.put( "element", paintingVariant );

        root.put( "value", new ListTag( Arrays.asList( alban ), Tag.COMPOUND ) );
        return root;
    }

    private CompoundTag createWoldVariant(int version)
    {

        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( "minecraft:wolf_variant" ) );
        CompoundTag ashen = new CompoundTag(new LinkedHashMap<>());
        ashen.put( "name", new StringTag( "minecraft:ashen" ) );
        ashen.put( "id", new IntTag( 0 ) );

        CompoundTag ashenProperties = new CompoundTag(new LinkedHashMap<>());
        if ( version < ProtocolConstants.MINECRAFT_1_21_5 )
        {
            ashenProperties.put( "wild_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen" ) );
            ashenProperties.put( "tame_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen_tame" ) );
            ashenProperties.put( "angry_texture", new StringTag( "minecraft:entity/wolf/wolf_ashen_angry" ) );
            ashenProperties.put( "biomes", new ListTag( Arrays.asList( new StringTag( "minecraft:plains" ) ), Tag.STRING ) );
        } else
        {
            CompoundTag assets = new CompoundTag(new LinkedHashMap<>());
            assets.put( "wild", new StringTag( "minecraft:entity/wolf/wolf_ashen" ) );
            assets.put( "tame", new StringTag( "minecraft:entity/wolf/wolf_ashen_tame" ) );
            assets.put( "angry", new StringTag( "minecraft:entity/wolf/wolf_ashen_angry" ) );
            ashenProperties.put( "assets", assets );
        }

        ashen.put( "element", ashenProperties );

        root.put( "value", new ListTag( Arrays.asList( ashen ), Tag.COMPOUND ) );
        return root;
    }


    private CompoundTag createEntityVariant(int version, String type, String entityName, boolean suffixedWithOwnName, String... entries)
    {
        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( type ) );

        List<TypedTag> values = new ArrayList<>();
        for ( String entity : entries )
        {
            CompoundTag element = new CompoundTag(new LinkedHashMap<>());
            String assetId = "entity/" + entityName + "/" + entity;
            if ( suffixedWithOwnName )
            {
                // Because frogs have that...
                assetId = assetId + "_" + entityName;
            }
            element.put( "asset_id", new StringTag( assetId ) );


            CompoundTag variant = new CompoundTag(new LinkedHashMap<>());
            variant.put( "name", new StringTag( "minecraft:" + entity ) );
            variant.put( "id", new IntTag( values.size() ) );
            variant.put( "element", element );
            values.add( variant );
        }
        root.put( "value", new ListTag( values, Tag.COMPOUND ) );
        return root;
    }

    private CompoundTag createWoldSoundVariant(int version)
    {
        CompoundTag root = new CompoundTag(new LinkedHashMap<>());
        root.put( "type", new StringTag( "minecraft:wolf_sound_variant" ) );
        CompoundTag angrySound = new CompoundTag(new LinkedHashMap<>());
        angrySound.put( "name", new StringTag( "minecraft:angry" ) );
        angrySound.put( "id", new IntTag( 0 ) );

        CompoundTag angrySoundVariant = new CompoundTag(new LinkedHashMap<>());

        angrySoundVariant.put( "ambient_sound", new StringTag( "minecraft:entity.wolf_angry.ambient" ) );
        angrySoundVariant.put( "death_sound", new StringTag( "minecraft:entity.wolf_angry.death" ) );
        angrySoundVariant.put( "growl_sound", new StringTag( "minecraft:entity.wolf_angry.growl" ) );
        angrySoundVariant.put( "hurt_sound", new StringTag( "minecraft:entity.wolf_angry.hurt" ) );
        angrySoundVariant.put( "pant_sound", new StringTag( "minecraft:entity.wolf_angry.pant" ) );
        angrySoundVariant.put( "whine_sound", new StringTag( "minecraft:entity.wolf_angry.whine" ) );


        angrySound.put( "element", angrySoundVariant );

        root.put( "value", new ListTag( Arrays.asList( angrySound ), Tag.COMPOUND ) );
        return root;
    }


    private CompoundTag encodeBiome(Biome biome)
    {
        CompoundTag biomeTag = new CompoundTag(new LinkedHashMap<>());

        biomeTag.put( "name", new StringTag( biome.getName() ) );
        biomeTag.put( "id", new IntTag( biome.getId() ) );

        CompoundTag element = new CompoundTag(new LinkedHashMap<>());
        element.put( "precipitation", new StringTag( biome.getPrecipitation() ) );

        element.put( "has_precipitation", new ByteTag( (byte) (biome.getPrecipitation().equals( "none" ) ? 0 : 1) ) );

        element.put( "depth", new FloatTag( biome.getDepth() ) );
        element.put( "temperature", new FloatTag( biome.getTemperature() ) );
        element.put( "scale", new FloatTag( biome.getScale() ) );
        element.put( "downfall", new FloatTag( biome.getDownfall() ) );
        element.put( "category", new StringTag( biome.getCategory() ) );

        CompoundTag effects = new CompoundTag(new LinkedHashMap<>());
        effects.put( "sky_color", new IntTag( biome.getSky_color() ) );
        effects.put( "water_fog_color", new IntTag( biome.getWater_color() ) );
        effects.put( "fog_color", new IntTag( biome.getFog_color() ) );
        effects.put( "water_color", new IntTag( biome.getWater_color() ) );
        if ( biome.getGrass_color_modiefer() != null )
        {
            effects.put( "grass_color_modifier", new StringTag( biome.getGrass_color_modiefer() ) );
        }
        if ( biome.getFoliage_color() != Integer.MIN_VALUE )
        {
            effects.put( "foliage_color", new IntTag( biome.getFoliage_color() ) );
        }

        CompoundTag moodSound = new CompoundTag(new LinkedHashMap<>());
        moodSound.put( "tick_delay", new IntTag( biome.getTick_delay() ) );
        moodSound.put( "offset", new DoubleTag( biome.getOffset() ) );
        moodSound.put( "block_search_extent", new IntTag( biome.getBlock_search_extent() ) );
        moodSound.put( "sound", new StringTag( biome.getSound() ) );

        effects.put( "mood_sound", moodSound );

        element.put( "effects", effects );
        biomeTag.put( "element", element );
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
