package ru.leymooo.botfilter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.leymooo.botfilter.packets.UpdateTags;
import ru.leymooo.botfilter.packets.UpdateTags.Entry;

public class UpdateTagsHelper
{
    private static Map<String, List<String>> REQUIRED_TAGS = Map.of( "minecraft:damage_type", List.of(
            "minecraft:is_lightning",
            "minecraft:panic_environmental_causes",
            "minecraft:bypasses_invulnerability",
            "minecraft:bypasses_shield",
            "minecraft:bypasses_wolf_armor",
            "minecraft:wither_immune_to",
            "minecraft:always_hurts_ender_dragons",
            "minecraft:is_projectile",
            "minecraft:bypasses_enchantments",
            "minecraft:is_fall",
            "minecraft:witch_resistant_to",
            "minecraft:bypasses_resistance",
            "minecraft:is_fire",
            "minecraft:no_impact",
            "minecraft:always_triggers_silverfish",
            "minecraft:is_player_attack",
            "minecraft:avoids_guardian_thorns",
            "minecraft:bypasses_armor",
            "minecraft:no_knockback",
            "minecraft:burns_armor_stands",
            "minecraft:is_freezing",
            "minecraft:mace_smash",
            "minecraft:ignites_armor_stands",
            "minecraft:no_anger",
            "minecraft:always_kills_armor_stands",
            "minecraft:damages_helmet",
            "minecraft:can_break_armor_stand",
            "minecraft:is_drowning",
            "minecraft:is_explosion",
            "minecraft:bypasses_effects",
            "minecraft:burn_from_stepping",
            "minecraft:panic_causes",
            "minecraft:always_most_significant_fall"
        ),
        "minecraft:banner_pattern", List.of(
            "minecraft:pattern_item/creeper",
            "minecraft:pattern_item/field_masoned",
            "minecraft:pattern_item/piglin",
            "minecraft:pattern_item/flow",
            "minecraft:pattern_item/skull",
            "minecraft:pattern_item/flower",
            "minecraft:pattern_item/mojang",
            "minecraft:pattern_item/bordure_indented",
            "minecraft:no_item_required",
            "minecraft:pattern_item/globe",
            "minecraft:pattern_item/guster" ) );


    public static UpdateTags createPacket()
    {

        Map<String, List<Entry>> data = new HashMap<>();
        REQUIRED_TAGS.forEach( (key, value) -> {

            List<UpdateTags.Entry> entries = new ArrayList<>();
            for ( String tag : value )
            {
                entries.add( new Entry( tag, new ArrayList<>() ) );
            }
            data.put( key, entries );
        } );
        return new UpdateTags( data );
    }
}
