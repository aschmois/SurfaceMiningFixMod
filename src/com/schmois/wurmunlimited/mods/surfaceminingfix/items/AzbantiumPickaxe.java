package com.schmois.wurmunlimited.mods.surfaceminingfix.items;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import com.schmois.wurmunlimited.mods.surfaceminingfix.Constants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.CreationCategories;
import com.wurmonline.server.items.CreationEntry;
import com.wurmonline.server.items.CreationEntryCreator;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTemplate;

public class AzbantiumPickaxe {
    Logger logger = Logger.getLogger(this.getClass().getName());

    public AzbantiumPickaxe() {
        logger.log(Level.INFO, "Azbantium Pickaxe enabled, creating item...");
        try {
            createItem();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex.getStackTrace());
        }
    }

    private void createItem() throws IOException, Exception {
        ItemTemplateBuilder bmr = new ItemTemplateBuilder("schmois.azbantium_pickaxe");
        bmr.name("azbantium pickaxe", "azbantium pickaxes",
                "A tool for mining. Will surface mine quicker than a regular pickaxe.");
        bmr.descriptions("superb", "good", "ok", "poor");
        bmr.itemTypes(new short[] { 108, 44, 38, 22, 10, 2 });
        bmr.imageNumber((short) 743);
        bmr.behaviourType((short) 1);
        bmr.combatDamage(30);
        bmr.decayTime(Constants.ap_decayTime);
        bmr.dimensions(1, 30, 70);
        bmr.primarySkill(10009);
        bmr.bodySpaces(MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY);
        bmr.modelName("model.tool.pickaxe.");
        bmr.difficulty(Constants.ap_difficulty);
        bmr.weightGrams(Constants.ap_weight);
        bmr.material((byte) 11);
        bmr.value(100);
        bmr.isTraded(false);
        bmr.armourType(-1);
        ItemTemplate templateMR = bmr.build();
        Constants.ap_id = templateMR.getTemplateId();
        createCreationEntry(Constants.ap_id);

    }

    private void createCreationEntry(int id) throws Exception {
        if (id > 0) {
            logger.log(Level.INFO, "Creating creation entries for: " + id + " (azbantium pickaxe)");
            CreationEntryCreator.createSimpleEntry(10015, ItemList.shaft, ItemList.pickBladeIron, Constants.ap_id, true,
                    true, 0.0f, false, false, CreationCategories.TOOLS);

        } else
            throw new Exception("ERROR ATTEMPTING TO CREATE ENTRY FOR " + id);
    }

}