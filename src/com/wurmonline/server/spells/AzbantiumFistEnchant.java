package com.wurmonline.server.spells;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.schmois.wurmunlimited.mods.surfaceminingfix.Constants;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.skills.Skill;

public class AzbantiumFistEnchant extends ReligiousSpell {

    private static Logger logger = Logger.getLogger(AzbantiumFistEnchant.class.getName());

    public AzbantiumFistEnchant(int cost, int difficulty, long cooldown) {
        super("Azbantium Fist", ModActions.getNextActionId(), 20, cost, difficulty, 30, cooldown);
        this.targetItem = true;
        this.enchantment = Constants.af_enchantmentId;
        this.description = "increases surface mining speed";
        this.effectdesc = "will mine surface rock quicker.";

        ActionEntry actionEntry = ActionEntry.createEntry((short) number, name, "enchanting",
                new int[] { 2 /* ACTION_TYPE_SPELL */, 36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */,
                        48 /* ACTION_TYPE_ENEMY_ALWAYS */ });
        ModActions.registerAction(actionEntry);
    }

    public static boolean isValidTarget(Item target) {
        if (!target.isMiningtool())
            return false;
        if (Constants.af_ironMaterial && target.getMaterial() == Materials.MATERIAL_IRON)
            return true;
        if (Constants.af_steelMaterial && target.getMaterial() == Materials.MATERIAL_STEEL)
            return true;
        if (Constants.af_seryllMaterial && target.getMaterial() == Materials.MATERIAL_SERYLL)
            return true;
        if (Constants.af_glimmersteelMaterial && target.getMaterial() == Materials.MATERIAL_GLIMMERSTEEL)
            return true;
        if (Constants.af_adamantineMaterial && target.getMaterial() == Materials.MATERIAL_ADAMANTINE)
            return true;
        return false;
    }

    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!isValidTarget(target)) {
            performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.");
            return false;
        }
        if (target.getSpellEffect((byte) 16) != null) {
            performer.getCommunicator().sendNormalServerMessage(
                    "The " + target.getName() + " is already enchanted with something that would negate the effect.");
            return false;
        }
        return Spell.mayBeEnchanted(target);
    }

    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        return false;
    }

    @Override
    void doEffect(final Skill castSkill, double power, final Creature performer, final Item target) {
        if (!isValidTarget(target)) {
            performer.getCommunicator().sendNormalServerMessage("The spell fizzles.");
            return;
        }

        ItemSpellEffects effs = target.getSpellEffects();
        if (effs == null) {
            effs = new ItemSpellEffects(target.getWurmId());
        }

        if (!Constants.af_usePower) {
            power = 100.0F;
        }
        SpellEffect eff = effs.getSpellEffect(this.enchantment);
        if (eff == null) {
            performer.getCommunicator()
                    .sendNormalServerMessage("The " + target.getName() + " will now surface mine quicker.");
            eff = new SpellEffect(target.getWurmId(), this.enchantment, (float) power, 20000000);
            effs.addSpellEffect(eff);
            Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " looks pleased.", performer, 5);
        } else if (eff.getPower() > power) {
            performer.getCommunicator().sendNormalServerMessage("You frown as you fail to improve the power.");
            Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " frowns.", performer, 5);
        } else {
            performer.getCommunicator()
                    .sendNormalServerMessage("You succeed in improving the power of the " + this.name + ".");
            eff.improvePower((float) power);
            Server.getInstance().broadCastAction(String.valueOf(performer.getName()) + " looks pleased.", performer, 5);
        }
    }

    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        isMaxed(performer, target);
        this.checkDestroyItem(power, performer, target);
    }

    private boolean isMaxed(Creature performer, Item target) {
        SpellEffect eff = target.getSpellEffect(this.enchantment);
        if (!Constants.af_usePower) {
            if (eff != null) {
                returnFavor(performer);
                return true;
            }
        } else {
            if (eff != null && eff.getPower() == 100.0F) {
                returnFavor(performer);
                return true;
            }
        }
        return false;
    }

    private void returnFavor(Creature performer) {
        try {
            performer.setFavor(performer.getFavor() + (float) this.cost);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Programming error", e);
        }
        performer.getCommunicator().sendNormalServerMessage(this.name + " is already maxed out.");
    }
}