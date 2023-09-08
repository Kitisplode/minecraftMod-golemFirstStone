# Changelog

## 0.0.1
    + initial release
    + added First of Stone entity
    + added Carved Stone Head and Carved Stone Core blocks
    + added First of Stone structure

## 0.0.2
    + added First of Oak entity
    + added Carved Oak Head and Carved Oak Core blocks
    + added First of Oak structure
    + redid First of Stone textures

## 0.0.3
    + added First of Brick entity
    + added Sculpted Emerald Head and Molded Brick Core blocks
    + added First of Brick structure

## 0.0.4
    + added First of Diorite entity
    + added Diorite Pawn entity
    + added Sculpted Gold Head and Chiseled Diorite Core blocks
    + added First of Diorite structure

## 0.0.5
    + added Dandori system
    + added Golem Dandori Call item
    + added Golem Dandori Attack item
    + integrated Dandori system into Iron and Snow Golems
    + added new Villager Professions and Structures as an alternate option to obtain First Heads
    + QoL - Made First of Stone and First of Brick AoE attacks damage Monsters only
    + QoL - Made First of Brick AoE shields apply to all non-Monsters
    + Balance - Made Diorite Pawns stay closer to their First of Diorite
    + Balance - Made Diorite Pawns die off if they don't have a target for 30 seconds
    + Balance - Reduced number of Diorite Pawns spawned to 3 at a time
    + Dandori???

## 0.0.6
    + added Golem Dandori Dig item
    + added Golem Dandori Throw item
    + added Dandori information to the HUD
    + added Terracotta Golems
    + added Cobblestone Golems
    + some refinements in golem targetting
    + More Dandori????

## 0.0.7
    + some golems (mostly just ones that are smaller than the player) can now be thrown with the Dandori Throw item)
    + added Plank Golems
    + added Mossy Golems
    + added rideable Grindstone Golems
    + removed dandori secret caves (they might have been causing issues with world generation sometimes). they'll return some day...

## 0.0.8
    + added Tuff Golems
    + added Copper Golems
    + added Copper Button (for Copper Golems to press!)

## 0.0.9
    + Rebalance changes to First of Stone
        - extended vertical range of AoE from 2 to 4
        - no longer pushable while attacking
        - resistance effect added while attacking
        - range to start charging attack increased from 2.3 blocks to 5.5 blocks
        - reduced damage drop-off so attack will always deal at least 65% of total damage to a target (30 maximum damage)
    + Rebalance changes to First of Oak
        - increase projectile speed from 2 to 4
        - projectile will now pierce through up to 4 enemies
        - reduced projectile damage from 10 to 7.5 (with the piercing, it felt like doing 10 to 4 enemies at once was maybe a bit much?)
        - removed projectile AoE
    + Rebalance changes to First of Brick
        - shield now reflects enemy arrows fired from outside
        - golem will now create a shield if an enemy is nearby as well as when allies are attacked
        - shield now applies 2 stacks of resistance instead of absorption
    + Rebalance changes to First of Diorite
        - Diorite Pawns no longer starve without targets
        - First of Diorite will now spawn only 1 pawn at a time, but with a much shorter cool down, and it can chain spawns together to go even faster
        - once the First of Diorite stops spawning, it will have to cooldown a little longer before it can get down and spawn more again
        - First of Diorite can have only 15 pawns active at a time
    + First of Diorite now has the flame inside its head like it's supposed to lol
    + Rebalance changes to Cobblestone Golem
        - deal less knockback when attacking
    + Rebalance changes to Plank Golem
        - deal less damage per arrow
        - slightly reduced fire rate
    + Rebalance changes to Mossy Golem
        - increased windup time for healing
    + Rebalance changes to Grindstone Golem
        - now have some damage resistance while charging
        - apply short slowness effect to enemies hit with charge
    + Changes to Dandori Call item
        - Can now use to command followers to go to a looked at location (hopefully makes golem commands slightly less clunky)