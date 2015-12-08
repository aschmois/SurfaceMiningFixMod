package com.schmois.wurmunlimited.mods.surfaceminingfix;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.CodeReplacer;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.LocalNameLookup;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.spells.AzbantiumFistEnchant;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.spells.Spells;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class SurfaceMiningFixMod implements WurmMod, Initable, PreInitable, Configurable, ServerStartedListener {

    private static final Logger logger = Logger.getLogger(SurfaceMiningFixMod.class.getName());

    @Override
    public void onServerStarted() {
        new Runnable() {
            @Override
            public void run() {
                if (Constants.addAzbantiumFistEnchantment) {
                    logger.log(Level.INFO, "Registering AzbantiumFist enchant");

                    AzbantiumFistEnchant azbantiumPickaxe = new AzbantiumFistEnchant(Constants.af_spellCost,
                            Constants.af_spellDifficulty, Constants.af_spellCooldown);

                    try {
                        ReflectionUtil.callPrivateMethod(Spells.class,
                                ReflectionUtil.getMethod(Spells.class, "addSpell"), azbantiumPickaxe);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }

                    if (Constants.af_fo)
                        Deities.getDeity(Deities.DEITY_FO).addSpell(azbantiumPickaxe);

                    if (Constants.af_magranon)
                        Deities.getDeity(Deities.DEITY_MAGRANON).addSpell(azbantiumPickaxe);

                    if (Constants.af_vynora)
                        Deities.getDeity(Deities.DEITY_VYNORA).addSpell(azbantiumPickaxe);
                }
            }
        }.run();

    }

    @Override
    public void configure(Properties properties) {
        Constants.debug = Constants.getBoolean(properties, "debug", Constants.debug);

        Constants.addAzbantiumFistEnchantment = Constants.getBoolean(properties, "addAzbantiumFistEnchantment",
                Constants.addAzbantiumFistEnchantment);

        Constants.af_enchantmentId = Byte
                .valueOf(properties.getProperty("af_enchantmentId", Byte.toString(Constants.af_enchantmentId)));

        Constants.af_spellCost = Integer
                .valueOf(properties.getProperty("af_spellCost", Integer.toString(Constants.af_spellCost)));
        Constants.af_spellDifficulty = Integer
                .valueOf(properties.getProperty("af_spellDifficulty", Integer.toString(Constants.af_spellDifficulty)));
        Constants.af_spellCooldown = Long
                .valueOf(properties.getProperty("af_spellCooldown", Long.toString(Constants.af_spellCooldown)));

        Constants.af_fo = Constants.getBoolean(properties, "af_fo", Constants.af_fo);
        Constants.af_magranon = Constants.getBoolean(properties, "af_magranon", Constants.af_magranon);
        Constants.af_vynora = Constants.getBoolean(properties, "af_vynora", Constants.af_vynora);

        Constants.af_ironMaterial = Constants.getBoolean(properties, "af_ironMaterial", Constants.af_ironMaterial);
        Constants.af_steelMaterial = Constants.getBoolean(properties, "af_steelMaterial", Constants.af_steelMaterial);
        Constants.af_seryllMaterial = Constants.getBoolean(properties, "af_seryllMaterial",
                Constants.af_seryllMaterial);
        Constants.af_glimmersteelMaterial = Constants.getBoolean(properties, "af_glimmersteelMaterial",
                Constants.af_glimmersteelMaterial);
        Constants.af_adamantineMaterial = Constants.getBoolean(properties, "af_adamantineMaterial",
                Constants.af_adamantineMaterial);

        Constants.af_usePower = Constants.getBoolean(properties, "af_usePower", Constants.af_usePower);

        if (Constants.debug) {
            logger.log(Level.INFO, "debug: " + Constants.debug);

            logger.log(Level.INFO, "addAzbantiumFistEnchantment: " + Constants.addAzbantiumFistEnchantment);

            logger.log(Level.INFO, "af_enchantmentId: " + Constants.af_enchantmentId);

            logger.log(Level.INFO, "af_spellCost: " + Constants.af_spellCost);
            logger.log(Level.INFO, "af_spellDifficulty: " + Constants.af_spellDifficulty);
            logger.log(Level.INFO, "af_spellCooldown: " + Constants.af_spellCooldown);

            logger.log(Level.INFO, "af_fo: " + Constants.af_fo);
            logger.log(Level.INFO, "af_magranon: " + Constants.af_magranon);
            logger.log(Level.INFO, "af_vynora: " + Constants.af_vynora);

            logger.log(Level.INFO, "af_ironMaterial: " + Constants.af_ironMaterial);
            logger.log(Level.INFO, "af_steelMaterial: " + Constants.af_steelMaterial);
            logger.log(Level.INFO, "af_seryllMaterial: " + Constants.af_seryllMaterial);
            logger.log(Level.INFO, "af_glimmersteelMaterial: " + Constants.af_glimmersteelMaterial);
            logger.log(Level.INFO, "af_adamantineMaterial: " + Constants.af_adamantineMaterial);

            logger.log(Level.INFO, "af_usePower: " + Constants.af_usePower);
        }
    }

    @Override
    public void preInit() {
        ModActions.init();
        try {
            /**
             * The condition for checking if it mined a slope or not is repeated so we need to run the following code twice, it's really dumb.
             */
            replaceMineSlopeCondition();
            replaceMineSlopeCondition(); // Stupid Rolf code
        } catch (NotFoundException | BadBytecode e) {
            throw new HookException(e);
        }
    }

    @Override
    public void init() {
    }

    public static boolean willMineSlope(Creature performer, Item source) {
        if (Constants.removeRockRestriction) {
            return true;
        }
        SpellEffect se = source.getSpellEffect(Constants.af_enchantmentId);
        if (se != null) {
            if (!Constants.af_usePower) {
                return true;
            }
            float power = se.getPower();
            float chance = power < 30 ? 25 + power / 5 : power;
            if (Constants.debug) {
                logger.log(Level.INFO, "Chance of rock mining: " + chance);
            }
            return Server.rand.nextFloat() <= chance / 100;
        }
        return Server.rand.nextInt(5) == 0;
    }

    private void replaceMineSlopeCondition() throws NotFoundException, BadBytecode {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass ctTileRockBehaviour = classPool.get("com.wurmonline.server.behaviours.TileRockBehaviour");
        CtClass ctRandom = classPool.get("java.util.Random");
        CtClass ctServer = classPool.get("com.wurmonline.server.Server");
        CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
        CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");

        CtClass[] paramTypes = { classPool.get("com.wurmonline.server.behaviours.Action"),
                classPool.get("com.wurmonline.server.creatures.Creature"),
                classPool.get("com.wurmonline.server.items.Item"), CtPrimitiveType.intType, CtPrimitiveType.intType,
                CtPrimitiveType.booleanType, CtPrimitiveType.intType, CtPrimitiveType.intType,
                CtPrimitiveType.shortType, CtPrimitiveType.floatType };

        CtMethod method = ctTileRockBehaviour.getMethod("action",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes));

        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        LocalNameLookup localNames = new LocalNameLookup(
                (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag));

        Bytecode bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addGetstatic(ctServer, "rand", Descriptor.of(ctRandom));
        bytecode.addIconst(5);
        bytecode.addInvokevirtual(ctRandom, "nextInt",
                Descriptor.ofMethod(CtPrimitiveType.intType, new CtClass[] { CtPrimitiveType.intType }));
        bytecode.add(Bytecode.IFNE);
        byte[] search = bytecode.get();

        bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addAload(localNames.get("performer"));
        bytecode.addAload(localNames.get("source"));
        bytecode.addInvokestatic(classPool.get(this.getClass().getName()), "willMineSlope",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[] { ctCreature, ctItem }));
        bytecode.addGap(2);
        bytecode.add(Bytecode.IFEQ);
        byte[] replacement = bytecode.get();

        new CodeReplacer(codeAttribute).replaceCode(search, replacement);
        methodInfo.rebuildStackMap(classPool);
    }
}
