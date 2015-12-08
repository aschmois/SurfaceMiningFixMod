# Surface Mining Fix Mod \*\*ALPHA\*\*

### Features:
- Azbantium Fist enchantment that allows faster rock mining dependant on power.
- Spell configuration
- Ago's Mod Loader required

### Information

Mod works just like Wind of Ages (requires statuette, etc.) but is for the Magranon (not yet configurable) deity. The power directly relates to the chance you have to mine a slope down. Without the enchantment, you have a 25% chance. As the power increases, the chance goes up till you get to 100% at power 100. **Remember, this is an alpha, expect big changes and some bugs.**

### Properties

File: surfaceminingfix.properties
````
# Magranon enchantment spell
addSpell=true

# spellCost: favor costs of the spell
spellCost=50
# spellDifficulty: difficulty. 60 is the same as WindOfAges
spellDifficulty=60
# spellCooldown: cooldown in milliseconds
spellCooldown=0

# only turn this on when requested
debug=true

# Don't touch these settings
classname=com.schmois.wurmunlimited.mods.surfaceminingfix.SurfaceMiningFixMod
classpath=surfaceminingfix-0.xxx.jar
sharedClassLoader=true
````

### To Do: (in no particular order)
- Better/more configuration
- Item equivalent to Azbantium Pickaxe
- Enchantment to be able to mine deeper underwater or a dredge-like item but for rock
- Limit enchantment so that it's the only one that is allowed
  - Implemented: If the pickaxe already has WoA you won't be able to enchant, this is not the case the other way around; needs expansion.
  - 

Thanks ago for all your help and your great mod loader.
