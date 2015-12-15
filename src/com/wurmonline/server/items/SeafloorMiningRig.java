package com.wurmonline.server.items;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.schmois.wurmunlimited.mods.surfaceminingfix.Constants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.ActionEntry;

public class SeafloorMiningRig {
    Logger logger = Logger.getLogger(this.getClass().getName());

    public SeafloorMiningRig() {
        logger.log(Level.INFO, "Azbantium Pickaxe enabled, creating item...");
        try {
            createItem();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace());
        }
    }

    // createItemTemplate(581, "dredge", "dredges", "almost full", "somewhat occupied", "half-full", "emptyish",
    // "A dirty sack with four metal blades along its rim. A rope is attached to it. The idea is that you drag it along the bottom and gather mud.",
    // new short[] { 108, 1, 21, 33, 147, 52, 44, 92, 139, 125 }, (short)265, (short)1, 0, 9072000L, 30, 30, 40, 1020, MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
    // "model.container.dredge.", 15.0f, 4500, (byte)14, 10000, true);

    /*
     * final int templateId, final String name, final String plural, final String itemDescriptionSuperb, final String itemDescriptionNormal, final String itemDescriptionBad, final String itemDescriptionRotten, final String itemDescriptionLong, final short[] itemTypes, final short imageNumber, final short behaviourType, final int combatDamage, final long decayTime, final int centimetersX, final int centimetersY, final int centimetersZ, final int primarySkill, final byte[] bodySpaces, final String modelName, final float difficulty, final int weightGrams, final byte material, final int value, final boolean isPurchased
     */

    private void createItem() throws IOException, Exception {
        ItemTemplateBuilder bmr = new ItemTemplateBuilder("schmois.seafloor_mining_rig");
        bmr.name("seafloor mining rig", "seafloor mining rigs",
                "A tool for deep sea mining. A rope is attached to it. Spiked rack and weighted to break rock deep underwater.");
        bmr.descriptions("almost full", "somewhat occupied", "half-full", "emptyish");
        bmr.itemTypes(new short[] { 108, 44, 38, 22, 10, 1, 33 });
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
        ActionEntry actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Seafloor Mining Rig",
                "sea mine", new int[] { 1, 4, 9, 36, 43, 46, 48 });

        ModActions.registerAction(actionEntry);
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

}