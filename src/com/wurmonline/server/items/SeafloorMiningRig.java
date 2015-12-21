package com.wurmonline.server.items;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.CodeReplacer;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.LocalNameLookup;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import com.schmois.wurmunlimited.mods.surfaceminingfix.Constants;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;

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

public class SeafloorMiningRig {
    static Logger logger = Logger.getLogger(SeafloorMiningRig.class.getName());

    public SeafloorMiningRig() {
        logger.log(Level.INFO, "Azbantium Pickaxe enabled, creating item...");
        try {
            createItem();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace());
        }
    }

    private void createItem() throws IOException, Exception {
        ItemTemplateBuilder bmr = new ItemTemplateBuilder("schmois.seafloor_mining_rig");
        bmr.name("seafloor mining rig", "seafloor mining rigs",
                "A tool for deep sea mining. A rope is attached to it. Spiked rack and weighted to break rock deep underwater.");
        bmr.descriptions("almost full", "somewhat occupied", "half-full", "emptyish");
        bmr.itemTypes(new short[] { 108, 44, 38, 22, 10, /* 1, 33 */ });
        bmr.imageNumber((short) 265);
        bmr.behaviourType((short) 1);
        bmr.combatDamage(0);
        bmr.decayTime(Constants.smr_decayTime);
        bmr.dimensions(30, 30, 40);
        bmr.primarySkill(10009);
        bmr.bodySpaces(MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY);
        bmr.modelName("model.container.dredge.");
        bmr.difficulty(Constants.smr_difficulty);
        bmr.weightGrams(Constants.smr_weight);
        bmr.material((byte) 11);
        bmr.value(10000);
        bmr.isTraded(false);
        bmr.armourType(-1);
        ItemTemplate templateMR = bmr.build();
        Constants.smr_id = templateMR.getTemplateId();
        createCreationEntry(Constants.smr_id);
    }

    private void createCreationEntry(int id) throws Exception {
        if (id > 0) {
            final AdvancedCreationEntry smr = CreationEntryCreator.createAdvancedEntry(10015, ItemList.ironBar,
                    ItemList.steelBar, Constants.smr_id, false, false, 0.0f, true, false, CreationCategories.TOOLS);
            smr.setDepleteFromSource(5000);
            smr.setDepleteFromTarget(1000);
            smr.addRequirement(new CreationRequirement(1, ItemList.ropeThick, 1, true));
            CreationRequirement leadReq = new CreationRequirement(2, ItemList.leadBar, 2, true);
            leadReq.setVolumeNeeded(2000);
            smr.addRequirement(leadReq);
        } else
            throw new Exception("ERROR ATTEMPTING TO CREATE ENTRY FOR " + id);
    }

    public static boolean canMineUnderwater(Creature performer, Item item) {
        final int digTilex = (int) performer.getStatus().getPositionX() + 2 >> 2;
        final int digTiley = (int) performer.getStatus().getPositionY() + 2 >> 2;
        int tile = Server.surfaceMesh.getTile(digTilex, digTiley);
        int height = Tiles.decodeHeight(tile);
        if (item.getTemplateId() == Constants.smr_id) {
            if (height <= -25) {
                // TODO: Make SMR hollow
                if (Constants.debug) {
                    logger.log(Level.INFO, performer.getName() + " is mining with SMR.");
                }
                /*if (performer.getVehicle() != -10L) { FIXME
                    try {
                        final Item ivehic = Items.getItem(performer.getVehicle());
                        if (ivehic.isBoat()) {
                            return true;
                        }
                    } catch (NoSuchItemException ex3) {
                    }
                }
                performer.getCommunicator()
                        .sendNormalServerMessage("You must be on a boat of some kind to use the SMR.");*/
                return true;
            } else {
                performer.getCommunicator().sendNormalServerMessage("The rock is too shallow to mine with an SMR.");
            }
            return false;
        }
        return height > -25;
    }

    /*  
    @formatter:off
         L728 
        1475 iload 14;             h 
        1477 bipush 231;
        1479 if_icmple 1733;
    @formatter:on
    */
    public static void replaceWaterHeightCondition() throws NotFoundException, BadBytecode {
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

        bytecode = new Bytecode(methodInfo.getConstPool());
        bytecode.addAload(localNames.get("performer"));
        bytecode.addAload(localNames.get("source"));
        bytecode.addInvokestatic(classPool.get(SeafloorMiningRig.class.getName()), "canMineUnderwater",
                Descriptor.ofMethod(CtPrimitiveType.booleanType, new CtClass[] { ctCreature, ctItem }));
        bytecode.add(Bytecode.IFEQ);
        byte[] replacement = bytecode.get();

        new CodeReplacer(codeAttribute).replaceCode(search, replacement);
        methodInfo.rebuildStackMap(classPool);
    }

}