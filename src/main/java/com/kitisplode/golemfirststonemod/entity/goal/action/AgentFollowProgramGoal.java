package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.google.common.collect.Lists;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.item.item.ItemInstruction;
import com.kitisplode.golemfirststonemod.mixin.MixinItemFrameAccessor;
import com.kitisplode.golemfirststonemod.mixin.MixinMushroomCowAccessor;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import com.kitisplode.golemfirststonemod.util.ModTags;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class AgentFollowProgramGoal extends Goal
{
    private final EntityGolemAgent agent;

    private ArrayList<Instruction> instructions = new ArrayList<>();

    private int loops;

    public AgentFollowProgramGoal(EntityGolemAgent agent)
    {
        this.agent = agent;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (this.agent.getActive()) return true;
        return false;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (this.instructions.isEmpty()) return false;
        return true;
    }

    @Override
    public void start()
    {
        // Generate the list of instructions to follow here.
        this.loops = -5;
        this.fillInstructions();
    }

    @Override
    public void stop()
    {
        this.agent.setActive(false);
        this.instructions.clear();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick()
    {
        if (this.instructions.isEmpty()) return;
        Instruction currentInstruction = this.instructions.get(0);
        if (!currentInstruction.isRunning())
        {
            currentInstruction.start();
        }
        else
        {
            if (!currentInstruction.isDone())
                currentInstruction.tick();
            else
            {
                this.instructions.remove(0);
                if (this.loops > 0 && this.instructions.isEmpty())
                {
                    this.loops--;
                    this.fillInstructions();
                }
            }
        }
    }

    private void fillInstructions()
    {
        this.instructions.clear();
        // Loop through the inventory.
        int inventorySize = this.agent.getInventory().getContainerSize();
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 1; i < inventorySize; i++)
        {
            items.add(this.agent.getInventory().getItem(i));
        }
        while (!items.isEmpty())
        {
            Instruction instruction = this.pullInstructionFromFirstItem(items);
            items.remove(0);
            if (instruction == null) continue;
            for (int o = 0; o < instruction.skipAmount(); o++) items.remove(0);
            if (instruction instanceof InstructionLoop loop && this.loops == -5)
            {
                this.loops = loop.getLoopCount();
            }
            this.instructions.add(instruction);
        }
    }

    private boolean itemIsShulkerBox(ItemStack itemStack)
    {
        return itemStack.is(Items.SHULKER_BOX) || itemStack.is(Items.WHITE_SHULKER_BOX) || itemStack.is(Items.ORANGE_SHULKER_BOX)
                || itemStack.is(Items.MAGENTA_SHULKER_BOX) || itemStack.is(Items.LIGHT_BLUE_SHULKER_BOX) || itemStack.is(Items.YELLOW_SHULKER_BOX)
                || itemStack.is(Items.LIME_SHULKER_BOX) || itemStack.is(Items.PINK_SHULKER_BOX) || itemStack.is(Items.GRAY_SHULKER_BOX)
                || itemStack.is(Items.LIGHT_GRAY_SHULKER_BOX) || itemStack.is(Items.CYAN_SHULKER_BOX) || itemStack.is(Items.PURPLE_SHULKER_BOX)
                || itemStack.is(Items.BLUE_SHULKER_BOX) || itemStack.is(Items.BROWN_SHULKER_BOX) || itemStack.is(Items.GREEN_SHULKER_BOX)
                || itemStack.is(Items.RED_SHULKER_BOX) || itemStack.is(Items.BLACK_SHULKER_BOX);
    }

    private InstructionSet shulkerBoxToInstruction(ItemStack itemStack)
    {
        int setLoops = -1;
        InstructionSet set = null;
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return null;
        CompoundTag boxTag = tag.getCompound("BlockEntityTag");
        if (boxTag.contains("Items"))
        {
            ArrayList<Instruction> list = new ArrayList<>();
            ArrayList<ItemStack> itemsInBox = new ArrayList<>();
            ListTag listtag = boxTag.getList("Items", 10);
            for(int j = 0; j < listtag.size(); ++j) {
                CompoundTag itemTag = listtag.getCompound(j);
                ItemStack innerItem = ItemStack.of(itemTag);
                itemsInBox.add(innerItem);
            }
            while (!itemsInBox.isEmpty())
            {
                Instruction instruction = this.pullInstructionFromFirstItem(itemsInBox);
                itemsInBox.remove(0);
                if (instruction == null) continue;
                for (int o = 0; o < instruction.skipAmount(); o++) itemsInBox.remove(0);
                if (instruction instanceof InstructionLoop loop && setLoops == -1)
                {
                    setLoops = loop.getLoopCount();
                }
                list.add(instruction);
            }
            set = new InstructionSet(this.agent, list);
            set.setLoops(setLoops);
        }
        return set;
    }

    private Instruction pullInstructionFromFirstItem(ArrayList<ItemStack> items)
    {
        if (items.isEmpty()) return null;
        ItemStack itemStack = items.get(0);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_MOVE_FORWARD.get())) return new InstructionMoveForward(this.agent, itemStack.getCount());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_TURN_LEFT_90.get())) return new InstructionTurn(this.agent, -itemStack.getCount());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_TURN_RIGHT_90.get())) return new InstructionTurn(this.agent, itemStack.getCount());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_USE_BLOCK.get())) return new InstructionUseBlock(this.agent);
        if (this.itemIsShulkerBox(itemStack)) return this.shulkerBoxToInstruction(itemStack);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_BLOCK.get()) || itemStack.is(ModItems.ITEM_INSTRUCTION_IF_SOLID.get()))
            return this.ifItemToInstruction(items);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_PLACE_BLOCK.get())) return this.placeItemToInstruction(items);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_BREAK_BLOCK.get())) return new InstructionDestroyBlock(this.agent, this.agent.getHeldItem());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_USE_TOOL.get())) return new InstructionUseTool(this.agent, this.agent.getHeldItem());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_ATTACK.get())) return new InstructionAttack(this.agent, this.agent.getHeldItem());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_LOOP.get())) return new InstructionLoop(this.agent, itemStack.getCount());
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_NOT.get())) return this.notItemToInstruction(items);
        return null;
    }

    private InstructionIf notItemToInstruction(ArrayList<ItemStack> items)
    {
        if (items.size() < 2) return null;
        ArrayList<ItemStack> tempList = (ArrayList<ItemStack>) items.clone();
        tempList.remove(0);
        InstructionIf result = ifItemToInstruction(tempList);
        if (result == null) return null;
        result.setInvert(true);
        result.setSkipAmount(result.skipAmount() + 1);
        return result;
    }

    private InstructionIf ifItemToInstruction(ArrayList<ItemStack> items)
    {
        ItemStack itemStack = items.get(0);
        if (!(itemStack.getItem() instanceof ItemInstruction itemInstruction)) return null;
        ArrayList<ItemStack> tempList = (ArrayList<ItemStack>) items.clone();
        if (items.size() <= itemInstruction.getInstructionCount()) return null;
        for (int i = 0; i < itemInstruction.getInstructionCount(); i++) tempList.remove(0);

        Instruction nextInstruction = this.pullInstructionFromFirstItem(tempList);
        if (nextInstruction == null) return null;
        ArrayList<Instruction> list = new ArrayList<>();
        list.add(nextInstruction);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_BLOCK.get())) return new InstructionIfCheckBlock(this.agent, Block.byItem(items.get(1).getItem()), list);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_SOLID.get())) return new InstructionIfCheckSolid(this.agent, list);
        return null;
    }

    private InstructionPlaceBlock placeItemToInstruction(ArrayList<ItemStack> items)
    {
        if (items.size() < 2) return null;
        return new InstructionPlaceBlock(this.agent, items.get(1));
    }

//======================================================================================================================

    abstract class Instruction
    {
        protected final EntityGolemAgent agent;
        protected int skipAmount = 0;

        public Instruction(EntityGolemAgent agent)
        {
            this.agent = agent;
        }
        public int skipAmount()
        {
            return this.skipAmount;
        }
        public void setSkipAmount(int skipAmount)
        {
            this.skipAmount = skipAmount;
        }

        private boolean isRunning;
        public boolean isRunning()
        {
            return this.isRunning;
        }
        public boolean isDone()
        {
            return true;
        }
        public void start()
        {
            this.isRunning = true;
        }
        protected void stop()
        {
            this.isRunning = false;
        }
        public void tick() {}

        protected BlockPos getBlockPosForward()
        {
            Direction direction = Direction.orderedByNearest(this.agent)[0];
            Vec3 eyePos = this.agent.getEyePosition();
            return new BlockPos((int) Math.floor(eyePos.x()), (int) Math.floor(eyePos.y()), (int) Math.floor(eyePos.z())).offset(direction.getNormal());
        }
    }

    class InstructionMoveForward extends Instruction
    {
        private final int distance;
        private BlockPos blockPos;
        private int ticks = 10;
        private Vec3 previousPos;

        public InstructionMoveForward(EntityGolemAgent agent, int distance)
        {
            super(agent);
            this.distance = distance;
        }

        public boolean isDone()
        {
            if (this.agent.position().equals(this.previousPos))
            {
                this.ticks -= 1;
                if (this.ticks <= 0) return true;
            }
            this.previousPos = this.agent.position();
            return this.blockPos.getCenter().distanceToSqr(new Vec3(this.agent.getX(), this.blockPos.getCenter().y(), this.agent.getZ())) <= Mth.square(0.1f);
        }

        public void start()
        {
            super.start();
            Direction direction = Direction.orderedByNearest(this.agent)[0];
            this.blockPos = this.agent.getOnPos().offset(direction.getNormal().multiply(this.distance));
            this.previousPos = this.agent.position();
        }

        public void tick()
        {

            Vec3 center = this.blockPos.getCenter();
            Vec3 vTo = center.subtract(this.agent.position()).multiply(1,0,1);
            if (vTo.horizontalDistanceSqr() <= Mth.square(0.5f))
            {
                this.agent.setDeltaMovement(0, this.agent.getDeltaMovement().y(), 0);
                this.agent.setPos(center.x(), this.agent.getY(), center.z());
                return;
            }
            vTo = vTo.normalize().scale(0.125f);
            this.agent.setDeltaMovement(vTo.x(), this.agent.getDeltaMovement().y(), vTo.z());
        }
    }

    class InstructionTurn extends Instruction
    {
        private final int directionMultiplier;
        private float directionToTurn;
        private int ticks = 10;
        private float previousDirection;

        public InstructionTurn(EntityGolemAgent agent, int directionMultiplier)
        {
            super(agent);
            this.directionMultiplier = directionMultiplier;
        }

        public boolean isDone()
        {
            if (this.previousDirection == this.agent.getYRot())
            {
                this.ticks -= 1;
                if (this.ticks <= 0) return true;
            }
            this.previousDirection = this.agent.getYRot();
            return Math.abs(this.directionToTurn - this.agent.getYRot()) <= 5;
        }

        public void start()
        {
            super.start();
            float startingAngle = Math.round(this.agent.getYRot() / 90.0f) * 90;
            this.directionToTurn = startingAngle + 90.0f * this.directionMultiplier;
            if (this.directionToTurn < 0.0f) this.directionToTurn += 360.0f;
            else if (this.directionToTurn >= 360.0f) this.directionToTurn -= 360.0f;
            this.previousDirection = this.agent.getYRot();
        }

        public void tick()
        {
            this.agent.setYRot(this.rotateTowards(this.agent.getYRot(), this.directionToTurn, this.agent.getMaxHeadYRot()));
            this.agent.setYHeadRot(this.agent.getYRot());
            this.agent.setYBodyRot(this.agent.getYRot());
        }

        private float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
            float f = Mth.degreesDifference(pFrom, pTo);
            float f1 = Mth.clamp(f, -pMaxDelta, pMaxDelta);
            return pFrom + f1;
        }
    }

//======================================================================================================================

    class InstructionSet extends Instruction
    {
        protected final ArrayList<Instruction> instructionList;
        private int loops = -5;
        private int currentStep = 0;

        public InstructionSet(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            super(agent);
            this.instructionList = instructionList;
        }

        public void setLoops(int loops)
        {
            this.loops = loops;
        }

        @Override
        public boolean isDone()
        {
            return this.instructionsEmpty();
        }

        @Override
        public void tick()
        {
            super.tick();
            if (this.instructionsEmpty()) return;
            Instruction currentInstruction = this.instructionList.get(this.currentStep);
            if (!currentInstruction.isRunning())
            {
                currentInstruction.start();
            }
            else
            {
                if (!currentInstruction.isDone())
                    currentInstruction.tick();
                else
                {
                    currentInstruction.stop();
                    this.currentStep++;
                    if (this.loops > 0 && this.instructionsEmpty())
                    {
                        this.loops--;
                        this.currentStep = 0;
                    }
                }
            }
        }

        private boolean instructionsEmpty()
        {
            return this.currentStep >= this.instructionList.size();
        }
    }

    abstract class InstructionIf extends InstructionSet
    {
        private boolean invert = false;
        public InstructionIf(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
        }

        @Override
        public void start()
        {
            super.start();
            if (this.checkCondition() == this.invert) this.instructionList.clear();
        }

        abstract public boolean checkCondition();

        public void setInvert(boolean invert)
        {
            this.invert = invert;
        }
    }

    class InstructionIfCheckBlock extends InstructionIf
    {
        private final Block blockType;

        public InstructionIfCheckBlock(EntityGolemAgent agent, Block blockType, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
            this.blockType = blockType;
            this.skipAmount = 2;
        }

        @Override
        public boolean checkCondition()
        {
            BlockPos bp = this.getBlockPosForward();
            return this.agent.level().getBlockState(bp).is(this.blockType);
        }
    }

    class InstructionIfCheckSolid extends InstructionIf
    {
        public InstructionIfCheckSolid(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
            this.skipAmount = 1;
        }

        @Override
        public boolean checkCondition()
        {
            BlockPos bp = this.getBlockPosForward();
            return this.agent.level().getBlockState(bp).canOcclude();
        }
    }

//======================================================================================================================

    class InstructionUseBlock extends Instruction
    {
        private int ticks = 10;

        public InstructionUseBlock(EntityGolemAgent agent)
        {
            super(agent);
        }

        @Override
        public boolean isDone()
        {
            return --ticks <= 0;
        }

        @Override
        public void start()
        {
            super.start();
            BlockPos bp = this.getBlockPosForward();
            BlockState bs = this.agent.level().getBlockState(bp);
            if (!bs.is(ModTags.Blocks.AGENT_CAN_INTERACT)) return;
            this.agent.swingArm();
            bs.use(this.agent.level(), null, InteractionHand.MAIN_HAND, ExtraMath.playerRaycast(this.agent.level(), this.agent, ClipContext.Fluid.ANY, 1));
        }
    }

    class InstructionPlaceBlock extends Instruction
    {
        private int ticks = 10;
        private final ItemStack itemStack;

        public InstructionPlaceBlock(EntityGolemAgent agent, ItemStack itemStack)
        {
            super(agent);
            this.itemStack = itemStack;
            this.skipAmount = 1;
        }

        @Override
        public boolean isDone()
        {
            return --ticks <= 0;
        }

        @Override
        public void start()
        {
            super.start();
            if (itemStack.isEmpty()) return;
            if (itemStack.getItem() instanceof BlockItem blockItem)
            {
                BlockPos bp = this.getBlockPosForward();
                BlockState bs = blockItem.getBlock().defaultBlockState();
                if (this.agent.level().getBlockState(bp).canBeReplaced())
                {
                    this.agent.level().setBlock(bp, bs, 11);
                    this.agent.level().gameEvent(this.agent, GameEvent.BLOCK_PLACE, bp);
                    itemStack.shrink(1);
                    this.agent.swingArm();
                }
            }
        }
    }

    class InstructionDestroyBlock extends Instruction
    {
        private BlockPos targetBlock;
        private ItemStack destroyingItem;
        private float destroyProgress;
        private final int ticksBetweenSwings = 5;
        private int ticks = ticksBetweenSwings;
        private boolean cancel = false;

        public InstructionDestroyBlock(EntityGolemAgent agent, ItemStack destroyingItem)
        {
            super(agent);
            this.destroyingItem = destroyingItem;
        }

        @Override
        public boolean isDone()
        {
            return this.cancel;
        }

        @Override
        public void start()
        {
            super.start();
            this.targetBlock = this.getBlockPosForward();
        }

        @Override
        public void tick()
        {
            this.ticks--;
            if (this.ticks <= 0)
            {
                this.ticks = this.ticksBetweenSwings;
                this.agent.swingArm();
                BlockState bs = this.agent.level().getBlockState(this.targetBlock);
                float itemSpeed = destroyingItem.getDestroySpeed(bs);
                float destroySpeed = bs.getDestroySpeed(this.agent.level(), this.targetBlock);
                if (destroySpeed == -1.0f)
                {
                    this.cancel = true;
                    return;
                }
                float destroyInc = itemSpeed / destroySpeed / 15;
                this.destroyProgress += destroyInc;
                if (this.destroyProgress >= 1.0f)
                {
                    if (this.destroyingItem.isDamageableItem())
                    {
                        this.destroyingItem.hurtAndBreak(1, this.agent, agent -> {
                            agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN);});
                    }
                    this.agent.level().destroyBlock(this.targetBlock, true, this.agent);
                    this.destroyProgress = 0.0f;
                    this.cancel = true;
                }
                else
                {
                    SoundType soundtype = bs.getSoundType(this.agent.level(), this.targetBlock, null);
                    Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(soundtype.getHitSound(), SoundSource.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, SoundInstance.createUnseededRandom(), this.targetBlock));
                }
                this.agent.level().destroyBlockProgress(this.agent.getId(), this.targetBlock, this.getDestroyStage());
            }
        }

        private int getDestroyStage() {
            return this.destroyProgress > 0.0F ? (int)(this.destroyProgress * 10.0F) : -1;
        }
    }

    class InstructionUseTool extends Instruction
    {
        private final ItemStack usedItem;
        private int ticks = 10;
        public InstructionUseTool(EntityGolemAgent agent, ItemStack usedItem)
        {
            super(agent);
            this.usedItem = usedItem;
        }
        @Override
        public boolean isDone()
        {
            return --ticks <= 0;
        }
        @Override
        public void start()
        {
            super.start();
            boolean itemUsed = false;
            BlockPos bp = this.getBlockPosForward();

            // Check for an entity first.
            List<Entity> entityList = this.agent.level().getEntities(this.agent, AABB.ofSize(bp.getCenter(), 1, 1, 1));
            if (!entityList.isEmpty())
            {
                Entity targetEntity = entityList.get(0);
                itemUsed = this.entityInteract(targetEntity);
            }
            // Otherwise, perform the block actions instead.
            if (!itemUsed)
            {
                if (this.usedItem.is(ItemTags.HOES))
                {
                    itemUsed = hoe(bp);
                    if (!itemUsed) itemUsed = hoe(bp.below());
                }
                if (this.usedItem.is(ItemTags.AXES))
                {
                    itemUsed = axe(bp);
                    if (!itemUsed) itemUsed = axe(bp.below());
                }
                if (this.usedItem.is(ItemTags.SHOVELS))
                {
                    itemUsed = shovel(bp);
                    if (!itemUsed) itemUsed = shovel(bp.below());
                }
                if (this.usedItem.is(Items.FLINT_AND_STEEL) || this.usedItem.is(Items.FIRE_CHARGE))
                    itemUsed = flintAndSteel(bp);
                if (this.usedItem.is(ItemTags.BOATS)) itemUsed = boat(bp);
                // BOW?
                if (this.usedItem.is(Items.POTION) && PotionUtils.getPotion(this.usedItem) == Potions.WATER) itemUsed = waterBottle(bp);
                if (this.usedItem.getItem() instanceof BucketItem) itemUsed = bucket(bp);
                if (this.usedItem.is(Items.GLASS_BOTTLE)) itemUsed = glassBottle(bp);
                if (this.usedItem.is(Items.SNOWBALL)
                        || this.usedItem.is(Items.EGG)
                        || this.usedItem.is(Items.ENDER_PEARL)
                        || this.usedItem.is(Items.EXPERIENCE_BOTTLE)
                        || this.usedItem.getItem() instanceof ThrowablePotionItem)
                    itemUsed = throwable();
                if (this.usedItem.is(Items.FISHING_ROD)) itemUsed = fishingRod();
                if (this.usedItem.is(Items.BONE_MEAL))
                {
                    itemUsed = boneMeal(bp);
                    if (!itemUsed) itemUsed = boneMeal(bp.below());
                }
                if (this.usedItem.is(Items.SHEARS)) itemUsed = shears(bp);
                if (this.usedItem.is(Items.ENDER_EYE)) itemUsed = enderEye(bp);
                // MAP?
                if (this.usedItem.is(Items.FIREWORK_ROCKET)) itemUsed = rocket(bp);
                if (this.usedItem.is(Items.LEAD)) itemUsed = lead(bp);
                // SHIELD?
                if (this.usedItem.getItem() instanceof RecordItem) itemUsed = record(bp);
                if (this.usedItem.is(Items.TRIDENT)) itemUsed = trident();
                // CROSSBOW?
                if (this.usedItem.getItem() instanceof InstrumentItem) itemUsed = instrument();
                if (this.usedItem.is(Items.HONEYCOMB)) itemUsed = honeycomb(bp);
                if (this.usedItem.is(Items.COMPASS)) itemUsed = compass(bp);
                // BRUSH?
                if (this.usedItem.is(Items.GLOWSTONE)) itemUsed = glowstone(bp);
                if (this.usedItem.is(Items.ARMOR_STAND)) itemUsed = armorStand(bp);
                if (this.usedItem.getItem() instanceof MinecartItem) itemUsed = minecart(bp);
                if (this.usedItem.is(Items.WRITABLE_BOOK) || this.usedItem.is(Items.WRITTEN_BOOK)) itemUsed = book(bp);
                if (this.agent.level().getBlockState(bp).is(Blocks.CHISELED_BOOKSHELF)) itemUsed = chiseledBookshelf(bp);
                if (!itemUsed && this.agent.level().getBlockState(bp).is(Blocks.WATER_CAULDRON)) itemUsed = washInCauldron(bp);
                if (!itemUsed && this.usedItem.is(Items.POWDER_SNOW_BUCKET)) itemUsed = snowBucket(bp);
                if (!itemUsed && this.usedItem.getItem() instanceof BlockItem blockItem)
                {
                    BlockState bs = blockItem.getBlock().defaultBlockState();
                    if (this.agent.level().getBlockState(bp).canBeReplaced())
                    {
                        this.agent.level().setBlock(bp, bs, 11);
                        this.agent.level().gameEvent(this.agent, GameEvent.BLOCK_PLACE, bp);
                        this.usedItem.shrink(1);
                        itemUsed = true;
                    }
                }
            }
            if (itemUsed)
            {
                this.agent.swingArm();
            }
            else
            {
                BlockState bs = this.agent.level().getBlockState(bp);
                if (!bs.is(ModTags.Blocks.AGENT_CAN_INTERACT)) return;
                this.agent.swingArm();
                bs.use(this.agent.level(), null, InteractionHand.MAIN_HAND, ExtraMath.playerRaycast(this.agent.level(), this.agent, ClipContext.Fluid.ANY, 1));
            }
        }

        private boolean entityInteract(Entity targetEntity)
        {
            boolean result = false;
            // Lead mob
            // WORKS
            if (this.usedItem.is(Items.LEAD))
            {
                if (targetEntity instanceof Mob mob && mob.isAlive() && !mob.isLeashed())
                {
                    mob.setLeashedTo(this.agent, true);
                    return true;
                }
            }
            // Name mob
            // WORKS
            if (this.usedItem.is(Items.NAME_TAG))
            {
                if (this.usedItem.hasCustomHoverName() && !(targetEntity instanceof Player) && targetEntity.isAlive())
                {
                    targetEntity.setCustomName(this.usedItem.getHoverName());
                    if (targetEntity instanceof Mob mob) {
                        mob.setPersistenceRequired();
                    }
                    this.usedItem.shrink(1);
                    return true;
                }
            }
            // Dye sheep
            // WORKS
            if (this.usedItem.getItem() instanceof DyeItem)
            {
                DyeItem dye = (DyeItem) this.usedItem.getItem();
                if (targetEntity instanceof Sheep sheep && sheep.isAlive() && !sheep.isSheared() && sheep.getColor() != dye.getDyeColor())
                {
                    sheep.setColor(dye.getDyeColor());
                    this.usedItem.shrink(1);
                    this.agent.level().playSound(null, sheep, SoundEvents.DYE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    result = true;
                }
            }
            // Shear sheep, mooshroom, snow golem
            // WORKS
            if (this.usedItem.is(Items.SHEARS))
            {
                if (targetEntity instanceof Sheep sheep && sheep.isAlive() && sheep.readyForShearing())
                {
                    sheep.shear(SoundSource.NEUTRAL);
                    sheep.gameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (targetEntity instanceof MushroomCow cow && cow.isAlive() && cow.readyForShearing())
                {
                    cow.shear(SoundSource.NEUTRAL);
                    cow.gameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (targetEntity instanceof SnowGolem golem && golem.isAlive() && golem.readyForShearing())
                {
                    golem.shear(SoundSource.NEUTRAL);
                    golem.gameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (result && this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
            }
            // Milk cow
            // WORKS
            if (this.usedItem.is(Items.BUCKET))
            {
                if (targetEntity instanceof Cow cow && cow.isAlive() && !cow.isBaby())
                {
                    this.agent.level().playSound(null, cow, SoundEvents.COW_MILK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    this.usedItem.shrink(1);
                    Block.popResource(this.agent.level(), this.agent.getOnPos().above(), new ItemStack(Items.MILK_BUCKET));
                    result = true;
                }
            }
            // Soup cow
            // WORKS
            if (this.usedItem.is(Items.BOWL))
            {
                if (targetEntity instanceof MushroomCow cow && cow.isAlive() && !cow.isBaby())
                {
                    ItemStack is;
                    SoundEvent se;
                    MixinMushroomCowAccessor mushroomCow = (MixinMushroomCowAccessor) cow;
                    if (mushroomCow.getEffect() != null)
                    {
                        is = new ItemStack(Items.SUSPICIOUS_STEW);
                        SuspiciousStewItem.saveMobEffect(is, mushroomCow.getEffect(), mushroomCow.getEffectDuration());
                        mushroomCow.setEffect(null);
                        mushroomCow.setEffectDuration(0);
                        se = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
                    }
                    else
                    {
                        is = new ItemStack(Items.MUSHROOM_STEW);
                        se = SoundEvents.MOOSHROOM_MILK;
                    }
                    cow.playSound(se, 1.0F, 1.0F);
                    this.usedItem.shrink(1);
                    Block.popResource(this.agent.level(), this.agent.getOnPos().above(), is);
                    result = true;
                }
            }
            // Saddle animal
            // WORKS
            if (this.usedItem.is(Items.SADDLE))
            {
                if (targetEntity instanceof Saddleable saddleable && targetEntity.isAlive() && saddleable.isSaddleable() && !saddleable.isSaddled())
                {
                    saddleable.equipSaddle(SoundSource.NEUTRAL);
                    this.usedItem.shrink(1);
                    result = true;
                }
            }
            // Armor animal
            // WORKS
            if (!result && targetEntity instanceof AbstractHorse horse
                    && horse.isAlive() && horse.canWearArmor() && horse.isArmor(this.usedItem) && !horse.isWearingArmor() && horse.isTamed())
            {
                if (this.usedItem.is(ItemTags.WOOL_CARPETS)
                        || this.usedItem.is(Items.LEATHER_HORSE_ARMOR) || this.usedItem.is(Items.IRON_HORSE_ARMOR)
                        || this.usedItem.is(Items.GOLDEN_HORSE_ARMOR) || this.usedItem.is(Items.DIAMOND_HORSE_ARMOR))
                {
                    horse.getSlot(401).set(this.usedItem.split(1));
                    result = true;
                }
                if (this.usedItem.is(Items.CHEST) && targetEntity instanceof AbstractChestedHorse chestedHorse && !chestedHorse.hasChest())
                {
                    chestedHorse.getSlot(499).set(this.usedItem.split(1));
                    result = true;
                }
            }
            // Breed animal
            // WORKS
            if (!result && targetEntity instanceof Animal animal && animal.isFood(this.usedItem))
            {
                int i = animal.getAge();
                if (i == 0 && animal.canFallInLove())
                {
                    animal.setInLove(null);
                    this.usedItem.shrink(1);
                    result = true;
                }
                else if (animal.isBaby())
                {
                    animal.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-i), true);
                    this.usedItem.shrink(1);
                    result = true;
                }
            }
            // Armor others
            // WORKS
            if (!result && this.usedItem.getItem() instanceof ArmorItem)
            {
                if (targetEntity instanceof LivingEntity livingEntity && livingEntity.canTakeItem(this.usedItem))
                {
                    EquipmentSlot equipmentslot = LivingEntity.getEquipmentSlotForItem(this.usedItem);
                    ItemStack itemstack = this.usedItem.split(1);
                    livingEntity.setItemSlot(equipmentslot, itemstack);
                    if (targetEntity instanceof Mob mob)
                    {
                        mob.setDropChance(equipmentslot, 2.0F);
                        mob.setPersistenceRequired();
                    }
                    result = true;
                }
            }
            // Enter boat
            // Enter minecart
            // Feed powered minecart
            // Explode TNT minecart
            // Repair iron golem
            if (this.usedItem.is(Items.IRON_INGOT) && targetEntity.getType().equals(EntityType.IRON_GOLEM) && targetEntity instanceof IronGolem golem && targetEntity.isAlive() && golem.getHealth() == golem.getMaxHealth())
            {
                golem.heal(25);
                golem.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F + (this.agent.getRandom().nextFloat() - this.agent.getRandom().nextFloat()) * 0.2F);
                this.usedItem.shrink(1);
                result = true;
            }
            // Item frame
            // WORKS
            if (targetEntity instanceof ItemFrame itemFrame && targetEntity.isAlive())
            {
                MixinItemFrameAccessor itemFrameAccessor = (MixinItemFrameAccessor) itemFrame;
                if (!itemFrameAccessor.getFixed())
                {
                    if (!itemFrame.getItem().isEmpty())
                    {
                        itemFrame.playSound(itemFrame.getRotateItemSound(), 1.0F, 1.0F);
                        itemFrame.setRotation(itemFrame.getRotation() + 1);
                        itemFrame.gameEvent(GameEvent.BLOCK_CHANGE, this.agent);
                        result = true;
                    }
                    else
                    {
                        boolean canContinue = true;
                        if (this.usedItem.is(Items.FILLED_MAP)) {
                            MapItemSavedData mapitemsaveddata = MapItem.getSavedData(this.usedItem, this.agent.level());
                            if (mapitemsaveddata != null && mapitemsaveddata.isTrackedCountOverLimit(256)) {
                                canContinue = false;
                            }
                        }
                        if (canContinue)
                        {
                            itemFrame.setItem(this.usedItem);
                            itemFrame.gameEvent(GameEvent.BLOCK_CHANGE, this.agent);
                            this.usedItem.shrink(1);
                            result = true;
                        }
                    }
                }
            }
            // Release leash knot
//            if (targetEntity.getType().equals(EntityType.LEASH_KNOT) && targetEntity.isAlive())
//            {
//                targetEntity.interact(null, null);
//            }
            return result;
        }

        // WORKS
        private boolean hoe(BlockPos bp)
        {
            boolean result = false;

            Block block = this.agent.level().getBlockState(bp).getBlock();
            BlockState bsNew = null;
            if (block == Blocks.ROOTED_DIRT)
            {
                Block.popResourceFromFace(this.agent.level(), bp, Direction.orderedByNearest(this.agent)[0].getOpposite(), new ItemStack(Items.HANGING_ROOTS));
                bsNew = Blocks.DIRT.defaultBlockState();
            }
            else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
                    this.agent.level().getBlockState(bp.above()).isAir())
            {
                if (block == Blocks.COARSE_DIRT) bsNew = Blocks.DIRT.defaultBlockState();
                else bsNew = Blocks.FARMLAND.defaultBlockState();
            }
            if (bsNew != null)
            {
                this.agent.level().setBlock(bp, bsNew, 11);
                this.agent.level().playSound(this.agent, bp, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
                result = true;
            }
            return result;
        }

        // WORKS
        private boolean axe(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.level().getBlockState(bp);
            int type = 0;
            BlockState bsNew = AxeItem.getAxeStrippingState(bs);
            if (bsNew == null) {bsNew = WeatheringCopper.getPrevious(bs).orElse(null); type = 1;}
            if (bsNew == null) {bsNew = Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(bs.getBlock())).map(block -> block.withPropertiesOf(bs)).orElse(null); type = 2;}
            if (bsNew != null)
            {
                switch (type)
                {
                    case 0 ->
                            this.agent.level().playSound(this.agent, bp, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    case 1 ->
                    {
                        this.agent.level().playSound(this.agent, bp, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ParticleUtils.spawnParticlesOnBlockFaces(this.agent.level(), bp, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
                    }
                    case 2 ->
                    {
                        this.agent.level().playSound(this.agent, bp, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ParticleUtils.spawnParticlesOnBlockFaces(this.agent.level(), bp, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                    }
                }
                this.agent.level().setBlock(bp, bsNew, 11);
                if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
                result = true;
            }
            return result;
        }

        // WORKS
        private boolean shovel(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.level().getBlockState(bp);
            BlockState bsNew = null;
            BlockState bsPath = ShovelItem.getShovelPathingState(bs);
            if (bsPath != null && this.agent.level().isEmptyBlock(bp.above()))
            {
                this.agent.level().playSound(this.agent, bp, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                bsNew = bsPath;
            }
            else if (bs.getBlock() instanceof CampfireBlock && bs.getValue(CampfireBlock.LIT))
            {
                CampfireBlock.dowse(this.agent, this.agent.level(), bp, bs);
                bsNew = bs.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
            }
            if (bsNew != null)
            {
                this.agent.level().setBlock(bp, bsNew, 11);
                if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
                result = true;
            }
            return result;
        }

        // WORKS
        private boolean flintAndSteel(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.level().getBlockState(bp);
            BlockState bsNew = null;
            if (!CampfireBlock.canLight(bs) && !CandleBlock.canLight(bs) && !CandleCakeBlock.canLight(bs))
            {
                if (BaseFireBlock.canBePlacedAt(this.agent.level(), bp, this.agent.getDirection())) bsNew = BaseFireBlock.getState(this.agent.level(), bp);
            }
            else if (bs.getBlock() instanceof TntBlock tnt)
            {
                tnt.onCaughtFire(bs, this.agent.level(), bp, null, this.agent);
                bsNew = Blocks.AIR.defaultBlockState();
            }
            else bsNew = bs.setValue(BlockStateProperties.LIT, Boolean.valueOf(true));
            if (bsNew != null)
            {
                this.agent.level().playSound(this.agent, bp, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, this.agent.level().getRandom().nextFloat() * 0.4F + 0.8F);
                this.agent.level().setBlock(bp, bsNew, 11);
                if (this.usedItem.is(Items.FIRE_CHARGE)) this.usedItem.shrink(1);
                if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
                result = true;
            }
            return result;
        }

        // WORKS
        private boolean boat(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.canOcclude()) return false;
            Vec3 center = bp.getCenter();
            Boat boat;
            if (this.usedItem.is(ItemTags.CHEST_BOATS)) boat = new ChestBoat(this.agent.level(), center.x(),center.y(),center.z());
            else boat = new Boat(this.agent.level(), center.x(),center.y(),center.z());
            Boat.Type type = Boat.Type.OAK;
            if (this.usedItem.is(Items.ACACIA_BOAT) || this.usedItem.is(Items.ACACIA_CHEST_BOAT)) type = Boat.Type.ACACIA;
            if (this.usedItem.is(Items.BIRCH_BOAT) || this.usedItem.is(Items.BIRCH_CHEST_BOAT)) type = Boat.Type.BIRCH;
            if (this.usedItem.is(Items.BIRCH_BOAT) || this.usedItem.is(Items.BIRCH_CHEST_BOAT)) type = Boat.Type.BIRCH;
            if (this.usedItem.is(Items.CHERRY_BOAT) || this.usedItem.is(Items.CHERRY_CHEST_BOAT)) type = Boat.Type.CHERRY;
            if (this.usedItem.is(Items.DARK_OAK_BOAT) || this.usedItem.is(Items.DARK_OAK_CHEST_BOAT)) type = Boat.Type.DARK_OAK;
            if (this.usedItem.is(Items.JUNGLE_BOAT) || this.usedItem.is(Items.JUNGLE_CHEST_BOAT)) type = Boat.Type.JUNGLE;
            if (this.usedItem.is(Items.MANGROVE_BOAT) || this.usedItem.is(Items.MANGROVE_CHEST_BOAT)) type = Boat.Type.MANGROVE;
            if (this.usedItem.is(Items.SPRUCE_BOAT) || this.usedItem.is(Items.SPRUCE_CHEST_BOAT)) type = Boat.Type.SPRUCE;
            boat.setVariant(type);
            boat.setYRot(this.agent.getDirection().toYRot());
            this.agent.level().addFreshEntity(boat);
            this.agent.level().gameEvent(this.agent, GameEvent.ENTITY_PLACE, bp.getCenter());
            this.usedItem.shrink(1);
            return true;
        }

        // WORKS
        private boolean waterBottle(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            boolean result = false;
            if (bs.is(BlockTags.CONVERTABLE_TO_MUD))
            {
                this.agent.level().gameEvent(this.agent, GameEvent.FLUID_PLACE, bp);
                this.agent.level().setBlockAndUpdate(bp, Blocks.MUD.defaultBlockState());
                if (this.agent.level() instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.SPLASH, (double)bp.getX() + level.random.nextDouble(), (double)(bp.getY() + 1), (double)bp.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                result = true;
            }
            else if (bs.is(Blocks.CAULDRON))
            {
                this.agent.level().setBlockAndUpdate(bp, Blocks.WATER_CAULDRON.defaultBlockState());
                result = true;
            }
            else if (bs.is(Blocks.WATER_CAULDRON) && bs.getValue(LayeredCauldronBlock.LEVEL) < 3)
            {
                this.agent.level().setBlockAndUpdate(bp, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, bs.getValue(LayeredCauldronBlock.LEVEL) + 1));
                result = true;
            }
            if (result)
            {
                this.agent.level().playSound(this.agent, bp, SoundEvents.BOTTLE_EMPTY, SoundSource.NEUTRAL, 1.0F, 1.0F);
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), new ItemStack(Items.GLASS_BOTTLE));
            }
            return result;
        }

        // WORKS
        private boolean bucket(BlockPos bp)
        {
            boolean result = false;
            Level level = this.agent.level();
            BlockState bs = level.getBlockState(bp);
            Fluid fluid = null;
            if (this.usedItem.is(Items.BUCKET))
            {
                ItemStack pickupItem = null;
                if (bs.getBlock() instanceof BucketPickup bucketpickup)
                {
                    pickupItem = bucketpickup.pickupBlock(level, bp, bs);
                    bucketpickup.getPickupSound(bs).ifPresent((p_150709_) ->
                    {
                        this.agent.playSound(p_150709_, 1.0F, 1.0F);
                    });
                }
                else if (bs.is(Blocks.WATER_CAULDRON) && bs.getValue(LayeredCauldronBlock.LEVEL) == 3)
                {
                    this.agent.level().playSound(null, bp, SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1,1);
                    this.agent.level().setBlockAndUpdate(bp, Blocks.CAULDRON.defaultBlockState());
                    pickupItem = new ItemStack(Items.WATER_BUCKET);
                }
                else if (bs.is(Blocks.LAVA_CAULDRON))
                {
                    this.agent.level().playSound(null, bp, SoundEvents.BUCKET_FILL_LAVA, SoundSource.NEUTRAL, 1,1);
                    this.agent.level().setBlockAndUpdate(bp, Blocks.CAULDRON.defaultBlockState());
                    pickupItem = new ItemStack(Items.LAVA_BUCKET);
                }
                else if (bs.is(Blocks.POWDER_SNOW_CAULDRON)  && bs.getValue(LayeredCauldronBlock.LEVEL) == 3)
                {
                    this.agent.level().playSound(null, bp, SoundEvents.BUCKET_FILL_POWDER_SNOW, SoundSource.NEUTRAL, 1,1);
                    this.agent.level().setBlockAndUpdate(bp, Blocks.CAULDRON.defaultBlockState());
                    pickupItem = new ItemStack(Items.POWDER_SNOW_BUCKET);
                }
                if (pickupItem != null)
                {
                    this.usedItem.shrink(1);
                    Block.popResource(level, this.agent.getOnPos().above(), pickupItem);
                    return true;
                }
            }
            else if (this.usedItem.is(Items.WATER_BUCKET)) fluid = Fluids.WATER;
            else if (this.usedItem.is(Items.LAVA_BUCKET)) fluid = Fluids.LAVA;
            if (fluid != null)
            {
                if (bs.is(Blocks.CAULDRON))
                {
                    if (fluid == Fluids.WATER)
                    {
                        BlockState bsNew = Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
                        this.agent.level().playSound(null, bp, SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 1, 1);
                        this.agent.level().setBlockAndUpdate(bp, bsNew);
                        result = true;
                    }
                    else
                    {
                        this.agent.level().playSound(null, bp, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.NEUTRAL, 1, 1);
                        this.agent.level().setBlockAndUpdate(bp, Blocks.LAVA_CAULDRON.defaultBlockState());
                        result = true;
                    }
                }
                else
                {
                    BucketItem bucket = (BucketItem) this.usedItem.getItem();
                    result = bucket.emptyContents(null, this.agent.level(), bp, null, null);
                    if (result) bucket.checkExtraContent(null, this.agent.level(), this.usedItem, bp);
                }
                if (result)
                {
                    this.usedItem.shrink(1);
                    Block.popResource(level, this.agent.getOnPos().above(), new ItemStack(Items.BUCKET));
                }
            }
            return result;
        }

        // WORKS idk how to test beehive lol
        private boolean glassBottle(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(BlockTags.BEEHIVES, (block) -> block.hasProperty(BeehiveBlock.HONEY_LEVEL) && block.getBlock() instanceof BeehiveBlock) && bs.getValue(BeehiveBlock.HONEY_LEVEL) >= 5)
            {
                ((BeehiveBlock)bs.getBlock()).releaseBeesAndResetHoneyLevel(this.agent.level(), bs, bp, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), new ItemStack(Items.HONEY_BOTTLE));
                result = true;
            }
            else if (bs.is(Blocks.WATER_CAULDRON))
            {
                LayeredCauldronBlock.lowerFillLevel(bs, this.agent.level(), bp);
                this.agent.level().playSound(null, this.agent, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                this.agent.level().gameEvent(this.agent, GameEvent.FLUID_PICKUP, bp);
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            }
            else if (this.agent.level().getFluidState(bp).is(FluidTags.WATER))
            {
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                result = true;
            }
            return result;
        }

        // WORKS
        private boolean throwable()
        {
            SoundEvent se = null;
            Level level = this.agent.level();
            if (this.usedItem.is(Items.SNOWBALL))
            {
                se = SoundEvents.SNOWBALL_THROW;
                Snowball snowball = new Snowball(level, this.agent);
                snowball.setItem(this.usedItem);
                snowball.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(snowball);
            }
            else if (this.usedItem.is(Items.EGG))
            {
                se = SoundEvents.EGG_THROW;
                ThrownEgg thrownegg = new ThrownEgg(level, this.agent);
                thrownegg.setItem(this.usedItem);
                thrownegg.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownegg);
            }
            else if (this.usedItem.is(Items.ENDER_PEARL))
            {
                se = SoundEvents.ENDER_PEARL_THROW;
                ThrownEnderpearl thrownenderpearl = new ThrownEnderpearl(level, this.agent);
                thrownenderpearl.setItem(this.usedItem);
                thrownenderpearl.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownenderpearl);
            }
            else if (this.usedItem.is(Items.EXPERIENCE_BOTTLE))
            {
                se = SoundEvents.EXPERIENCE_BOTTLE_THROW;
                ThrownExperienceBottle thrownexperiencebottle = new ThrownExperienceBottle(level, this.agent);
                thrownexperiencebottle.setItem(this.usedItem);
                thrownexperiencebottle.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), -20.0F, 0.7F, 1.0F);
                level.addFreshEntity(thrownexperiencebottle);
            }
            else
            {
                se = SoundEvents.LINGERING_POTION_THROW;
                if (this.usedItem.is(Items.SPLASH_POTION)) se = SoundEvents.SPLASH_POTION_THROW;
                ThrownPotion thrownpotion = new ThrownPotion(level, this.agent);
                thrownpotion.setItem(this.usedItem);
                thrownpotion.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), -20.0F, 0.5F, 1.0F);
                level.addFreshEntity(thrownpotion);
            }
            this.usedItem.shrink(1);
            if (se != null) level.playSound(this.agent, this.agent.getOnPos().above(), se, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            return true;
        }

        // Does not work (fishing rod does not currently support nonplayer entities)
        private boolean fishingRod()
        {
            // Pull in the bobber.
//            if (this.agent.fishing != null)
//            {
//                if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
//                this.agent.fishing.retrieve(this.usedItem);
//                this.agent.level().playSound(null, this.agent, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (this.agent.level().getRandom().nextFloat() * 0.4F + 0.8F));
//                this.agent.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
//            }
//            // Send out the bobber.
//            else
//            {
//                this.agent.level().playSound(null, this.agent, SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 1.0F, 0.4F / (this.agent.level().getRandom().nextFloat() * 0.4F + 0.8F));
//                int k = EnchantmentHelper.getFishingSpeedBonus(this.usedItem);
//                int j = EnchantmentHelper.getFishingLuckBonus(this.usedItem);
//                FishingHook hook = new FishingHook(EntityType.FISHING_BOBBER, this.agent.level());
//                hook.setOwner(this.agent);
//                float f = this.agent.getXRot();
//                float f1 = this.agent.getYRot();
//                float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//                float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
//                float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
//                float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
//                double d0 = this.agent.getX() - (double)f3 * 0.3D;
//                double d1 = this.agent.getEyeY();
//                double d2 = this.agent.getZ() - (double)f2 * 0.3D;
//                hook.moveTo(d0, d1, d2, f1, f);
//                Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
//                double d3 = vec3.length();
//                vec3 = vec3.multiply(0.6D / d3 + this.agent.getRandom().triangle(0.5D, 0.0103365D), 0.6D / d3 + this.agent.getRandom().triangle(0.5D, 0.0103365D), 0.6D / d3 + this.agent.getRandom().triangle(0.5D, 0.0103365D));
//                hook.setDeltaMovement(vec3);
//                hook.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
//                hook.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double)(180F / (float)Math.PI)));
//                hook.yRotO = hook.getYRot();
//                hook.xRotO = hook.getXRot();
//                this.agent.level().addFreshEntity(hook);
//                this.agent.gameEvent(GameEvent.ITEM_INTERACT_START);
//            }
            return false;
        }

        // WORKS
        private boolean boneMeal(BlockPos bp)
        {
            if (BoneMealItem.growCrop(this.usedItem, this.agent.level(), bp) && !BoneMealItem.growWaterPlant(this.usedItem, this.agent.level(), bp, null))
            {
                this.agent.level().levelEvent(1505, bp, 0);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean shears(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            Block block = bs.getBlock();
            if (block instanceof GrowingPlantHeadBlock growingplantheadblock)
            {
                if (!growingplantheadblock.isMaxAge(bs))
                {
                    this.agent.level().playSound(this.agent, bp, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    BlockState bsNew = growingplantheadblock.getMaxAgeState(bs);
                    this.agent.level().setBlockAndUpdate(bp, bsNew);
                    this.agent.level().gameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Context.of(this.agent, bsNew));
                    if (this.usedItem.isDamageableItem()) this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
                    return true;
                }
            }
            return false;
        }

        // WORKS
        private boolean enderEye(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.END_PORTAL_FRAME) && !bs.getValue(EndPortalFrameBlock.HAS_EYE))
            {
                BlockState bsNew = bs.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
                Block.pushEntitiesUp(bs, bsNew, this.agent.level(), bp);
                this.agent.level().setBlock(bp, bsNew, 2);
                this.agent.level().updateNeighbourForOutputSignal(bp, Blocks.END_PORTAL_FRAME);
                this.usedItem.shrink(1);
                this.agent.level().levelEvent(1503, bp, 0);
                BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = EndPortalFrameBlock.getOrCreatePortalShape().find(this.agent.level(), bp);
                if (blockpattern$blockpatternmatch != null) {
                    BlockPos blockpos1 = blockpattern$blockpatternmatch.getFrontTopLeft().offset(-3, 0, -3);
                    for(int i = 0; i < 3; ++i) {
                        for(int j = 0; j < 3; ++j) {
                            this.agent.level().setBlock(blockpos1.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
                        }
                    }
                    this.agent.level().globalLevelEvent(1038, blockpos1.offset(1, 0, 1), 0);
                }
            }
            else
            {
                ServerLevel level = (ServerLevel)this.agent.level();
                BlockPos structureBp = level.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, this.agent.blockPosition(), 100, false);
                if (structureBp != null)
                {
                    EyeOfEnder eyeofender = new EyeOfEnder(this.agent.level(), this.agent.getX(), this.agent.getEyeY(), this.agent.getZ());
                    eyeofender.setItem(this.usedItem);
                    eyeofender.signalTo(structureBp);
                    this.agent.level().gameEvent(GameEvent.PROJECTILE_SHOOT, eyeofender.position(), GameEvent.Context.of(this.agent));
                    this.agent.level().addFreshEntity(eyeofender);
                    this.agent.level().playSound((Player)null, this.agent.getX(), this.agent.getY(), this.agent.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (this.agent.level().getRandom().nextFloat() * 0.4F + 0.8F));
                    this.agent.level().levelEvent((Player)null, 1003, this.agent.blockPosition(), 0);
                    this.usedItem.shrink(1);
                    return true;
                }
            }
            return false;
        }

        // WORKS
        private boolean rocket(BlockPos bp)
        {
            ItemStack itemstack = this.usedItem;
            Vec3 vec3 = bp.getCenter();
            Direction direction = Direction.DOWN;
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(this.agent.level(), this.agent, vec3.x + (double)direction.getStepX() * 0.15D, vec3.y + (double)direction.getStepY() * 0.15D, vec3.z + (double)direction.getStepZ() * 0.15D, itemstack);
            this.agent.level().addFreshEntity(fireworkrocketentity);
            itemstack.shrink(1);
            return true;
        }

        // WORKS
        private boolean lead(BlockPos bp)
        {
            if (this.agent.level().getBlockState(bp).is(BlockTags.FENCES))
            {
                LeashFenceKnotEntity leashfenceknotentity = null;
                boolean flag = false;
                double d0 = 7.0D;
                int i = bp.getX();
                int j = bp.getY();
                int k = bp.getZ();
                for (Mob mob : this.agent.level().getEntitiesOfClass(Mob.class, new AABB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0)))
                {
                    if (mob.getLeashHolder() == this.agent)
                    {
                        if (leashfenceknotentity == null)
                        {
                            leashfenceknotentity = LeashFenceKnotEntity.getOrCreateKnot(this.agent.level(), bp);
                            leashfenceknotentity.playPlacementSound();
                        }
                        mob.setLeashedTo(leashfenceknotentity, true);
                        flag = true;
                    }
                }
                if (flag)
                {
                    this.agent.level().gameEvent(GameEvent.BLOCK_ATTACH, bp, GameEvent.Context.of(this.agent));
                    this.usedItem.shrink(1);
                    return true;
                }
            }
            return false;
        }

        // WORKS
        private boolean record(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.JUKEBOX) && !bs.getValue(JukeboxBlock.HAS_RECORD))
            {
                BlockEntity blockentity = this.agent.level().getBlockEntity(bp);
                if (blockentity instanceof JukeboxBlockEntity)
                {
                    JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
                    jukeboxblockentity.setFirstItem(this.usedItem.copy());
                    this.agent.level().gameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Context.of(this.agent, bs));
                    this.usedItem.shrink(1);
                    return true;
                }
            }
            return false;
        }

        // WORKS
        private boolean trident()
        {
            LivingEntity owner = this.agent.getOwner();
            if (owner == null) owner = this.agent;
            this.usedItem.hurtAndBreak(1, this.agent, agent -> this.agent.level().broadcastEntityEvent(agent, EntityGolemAgent.ENTITY_EVENT_TOOL_BROKEN));
            ThrownTrident throwntrident = new ThrownTrident(this.agent.level(), owner, this.usedItem);
            throwntrident.setPos(this.agent.getEyePosition());
            throwntrident.shootFromRotation(this.agent, this.agent.getXRot(), this.agent.getYRot(), 0.0F, 2.5F, 1.0F);
            this.agent.level().addFreshEntity(throwntrident);
            this.agent.level().playSound(null, throwntrident, SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, 1.0F, 1.0F);
            this.usedItem.shrink(1);
            return true;
        }

        // WORKS
        private boolean instrument()
        {
            TagKey<Instrument> instruments = null;
            if (this.usedItem.is(Items.GOAT_HORN))
            {
                instruments = InstrumentTags.GOAT_HORNS;
            }
            if (instruments != null)
            {
                Optional<? extends Holder<Instrument>> optional = Optional.empty();
                CompoundTag compoundtag = this.usedItem.getTag();
                if (compoundtag != null && compoundtag.contains("instrument", 8)) {
                    ResourceLocation resourcelocation = ResourceLocation.tryParse(compoundtag.getString("instrument"));
                    if (resourcelocation != null) {
                        optional = BuiltInRegistries.INSTRUMENT.getHolder(ResourceKey.create(Registries.INSTRUMENT, resourcelocation));
                    }
                }
                if (optional.isEmpty())
                {
                    Iterator<Holder<Instrument>> iterator = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(instruments).iterator();
                    optional = iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
                }

                Instrument instrument = optional.get().value();
                SoundEvent soundevent = instrument.soundEvent().value();
                float f = instrument.range() / 16.0F;
                this.agent.level().playSound(null, this.agent, soundevent, SoundSource.RECORDS, f, 1.0F);
                this.agent.level().gameEvent(GameEvent.INSTRUMENT_PLAY, this.agent.position(), GameEvent.Context.of(this.agent));
                return true;
            }
            return false;
        }

        // WORKS
        private boolean honeycomb(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            Optional<BlockState> optional = HoneycombItem.getWaxed(bs);
            if (optional.isPresent())
            {
                this.agent.level().setBlockAndUpdate(bp, optional.get());
                this.agent.level().levelEvent(3003, bp, 0);
                this.usedItem.shrink(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean compass(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.LODESTONE))
            {
                this.agent.level().playSound(this.agent, bp, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                CompoundTag compoundTag = this.usedItem.getOrCreateTag();
                compoundTag.put("LodestonePos", NbtUtils.writeBlockPos(bp));
                Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, this.agent.level().dimension()).resultOrPartial(LogUtils.getLogger()::error).ifPresent((p_40731_) -> {
                    compoundTag.put("LodestoneDimension", p_40731_);
                });
                compoundTag.putBoolean("LodestoneTracked", true);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), this.usedItem.copy());
                this.usedItem.shrink(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean glowstone(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.RESPAWN_ANCHOR) && (bs.getValue(RespawnAnchorBlock.CHARGE) != 4))
            {
                RespawnAnchorBlock.charge(null, this.agent.level(), bp, bs);
                this.usedItem.shrink(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean armorStand(BlockPos bp)
        {
            ServerLevel level = (ServerLevel) this.agent.level();
            Consumer<ArmorStand> consumer = EntityType.appendDefaultStackConfig((entity) -> {
                entity.setYRot(this.agent.getDirection().toYRot());
            }, level, this.usedItem, null);
            ArmorStand armorstand = EntityType.ARMOR_STAND.create(level, this.usedItem.getTag(), consumer, bp, MobSpawnType.SPAWN_EGG, false, false);
            if (armorstand != null)
            {
                armorstand.moveTo(armorstand.getX(), armorstand.getY(), armorstand.getZ(), armorstand.getYRot(), 0.0F);
                level.addFreshEntityWithPassengers(armorstand);
                GolemFirstStoneMod.LOGGER.info("" + armorstand);
                level.playSound(null, this.agent, SoundEvents.ARMOR_STAND_PLACE, SoundSource.NEUTRAL, 0.75F, 0.8F);
                armorstand.gameEvent(GameEvent.ENTITY_PLACE, this.agent);
                this.usedItem.shrink(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean minecart(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            AbstractMinecart.Type type = AbstractMinecart.Type.RIDEABLE;
            if (this.usedItem.is(Items.CHEST_MINECART)) type = AbstractMinecart.Type.CHEST;
            if (this.usedItem.is(Items.TNT_MINECART)) type = AbstractMinecart.Type.TNT;
            if (this.usedItem.is(Items.FURNACE_MINECART)) type = AbstractMinecart.Type.FURNACE;
            if (this.usedItem.is(Items.HOPPER_MINECART)) type = AbstractMinecart.Type.HOPPER;
            if (this.usedItem.is(Items.COMMAND_BLOCK_MINECART)) type = AbstractMinecart.Type.COMMAND_BLOCK;
            if (bs.is(BlockTags.RAILS))
            {
                RailShape railshape = bs.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)bs.getBlock()).getRailDirection(bs, this.agent.level(), bp, null) : RailShape.NORTH_SOUTH;
                double d0 = 0.0D;
                if (railshape.isAscending()) {
                    d0 = 0.5D;
                }
                AbstractMinecart abstractminecart = AbstractMinecart.createMinecart(this.agent.level(), (double)bp.getX() + 0.5D, (double)bp.getY() + 0.0625D + d0, (double)bp.getZ() + 0.5D, type);
                if (this.usedItem.hasCustomHoverName()) abstractminecart.setCustomName(this.usedItem.getHoverName());
                this.agent.level().addFreshEntity(abstractminecart);
                this.agent.level().gameEvent(GameEvent.ENTITY_PLACE, bp, GameEvent.Context.of(this.agent, this.agent.level().getBlockState(bp.below())));
                this.usedItem.shrink(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean book(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.LECTERN))
            {
                LecternBlock.tryPlaceBook(this.agent, this.agent.level(), bp, bs, this.usedItem);
                return true;
            }
            else if (this.usedItem.is(Items.WRITABLE_BOOK))
            {
                if (!this.usedItem.hasTag())
                {
                    ItemStack newBook = new ItemStack(Items.WRITTEN_BOOK);

                    newBook.addTagElement("author", StringTag.valueOf(this.agent.getDisplayName().getString()));
                    newBook.addTagElement("title", StringTag.valueOf(Component.translatable("book.golemfirststonemod.book_1").getString()));
                    newBook.addTagElement("filtered_title", StringTag.valueOf(Component.translatable("book.golemfirststonemod.book_1.filtered_title").getString()));

                    List<String> pages = Lists.newArrayList();
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_1").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_2").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_3").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_4").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_5").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_6").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_7").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_8").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_9").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_10").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_11").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_12").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_13").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_14").getString());
                    pages.add(Component.translatable("book.golemfirststonemod.book_1.page_15").getString());
                    ListTag listtag = new ListTag();
                    pages.stream().map(StringTag::valueOf).forEach(listtag::add);
                    newBook.addTagElement("pages", listtag);

                    Block.popResource(this.agent.level(), this.agent.getOnPos().above(), newBook);
                    this.usedItem.shrink(1);
                }
            }
            return false;
        }

        // WORKS
        private boolean chiseledBookshelf(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            BlockEntity be = this.agent.level().getBlockEntity(bp);
            if (be instanceof ChiseledBookShelfBlockEntity shelfBe)
            {
                if (this.usedItem.is(ItemTags.BOOKSHELF_BOOKS))
                {
                    for (int i = 0; i < 6; i++)
                    {
                        if (!bs.getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i)))
                        {
                            SoundEvent soundevent = this.usedItem.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
                            shelfBe.setItem(i, this.usedItem.split(1));
                            this.agent.level().playSound(null, bp, soundevent, SoundSource.NEUTRAL, 1.0F, 1.0F);

                            this.agent.level().gameEvent(this.agent, GameEvent.BLOCK_CHANGE, bp);
                            return true;
                        }
                    }
                }
                for (int i = 0; i < 6; i++)
                {
                    if (bs.getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i)))
                    {
                        ItemStack newBook = shelfBe.removeItem(i, 1);
                        SoundEvent soundevent = newBook.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
                        this.agent.level().playSound(null, bp, soundevent, SoundSource.NEUTRAL, 1.0F, 1.0F);

                        this.agent.level().gameEvent(this.agent, GameEvent.BLOCK_CHANGE, bp);
                        Block.popResource(this.agent.level(), this.agent.getOnPos().above(), newBook);
                        return true;
                    }
                }
            }
            return false;
        }

        // WORKS
        private boolean washInCauldron(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            ItemStack newItem = null;
            if (this.usedItem.is(ItemTags.BANNERS) && BannerBlockEntity.getPatternCount(this.usedItem) > 0)
            {
                newItem = this.usedItem.copyWithCount(1);
                BannerBlockEntity.removeLastPattern(newItem);
            }
            else if (Block.byItem(this.usedItem.getItem()) instanceof ShulkerBoxBlock && !this.usedItem.is(Items.SHULKER_BOX))
            {
                newItem = new ItemStack(Items.SHULKER_BOX);
                if (this.usedItem.hasTag()) newItem.setTag(this.usedItem.getTag().copy());
            }
            else if (this.usedItem.getItem() instanceof DyeableLeatherItem dyeableLeatherItem && dyeableLeatherItem.hasCustomColor(this.usedItem))
            {
                newItem = this.usedItem.copy();
                dyeableLeatherItem.clearColor(newItem);
            }
            if (newItem != null)
            {
                LayeredCauldronBlock.lowerFillLevel(bs, this.agent.level(), bp);
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), newItem);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean snowBucket(BlockPos bp)
        {
            BlockState bs = this.agent.level().getBlockState(bp);
            if (bs.is(Blocks.CAULDRON) || (bs.is(Blocks.POWDER_SNOW_CAULDRON) && bs.getValue(LayeredCauldronBlock.LEVEL) < 3))
            {
                BlockState bsNew = Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
                this.agent.level().playSound(null, bp, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, SoundSource.NEUTRAL, 1, 1);
                this.agent.level().setBlockAndUpdate(bp, bsNew);
                this.usedItem.shrink(1);
                Block.popResource(this.agent.level(), this.agent.getOnPos().above(), new ItemStack(Items.BUCKET));
                return true;
            }
            return false;
        }
    }

    class InstructionAttack extends Instruction
    {
        private final ItemStack attackingItem;
        private int ticks = 10;
        public InstructionAttack(EntityGolemAgent agent, ItemStack attackingItem)
        {
            super(agent);
            this.attackingItem = attackingItem;
        }

        @Override
        public boolean isDone()
        {
            return --ticks <= 0;
        }

        @Override
        public void start()
        {
            super.start();
            this.agent.swingArm();
            BlockPos bp = this.getBlockPosForward();
            TargetingConditions tp = TargetingConditions.forCombat();
            Vec3 center = bp.getCenter();
            AABB aabb = AABB.ofSize(center, 1,1,1);
            LivingEntity targetEntity = this.agent.level().getNearestEntity(LivingEntity.class, tp, this.agent, center.x(),center.y(),center.z(), aabb);
            if (targetEntity != null && this.agent.canAttack(targetEntity))
            {
                if (this.attackingItem != null && !(this.attackingItem.isEmpty()))
                {
                    this.attackingItem.getItem().hurtEnemy(this.attackingItem, targetEntity, this.agent);
                }
                this.agent.doHurtTarget(targetEntity);
            }
            else
            {
                List<Entity> entityList = this.agent.level().getEntities(this.agent, AABB.ofSize(bp.getCenter(), 1, 1, 1));
                if (!entityList.isEmpty())
                {
                    Entity entity = entityList.get(0);
                    if (entity instanceof LivingEntity livingEntity)
                    {
                        if (this.agent.canAttack(livingEntity)) this.agent.doHurtTarget(entity);
                    }
                    else this.agent.doHurtTarget(entity);
                }
            }
        }
    }

    class InstructionLoop extends Instruction
    {
        private final int loopCount;
        public InstructionLoop(EntityGolemAgent agent, int loopCount)
        {
            super(agent);
            this.loopCount = loopCount;
        }

        public int getLoopCount()
        {
            return this.loopCount;
        }
    }

}
