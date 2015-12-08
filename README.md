# Surface Mining Fix Mod \*\*ALPHA\*\*

### Features:
- Azbantium Fist enchantment that allows faster surface mining dependant on power.
- Detailed configuration
- Remove surface mining restriction altogether (off by default)
- Ago's Mod Loader required

### Information

Mod works just like Wind of Ages (requires statuette, etc.) but is for the Magranon deity by default. The power directly relates to the chance you have to mine a slope down. Without the enchantment, you have a 25% chance. As the power increases, the chance goes up till you get to 100% at power 100. **Remember, this is an alpha, expect big changes and some bugs.**

### Properties

File: surfaceminingfix.properties
````
# Don't touch these settings
classname=com.schmois.wurmunlimited.mods.surfaceminingfix.SurfaceMiningFixMod
classpath=surfaceminingfix-0.1.2.jar
sharedClassLoader=true

# only turn this on when requested
debug=false

# Surface mine like digging (AF will be useless). Default: false
removeRockRestriction=false

# AzbantiumFist (AF) will make surface mining easier. Default: true
addAzbantiumFistEnchantment=true

# Enchantment ID don't change unless you know what you're doing. Default: 34
af_enchantmentId=34

# Favor costs of each enchantment cost. Default: 50
af_spellCost=50
# Wind of Ages is 60. Default: 60
af_spellDifficulty=60
# Enchantment Cooldown. Default: 0
af_spellCooldown=0

# Allow all priests to enchant. Will ignore other deity options. Default: false
af_all=false
# Allow fo priest to enchant. Default: false
af_fo=false
# Allow magranon priest to enchant. Default: true
af_magranon=true
# Allow vynora priest to enchant. Default: false
af_vynora=false

# Allow pickaxes made with iron to be enchanted. Default: false
af_ironMaterial=false
# Allow pickaxes made with steel to be enchanted. Default: true
af_steelMaterial=true
# Allow pickaxes made with seryll to be enchanted. Default: true
af_seryllMaterial=true
# Allow pickaxes made with glimmersteel to be enchanted. Default: true
af_glimmersteelMaterial=true
# Allow pickaxes made with adamantine to be enchanted. Default: true
af_adamantineMaterial=true

# If true, the power of the spell will correlate to the chance you have to mine. At power 30 you'll have a 30% chance, at power 100 you'll have a 100% chance. Power 1-29 will have a value between 25-29. If false, enchanting will automatically get powered to 100. Default: true
af_usePower=true

# If false, WoA and AF will not be allowed to be on the pickaxe at the same time. Default: false
af_allowWoA=false
````

### To Do: (in no particular order)
- ~~Better/more configuration~~ (done)
- Item equivalent to Azbantium Pickaxe
- Enchantment to be able to mine deeper under water or a dredge-like item but for rock
- ~~Limit enchantment so that it's the only one that is allowed~~ (done)
  - ~~Implemented: If the pickaxe already has WoA you won't be able to enchant, this is not the case the other way around; needs expansion.~~ (done)

Thanks ago for all your help and your great mod loader.

[Ago's Mod Loader](http://forum.wurmonline.com/index.php?/topic/133085-released-server-mod-loader-priest-crops-seasons-server-packs-bag-of-holding/)
