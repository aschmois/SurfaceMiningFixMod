package com.wurmonline.server.spells;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.skills.Skill;

public class AzbantiumFistEnchant extends ReligiousSpell {

	public static final byte BUFF_AZBANTIUM_FIST = 34;
	private static Logger logger = Logger.getLogger(AzbantiumFistEnchant.class.getName());

	public AzbantiumFistEnchant(int cost, int difficulty, long cooldown) {
		super("Azbantium Fist", ModActions.getNextActionId(), 20, cost, difficulty, 30, cooldown);
		this.targetItem = true;
		this.enchantment = BUFF_AZBANTIUM_FIST;

		try {
			ReflectionUtil.setPrivateField(this, ReflectionUtil.getField(Spell.class, "description"),
					"increases surface mining speed");
			ReflectionUtil.setPrivateField(this, ReflectionUtil.getField(Spell.class, "effectdesc"),
					"will mine surface rock quicker.");
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}

		ActionEntry actionEntry = ActionEntry.createEntry((short) number, name, "enchanting",
				new int[] { 2 /* ACTION_TYPE_SPELL */, 36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */,
						48 /* ACTION_TYPE_ENEMY_ALWAYS */ });
		ModActions.registerAction(actionEntry);
	}

	public static boolean isValidTarget(Item target) {
		return target.isMiningtool() && target.getMaterial() != Materials.MATERIAL_IRON;
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
	void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
		if (!isValidTarget(target)) {
			performer.getCommunicator().sendNormalServerMessage("The spell fizzles.");
			return;
		}

		ItemSpellEffects effs = target.getSpellEffects();
		if (effs == null) {
			effs = new ItemSpellEffects(target.getWurmId());
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
		this.checkDestroyItem(power, performer, target);
	}
}