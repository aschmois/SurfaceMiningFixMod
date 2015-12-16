package com.wurmonline.server.items;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import com.schmois.wurmunlimited.mods.surfaceminingfix.Constants;
import com.wurmonline.server.MiscConstants;

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

}