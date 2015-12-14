package com.schmois.wurmunlimited.mods.surfaceminingfix;

import java.util.Properties;

public class Constants {
    public static boolean debug = false;
    
    public static boolean removeRockRestriction = false;

    // Azbantium Fist Enchantment
    public static boolean addAzbantiumFistEnchantment = true;

    public static byte af_enchantmentId = (byte) 34;

    public static int af_spellCost = 50;
    public static int af_spellDifficulty = 60;
    public static long af_spellCooldown = 0L;

    public static boolean af_all = false;
    public static boolean af_fo = false;
    public static boolean af_magranon = true;
    public static boolean af_vynora = false;
    public static boolean af_libila = false;

    public static boolean af_ironMaterial = false;
    public static boolean af_steelMaterial = true;
    public static boolean af_seryllMaterial = true;
    public static boolean af_glimmersteelMaterial = true;
    public static boolean af_adamantineMaterial = true;

    public static boolean af_usePower = true;
    
    public static boolean af_allowWoA = false;

    public static boolean getBoolean(Properties properties, String propertyName, boolean defaultValue) {
        String tmp = properties.getProperty(propertyName, Boolean.toString(defaultValue));
        return tmp.equalsIgnoreCase("true") || tmp.equalsIgnoreCase("yes") || tmp.equalsIgnoreCase("1");
    }

}
