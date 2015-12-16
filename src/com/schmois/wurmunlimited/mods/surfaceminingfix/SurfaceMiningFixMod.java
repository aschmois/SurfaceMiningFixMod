package com.schmois.wurmunlimited.mods.surfaceminingfix;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.CodeReplacer;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.classhooks.LocalNameLookup;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.schmois.wurmunlimited.mods.surfaceminingfix.items.AzbantiumPickaxe;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.SeafloorMiningRig;
import com.wurmonline.server.spells.AzbantiumFistEnchant;
import com.wurmonline.server.spells.Spell;
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
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class SurfaceMiningFixMod
        implements WurmMod, Initable, PreInitable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

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

                    if (Constants.af_all) {
                        for (Deity deity : Deities.getDeities()) {
                            deity.addSpell(azbantiumPickaxe);
                        }
                    } else {
                        if (Constants.af_fo)
                            Deities.getDeity(Deities.DEITY_FO).addSpell(azbantiumPickaxe);

                        if (Constants.af_magranon)
                            Deities.getDeity(Deities.DEITY_MAGRANON).addSpell(azbantiumPickaxe);

                        if (Constants.af_vynora)
                            Deities.getDeity(Deities.DEITY_VYNORA).addSpell(azbantiumPickaxe);

                        if (Constants.af_libila)
                            Deities.getDeity(Deities.DEITY_LIBILA).addSpell(azbantiumPickaxe);
                    }
                }
            }
        }.run();

    }

    @Override
    public void configure(Properties properties) {
        Constants.debug = Constants.getBoolean(properties, "debug", Constants.debug);

        Constants.alwaysLowerRockSlope = Constants.getBoolean(properties, "alwaysLowerRockSlope",
                Constants.alwaysLowerRockSlope);
        Constants.noNeedToUnconverRock = Constants.getBoolean(properties, "noNeedToUnconverRock",
                Constants.noNeedToUnconverRock);

        // Azbantium Fist
        Constants.addAzbantiumFistEnchantment = Constants.getBoolean(properties, "addAzbantiumFistEnchantment",
                Constants.addAzbantiumFistEnchantment);

        Constants.af_enchantmentId = Byte.valueOf(
                properties.getProperty("af_enchantmentId", Byte.toString(Constants.af_enchantmentId)).replace(",", ""));

        Constants.af_spellCost = Integer.valueOf(
                properties.getProperty("af_spellCost", Integer.toString(Constants.af_spellCost)).replace(",", ""));
        Constants.af_spellDifficulty = Integer.valueOf(properties
                .getProperty("af_spellDifficulty", Integer.toString(Constants.af_spellDifficulty)).replace(",", ""));
        Constants.af_spellCooldown = Long.valueOf(
                properties.getProperty("af_spellCooldown", Long.toString(Constants.af_spellCooldown)).replace(",", ""));

        Constants.af_all = Constants.getBoolean(properties, "af_all", Constants.af_all);
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

        Constants.af_allowWoA = Constants.getBoolean(properties, "af_allowWoA", Constants.af_allowWoA);

        // Azbantium Pickaxe
        Constants.addAzbantiumPickaxeItem = Constants.getBoolean(properties, "addAzbantiumPickaxeItem",
                Constants.addAzbantiumPickaxeItem);

        Constants.ap_decayTime = Long.valueOf(
                properties.getProperty("ap_decayTime", Long.toString(Constants.ap_decayTime)).replace(",", ""));
        Constants.ap_difficulty = Float.valueOf(
                properties.getProperty("ap_difficulty", Float.toString(Constants.ap_difficulty)).replace(",", ""));
        Constants.ap_weight = Integer
                .valueOf(properties.getProperty("ap_weight", Integer.toString(Constants.ap_weight)).replace(",", ""));

        Constants.ap_useQuality = Constants.getBoolean(properties, "ap_useQuality", Constants.ap_useQuality);

        // Seafloor Mining Rig
        Constants.addSeafloorMiningRigItem = Constants.getBoolean(properties, "addSeafloorMiningRigItem",
                Constants.addSeafloorMiningRigItem);

        Constants.smr_decayTime = Long.valueOf(
                properties.getProperty("smr_decayTime", Long.toString(Constants.smr_decayTime)).replace(",", ""));
        Constants.smr_difficulty = Float.valueOf(
                properties.getProperty("smr_difficulty", Float.toString(Constants.smr_difficulty)).replace(",", ""));
        Constants.smr_weight = Integer
                .valueOf(properties.getProperty("smr_weight", Integer.toString(Constants.smr_weight)).replace(",", ""));

        if (Constants.debug) {
            logger.log(Level.INFO, "debug: " + Constants.debug);

            logger.log(Level.INFO, "alwaysLowerRockSlope: " + Constants.alwaysLowerRockSlope);
            logger.log(Level.INFO, "noNeedToUnconverRock: " + Constants.noNeedToUnconverRock);

            // Azbantium Fist
            logger.log(Level.INFO, "addAzbantiumFistEnchantment: " + Constants.addAzbantiumFistEnchantment);

            logger.log(Level.INFO, "af_enchantmentId: " + Constants.af_enchantmentId);

            logger.log(Level.INFO, "af_spellCost: " + Constants.af_spellCost);
            logger.log(Level.INFO, "af_spellDifficulty: " + Constants.af_spellDifficulty);
            logger.log(Level.INFO, "af_spellCooldown: " + Constants.af_spellCooldown);

            logger.log(Level.INFO, "af_all: " + Constants.af_all);
            logger.log(Level.INFO, "af_fo: " + Constants.af_fo);
            logger.log(Level.INFO, "af_magranon: " + Constants.af_magranon);
            logger.log(Level.INFO, "af_vynora: " + Constants.af_vynora);

            logger.log(Level.INFO, "af_ironMaterial: " + Constants.af_ironMaterial);
            logger.log(Level.INFO, "af_steelMaterial: " + Constants.af_steelMaterial);
            logger.log(Level.INFO, "af_seryllMaterial: " + Constants.af_seryllMaterial);
            logger.log(Level.INFO, "af_glimmersteelMaterial: " + Constants.af_glimmersteelMaterial);
            logger.log(Level.INFO, "af_adamantineMaterial: " + Constants.af_adamantineMaterial);

            logger.log(Level.INFO, "af_usePower: " + Constants.af_usePower);

            logger.log(Level.INFO, "af_allowWoA: " + Constants.af_allowWoA);

            // Azbantium Pickaxe Item
            logger.log(Level.INFO, "addAzbantiumPickaxeItem: " + Constants.addAzbantiumPickaxeItem);

            logger.log(Level.INFO, "ap_decayTime: " + Constants.ap_decayTime);
            logger.log(Level.INFO, "ap_difficulty: " + Constants.ap_difficulty);
            logger.log(Level.INFO, "ap_weight: " + Constants.ap_weight);

            logger.log(Level.INFO, "ap_useQuality: " + Constants.ap_useQuality);

            // Seafloor Mining Rig
            logger.log(Level.INFO, "addSeafloorMiningRigItem: " + Constants.addSeafloorMiningRigItem);

            logger.log(Level.INFO, "smr_decayTime: " + Constants.smr_decayTime);
            logger.log(Level.INFO, "smr_difficulty: " + Constants.smr_difficulty);
            logger.log(Level.INFO, "smr_weight: " + Constants.smr_weight);
        }
    }

    @Override
    public void preInit() {
        ModActions.init();
        if (Constants.alwaysLowerRockSlope || Constants.addAzbantiumFistEnchantment
                || Constants.addSeafloorMiningRigItem) {
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
        if (Constants.noNeedToUnconverRock) {
            try {
                replaceSurroundingRockCondition();
            } catch (NotFoundException | BadBytecode e) {
                throw new HookException(e);
            }
        }
        if (Constants.addSeafloorMiningRigItem) {
            try {
                replaceWaterHeightCondition();
            } catch (NotFoundException | BadBytecode e) {
                throw new HookException(e);
            }
        }
    }

    @Override
    public void init() {
        if (!Constants.af_allowWoA || !Constants.af_allowBotD) {
            InvocationHandlerFactory spellCheck = new InvocationHandlerFactory() {
                @Override
                public InvocationHandler createInvocationHandler() {
                    return new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            Object precondition = method.invoke(proxy, args);
                            if (precondition instanceof Boolean && proxy instanceof Spell) {
                                if (!(boolean) precondition) {
                                    return false;
                                }
                                Creature performer = (Creature) args[1];
                                Item target = (Item) args[2];
                                if (target.getSpellEffect(Constants.af_enchantmentId) != null) {
                                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName()
                                            + " is already enchanted with something that would negate the effect.");
                                    return false;
                                }
                            }
                            return precondition;
                        }
                    };
                }
            };

            if (!Constants.af_allowWoA) {
                try {

                    ClassPool classPool = HookManager.getInstance().getClassPool();
                    CtClass[] paramTypes = { classPool.get("com.wurmonline.server.skills.Skill"),
                            classPool.get("com.wurmonline.server.creatures.Creature"),
                            classPool.get("com.wurmonline.server.items.Item") };
                    HookManager.getInstance().registerHook("com.wurmonline.server.spells.WindOfAges", "precondition",
                            Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes), spellCheck);
                } catch (NotFoundException e) {
                    logger.log(Level.INFO, "Broken Wind of Ages hook, let dev know", e);
                }
            }

            if (!Constants.af_allowBotD) {
                try {

                    ClassPool classPool = HookManager.getInstance().getClassPool();
                    CtClass[] paramTypes = { classPool.get("com.wurmonline.server.skills.Skill"),
                            classPool.get("com.wurmonline.server.creatures.Creature"),
                            classPool.get("com.wurmonline.server.items.Item") };
                    HookManager.getInstance().registerHook("com.wurmonline.server.spells.BlessingDark", "precondition",
                            Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes), spellCheck);
                } catch (NotFoundException e) {
                    logger.log(Level.INFO, "Broken Blessing of the Dark hook, let dev know", e);
                }
            }
        }
    }

    @Override
    public void onItemTemplatesCreated() {
        if (Constants.addAzbantiumPickaxeItem) {
            new AzbantiumPickaxe();
        }
        if (Constants.addSeafloorMiningRigItem) {
            new SeafloorMiningRig();
        }
    }

    public static boolean willMineSlope(Creature performer, Item source) {
        if (Constants.alwaysLowerRockSlope) {
            return true;
        }
        float power = 0.0F;

        if (Constants.addAzbantiumPickaxeItem && source.getTemplateId() == Constants.ap_id) {
            if (!Constants.ap_useQuality) {
                return true;
            }
            power = source.getQualityLevel();
            if (Constants.debug) {
                logger.log(Level.INFO, "Azbantium Pickaxe: " + power + "QL");
            }
        }
        SpellEffect se = source.getSpellEffect(Constants.af_enchantmentId);
        if (se != null) {
            if (!Constants.af_usePower) {
                return true;
            }
            power = se.getPower();
            if (Constants.debug) {
                logger.log(Level.INFO, "Azbantium Fist: " + power + " power");
            }
        }
        return Server.rand.nextFloat() <= getChance(power) / 100;
    }

    private static float getChance(float power) {
        float chance = power < 30 ? 25 + power / 5 : power;
        if (Constants.debug) {
            logger.log(Level.INFO, "Chance of rock mining: " + chance + "%");
        }
        return chance;
    }

    public static boolean canMineUnderwater(Creature performer, Item item) {
        final int digTilex = (int) performer.getStatus().getPositionX() + 2 >> 2;
        final int digTiley = (int) performer.getStatus().getPositionY() + 2 >> 2;
        int tile = Server.surfaceMesh.getTile(digTilex, digTiley);
        int height = Tiles.decodeHeight(tile);
        if (item.getTemplateId() == Constants.smr_id) {
            if (height < -25) {
                // TODO: Make SMR hollow
                if (Constants.debug) {
                    logger.log(Level.INFO, performer.getName() + " is mining with SMR.");
                }
                if (performer.getVehicle() != -10L) {
                    try {
                        final Item ivehic = Items.getItem(performer.getVehicle());
                        if (ivehic.isBoat()) {
                            return true;
                        }
                    } catch (NoSuchItemException ex3) {
                    }
                }
                performer.getCommunicator()
                        .sendNormalServerMessage("You must be on a boat of some kind to use the SMR.");
            } else {
                performer.getCommunicator().sendNormalServerMessage("The rock is too shallow to mine with an SMR.");
            }
            return false;
        }
        return height > -25;
    }

    private void replaceMineSlopeCondition() throws NotFoundException, BadBytecode {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass ctTileRockBehaviour = classPool.get("com.wurmonline.server.behaviours.TileRockBehaviour");
        CtClass ctRandom = classPool.get("java.util.Random");
        CtClass ctServer = classPool.get("com.wurmonline.server.Server");
        CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
        CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
        CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");

        CtClass[] paramTypes = { ctAction, ctCreature, ctItem, CtPrimitiveType.intType, CtPrimitiveType.intType,
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

    /*
    @formatter:off
         L766
        1665 aload_2;              performer 
        1666 invokevirtual 272;    com.wurmonline.server.creatures.Communicator getCommunicator() 
         L767 
        1669 ldc_w 916;            "The surrounding area needs to be rock before you mine." 
         L766 
        1672 invokevirtual 278;    void sendNormalServerMessage(java.lang.String performer) 
         L768 
        1675 iconst_1;
        1676 ireturn;
    @formatter:on
    */
    private void replaceSurroundingRockCondition() throws NotFoundException, BadBytecode {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass ctString = classPool.get("java.lang.String");
        CtClass ctTileRockBehaviour = classPool.get("com.wurmonline.server.behaviours.TileRockBehaviour");
        CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
        CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
        CtClass ctCommunicator = classPool.get("com.wurmonline.server.creatures.Communicator");

        CtClass[] paramTypes = { classPool.get("com.wurmonline.server.behaviours.Action"), ctCreature, ctItem,
                CtPrimitiveType.intType, CtPrimitiveType.intType, CtPrimitiveType.booleanType, CtPrimitiveType.intType,
                CtPrimitiveType.intType, CtPrimitiveType.shortType, CtPrimitiveType.floatType };

        CtMethod method = ctTileRockBehaviour.getMethod("action",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes));

        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        Bytecode bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addAload(2);
        bytecode.addInvokevirtual(ctCreature, "getCommunicator", ctCommunicator, new CtClass[] {});

        bytecode.addLdc("The surrounding area needs to be rock before you mine.");

        bytecode.addInvokevirtual(ctCommunicator, "sendNormalServerMessage", CtPrimitiveType.voidType,
                new CtClass[] { ctString });

        bytecode.addIconst(1);
        bytecode.add(Bytecode.IRETURN);

        byte[] search = bytecode.get();

        bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addGap(search.length);
        byte[] replacement = bytecode.get();

        new CodeReplacer(codeAttribute).replaceCode(search, replacement);
        methodInfo.rebuildStackMap(classPool);
    }

    /*  
    @formatter:off
         L728 
        1475 iload 14;             h 
        1477 bipush 231;
        1479 if_icmple 1733;
    @formatter:on
    */
    private void replaceWaterHeightCondition() throws NotFoundException, BadBytecode {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass ctTileRockBehaviour = classPool.get("com.wurmonline.server.behaviours.TileRockBehaviour");
        CtClass ctCreature = classPool.get("com.wurmonline.server.creatures.Creature");
        CtClass ctItem = classPool.get("com.wurmonline.server.items.Item");
        CtClass ctAction = classPool.get("com.wurmonline.server.behaviours.Action");

        CtClass[] paramTypes = { ctAction, ctCreature, ctItem, CtPrimitiveType.intType, CtPrimitiveType.intType,
                CtPrimitiveType.booleanType, CtPrimitiveType.intType, CtPrimitiveType.intType,
                CtPrimitiveType.shortType, CtPrimitiveType.floatType };

        CtMethod method = ctTileRockBehaviour.getMethod("action",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, paramTypes));

        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalNameLookup localNames = new LocalNameLookup(
                (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag));

        CodeIterator codeIterator = codeAttribute.iterator();

        while (codeIterator.hasNext()) {
            int pos = codeIterator.next();
            int op = codeIterator.byteAt(pos);
            if (op == CodeIterator.ISTORE) {
                int fieldRefIdx = codeIterator.byteAt(pos + 1);
                if (14 == fieldRefIdx) {
                    Bytecode bytecode = new Bytecode(codeIterator.get().getConstPool());
                    bytecode.addGap(1);
                    codeIterator.insertAt(pos + 2, bytecode.get());
                    break;
                }
            }
        }

        Bytecode bytecode = new Bytecode(methodInfo.getConstPool());

        bytecode.add(Bytecode.NOP);
        bytecode.addIload(14);
        bytecode.add(Bytecode.BIPUSH);
        bytecode.add(231);
        bytecode.add(Bytecode.IF_ICMPLE);

        byte[] search = bytecode.get();

        logger.log(Level.INFO, "Search bytecode length: " + search.length);

        bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addAload(localNames.get("performer"));
        bytecode.addAload(localNames.get("source"));
        bytecode.addInvokestatic(classPool.get(this.getClass().getName()), "canMineUnderwater",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[] { ctCreature, ctItem }));
        bytecode.add(Bytecode.IFEQ);
        byte[] replacement = bytecode.get();

        logger.log(Level.INFO, "Replacement bytecode length: " + replacement.length);

        new CodeReplacer(codeAttribute).replaceCode(search, replacement);
        methodInfo.rebuildStackMap(classPool);
    }
}
