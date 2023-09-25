package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.google.common.collect.Lists;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.item.item.ItemInstruction;
import com.kitisplode.golemfirststonemod.mixin.MixinAxeItemAccessor;
import com.kitisplode.golemfirststonemod.mixin.MixinShovelItemAccessor;
import com.kitisplode.golemfirststonemod.mixin.MixinItemFrameEntityAccessor;
import com.kitisplode.golemfirststonemod.mixin.MixinMooshroomEntityAccessor;
import com.kitisplode.golemfirststonemod.mixin.MixinTntBlockAccessor;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import com.kitisplode.golemfirststonemod.util.ModTags;
import com.mojang.logging.LogUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

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
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart()
    {
        if (this.agent.getActive()) return true;
        return false;
    }

    @Override
    public boolean shouldContinue()
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

    public boolean shouldRunEveryTick() {
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
        int inventorySize = this.agent.getInventory().size();
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 1; i < inventorySize; i++)
        {
            items.add(this.agent.getInventory().getStack(i));
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
        return itemStack.isOf(Items.SHULKER_BOX) || itemStack.isOf(Items.WHITE_SHULKER_BOX) || itemStack.isOf(Items.ORANGE_SHULKER_BOX)
                || itemStack.isOf(Items.MAGENTA_SHULKER_BOX) || itemStack.isOf(Items.LIGHT_BLUE_SHULKER_BOX) || itemStack.isOf(Items.YELLOW_SHULKER_BOX)
                || itemStack.isOf(Items.LIME_SHULKER_BOX) || itemStack.isOf(Items.PINK_SHULKER_BOX) || itemStack.isOf(Items.GRAY_SHULKER_BOX)
                || itemStack.isOf(Items.LIGHT_GRAY_SHULKER_BOX) || itemStack.isOf(Items.CYAN_SHULKER_BOX) || itemStack.isOf(Items.PURPLE_SHULKER_BOX)
                || itemStack.isOf(Items.BLUE_SHULKER_BOX) || itemStack.isOf(Items.BROWN_SHULKER_BOX) || itemStack.isOf(Items.GREEN_SHULKER_BOX)
                || itemStack.isOf(Items.RED_SHULKER_BOX) || itemStack.isOf(Items.BLACK_SHULKER_BOX);
    }

    private InstructionSet shulkerBoxToInstruction(ItemStack itemStack)
    {
        int setLoops = -1;
        InstructionSet set = null;
        NbtCompound tag = itemStack.getNbt();
        if (tag == null) return null;
        NbtCompound boxTag = tag.getCompound("BlockEntityTag");
        if (boxTag.contains("Items"))
        {
            ArrayList<Instruction> list = new ArrayList<>();
            ArrayList<ItemStack> itemsInBox = new ArrayList<>();
            NbtList listtag = boxTag.getList("Items", 10);
            for(int j = 0; j < listtag.size(); ++j) {
                NbtCompound itemTag = listtag.getCompound(j);
                ItemStack innerItem = ItemStack.fromNbt(itemTag);
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
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_MOVE_FORWARD)) return new InstructionMoveForward(this.agent, itemStack.getCount());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_TURN_LEFT_90)) return new InstructionTurn(this.agent, -itemStack.getCount());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_TURN_RIGHT_90)) return new InstructionTurn(this.agent, itemStack.getCount());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_USE_BLOCK)) return new InstructionUseBlock(this.agent);
        if (this.itemIsShulkerBox(itemStack)) return this.shulkerBoxToInstruction(itemStack);
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_IF_BLOCK) || itemStack.isOf(ModItems.ITEM_INSTRUCTION_IF_SOLID))
            return this.ifItemToInstruction(items);
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_PLACE_BLOCK)) return this.placeItemToInstruction(items);
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_BREAK_BLOCK)) return new InstructionDestroyBlock(this.agent, this.agent.getHeldItem());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_USE_TOOL)) return new InstructionUseTool(this.agent, this.agent.getHeldItem());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_ATTACK)) return new InstructionAttack(this.agent, this.agent.getHeldItem());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_LOOP)) return new InstructionLoop(this.agent, itemStack.getCount());
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_NOT)) return this.notItemToInstruction(items);
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

        GolemFirstStoneMod.LOGGER.info("" + tempList);
        Instruction nextInstruction = this.pullInstructionFromFirstItem(tempList);
        if (nextInstruction == null) return null;
        ArrayList<Instruction> list = new ArrayList<>();
        list.add(nextInstruction);
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_IF_BLOCK)) return new InstructionIfCheckBlock(this.agent, Block.getBlockFromItem(items.get(1).getItem()), list);
        if (itemStack.isOf(ModItems.ITEM_INSTRUCTION_IF_SOLID)) return new InstructionIfCheckSolid(this.agent, list);
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
            Direction direction = Direction.getEntityFacingOrder(this.agent)[0];
            return this.agent.getBlockPos().offset(direction);
        }
    }

    class InstructionMoveForward extends Instruction
    {
        private final int distance;
        private BlockPos blockPos;
        private int ticks = 10;
        private Vec3d previousPos;

        public InstructionMoveForward(EntityGolemAgent agent, int distance)
        {
            super(agent);
            this.distance = distance;
        }

        public boolean isDone()
        {
            if (this.agent.getPos().equals(this.previousPos))
            {
                this.ticks -= 1;
                if (this.ticks <= 0) return true;
            }
            this.previousPos = this.agent.getPos();
            return this.blockPos.toCenterPos().squaredDistanceTo(new Vec3d(this.agent.getX(), this.blockPos.toCenterPos().getY(), this.agent.getZ())) <= MathHelper.square(0.1f);
        }

        public void start()
        {
            super.start();
            Direction direction = Direction.getEntityFacingOrder(this.agent)[0];
            this.blockPos = this.agent.getSteppingPos().add(direction.getVector().multiply(this.distance));
            this.previousPos = this.agent.getPos();
        }

        public void tick()
        {

            Vec3d center = this.blockPos.toCenterPos();
            Vec3d vTo = center.subtract(this.agent.getPos()).multiply(1,0,1);
            if (vTo.horizontalLengthSquared() <= MathHelper.square(0.5f))
            {
                this.agent.setVelocity(0, this.agent.getVelocity().getY(), 0);
                this.agent.setPos(center.getX(), this.agent.getY(), center.getZ());
                return;
            }
            vTo = vTo.normalize().multiply(0.125f);
            this.agent.setVelocity(vTo.getX(), this.agent.getVelocity().getY(), vTo.getZ());
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
            if (this.previousDirection == this.agent.getYaw())
            {
                this.ticks -= 1;
                if (this.ticks <= 0) return true;
            }
            this.previousDirection = this.agent.getYaw();
            return Math.abs(this.directionToTurn - this.agent.getYaw()) <= 5;
        }

        public void start()
        {
            super.start();
            float startingAngle = Math.round(this.agent.getYaw() / 90.0f) * 90;
            this.directionToTurn = startingAngle + 90.0f * this.directionMultiplier;
            if (this.directionToTurn < 0.0f) this.directionToTurn += 360.0f;
            else if (this.directionToTurn >= 360.0f) this.directionToTurn -= 360.0f;
            this.previousDirection = this.agent.getYaw();
        }

        public void tick()
        {
            this.agent.setYaw(this.rotateTowards(this.agent.getYaw(), this.directionToTurn, this.agent.getMaxLookYawChange()));
            this.agent.setHeadYaw(this.agent.getYaw());
            this.agent.setBodyYaw(this.agent.getYaw());
        }

        private float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
            float f = MathHelper.subtractAngles(pFrom, pTo);
            float f1 = MathHelper.clamp(f, -pMaxDelta, pMaxDelta);
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
            return this.agent.getWorld().getBlockState(bp).isOf(this.blockType);
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
            return this.agent.getWorld().getBlockState(bp).isOpaque();
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
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (!bs.isIn(ModTags.Blocks.AGENT_CAN_INTERACT)) return;
            this.agent.swingArm();
            bs.onUse(this.agent.getWorld(), null, Hand.MAIN_HAND, ExtraMath.playerRaycast(this.agent.getWorld(), this.agent, RaycastContext.FluidHandling.ANY, 1));
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
                BlockState bs = blockItem.getBlock().getDefaultState();
                if (this.agent.getWorld().getBlockState(bp).isReplaceable())
                {
                    this.agent.getWorld().setBlockState(bp, bs, 11);
                    this.agent.getWorld().emitGameEvent(this.agent, GameEvent.BLOCK_PLACE, bp);
                    itemStack.decrement(1);
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
                BlockState bs = this.agent.getWorld().getBlockState(this.targetBlock);
                float itemSpeed = destroyingItem.getMiningSpeedMultiplier(bs);
                float destroySpeed = bs.getHardness(this.agent.getWorld(), this.targetBlock);
                if (destroySpeed == -1.0f)
                {
                    this.cancel = true;
                    return;
                }
                float destroyInc = itemSpeed / destroySpeed / 15;
                this.destroyProgress += destroyInc;
                if (this.destroyProgress >= 1.0f)
                {
                    if (this.destroyingItem.isDamageable())
                    {
                        this.destroyingItem.damage(1, this.agent, agent -> {
                            agent.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);});
                    }
                    this.agent.getWorld().breakBlock(this.targetBlock, true, this.agent);
                    this.destroyProgress = 0.0f;
                    this.cancel = true;
                }
                else
                {
                    BlockSoundGroup soundtype = bs.getSoundGroup();
                    MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(soundtype.getHitSound(), SoundCategory.NEUTRAL, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, SoundInstance.createRandom(), this.targetBlock));
                }
                this.agent.getWorld().setBlockBreakingInfo(this.agent.getId(), this.targetBlock, this.getDestroyStage());
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
            List<Entity> entityList = this.agent.getWorld().getOtherEntities(this.agent, Box.of(bp.toCenterPos(), 1, 1, 1));
            if (!entityList.isEmpty())
            {
                Entity targetEntity = entityList.get(0);
                itemUsed = this.entityInteract(targetEntity);
            }
            // Otherwise, perform the block actions instead.
            if (!itemUsed)
            {
                if (this.usedItem.getItem() instanceof HoeItem)
                {
                    itemUsed = hoe(bp);
                    if (!itemUsed) itemUsed = hoe(bp.down());
                }
                if (this.usedItem.getItem() instanceof AxeItem)
                {
                    itemUsed = axe(bp);
                    if (!itemUsed) itemUsed = axe(bp.down());
                }
                if (this.usedItem.getItem() instanceof ShovelItem)
                {
                    itemUsed = shovel(bp);
                    if (!itemUsed) itemUsed = shovel(bp.down());
                }
                if (this.usedItem.isOf(Items.FLINT_AND_STEEL) || this.usedItem.isOf(Items.FIRE_CHARGE))
                    itemUsed = flintAndSteel(bp);
                if (this.usedItem.isIn(ItemTags.BOATS)) itemUsed = boat(bp);
                // BOW?
                if (this.usedItem.isOf(Items.POTION) && PotionUtil.getPotion(this.usedItem) == Potions.WATER) itemUsed = waterBottle(bp);
                if (this.usedItem.getItem() instanceof BucketItem) itemUsed = bucket(bp);
                if (this.usedItem.isOf(Items.GLASS_BOTTLE)) itemUsed = glassBottle(bp);
                if (this.usedItem.isOf(Items.SNOWBALL)
                        || this.usedItem.isOf(Items.EGG)
                        || this.usedItem.isOf(Items.ENDER_PEARL)
                        || this.usedItem.isOf(Items.EXPERIENCE_BOTTLE)
                        || this.usedItem.getItem() instanceof ThrowablePotionItem)
                    itemUsed = throwable();
                if (this.usedItem.isOf(Items.FISHING_ROD)) itemUsed = fishingRod();
                if (this.usedItem.isOf(Items.BONE_MEAL))
                {
                    itemUsed = boneMeal(bp);
                    if (!itemUsed) itemUsed = boneMeal(bp.down());
                }
                if (this.usedItem.isOf(Items.SHEARS)) itemUsed = shears(bp);
                if (this.usedItem.isOf(Items.ENDER_EYE)) itemUsed = enderEye(bp);
                // MAP?
                if (this.usedItem.isOf(Items.FIREWORK_ROCKET)) itemUsed = rocket(bp);
                if (this.usedItem.isOf(Items.LEAD)) itemUsed = lead(bp);
                // SHIELD?
                if (this.usedItem.getItem() instanceof MusicDiscItem) itemUsed = record(bp);
                if (this.usedItem.isOf(Items.TRIDENT)) itemUsed = trident();
                // CROSSBOW?
                if (this.usedItem.getItem() instanceof GoatHornItem) itemUsed = instrument();
                if (this.usedItem.isOf(Items.HONEYCOMB)) itemUsed = honeycomb(bp);
                if (this.usedItem.isOf(Items.COMPASS)) itemUsed = compass(bp);
                // BRUSH?
                if (this.usedItem.isOf(Items.GLOWSTONE)) itemUsed = glowstone(bp);
                if (this.usedItem.isOf(Items.ARMOR_STAND)) itemUsed = armorStand(bp);
                if (this.usedItem.getItem() instanceof MinecartItem) itemUsed = minecart(bp);
                if (this.usedItem.isOf(Items.WRITABLE_BOOK) || this.usedItem.isOf(Items.WRITTEN_BOOK)) itemUsed = book(bp);
                if (this.agent.getWorld().getBlockState(bp).isOf(Blocks.CHISELED_BOOKSHELF)) itemUsed = chiseledBookshelf(bp);
                if (!itemUsed && this.agent.getWorld().getBlockState(bp).isOf(Blocks.WATER_CAULDRON)) itemUsed = washInCauldron(bp);
                if (!itemUsed && this.usedItem.isOf(Items.POWDER_SNOW_BUCKET)) itemUsed = snowBucket(bp);
                if (!itemUsed && this.usedItem.getItem() instanceof BlockItem blockItem)
                {
                    BlockState bs = blockItem.getBlock().getDefaultState();
                    if (this.agent.getWorld().getBlockState(bp).isReplaceable())
                    {
                        this.agent.getWorld().setBlockState(bp, bs, 11);
                        this.agent.getWorld().emitGameEvent(this.agent, GameEvent.BLOCK_PLACE, bp);
                        this.usedItem.decrement(1);
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
                BlockState bs = this.agent.getWorld().getBlockState(bp);
                if (!bs.isIn(ModTags.Blocks.AGENT_CAN_INTERACT)) return;
                this.agent.swingArm();
                bs.onUse(this.agent.getWorld(), null, Hand.MAIN_HAND, ExtraMath.playerRaycast(this.agent.getWorld(), this.agent, RaycastContext.FluidHandling.ANY, 1));
            }
        }

        private boolean entityInteract(Entity targetEntity)
        {
            boolean result = false;
            // Lead mob
            // WORKS
            if (this.usedItem.isOf(Items.LEAD))
            {
                if (targetEntity instanceof MobEntity mob && mob.isAlive() && !mob.isLeashed())
                {
                    mob.attachLeash(this.agent, true);
                    return true;
                }
            }
            // Name mob
            // WORKS
            if (this.usedItem.isOf(Items.NAME_TAG))
            {
                if (this.usedItem.hasCustomName() && !(targetEntity instanceof PlayerEntity) && targetEntity.isAlive())
                {
                    targetEntity.setCustomName(this.usedItem.getName());
                    if (targetEntity instanceof MobEntity mob) {
                        mob.setPersistent();
                    }
                    this.usedItem.decrement(1);
                    return true;
                }
            }
            // Dye sheep
            // WORKS
            if (this.usedItem.getItem() instanceof DyeItem)
            {
                DyeItem dye = (DyeItem) this.usedItem.getItem();
                if (targetEntity instanceof SheepEntity sheep && sheep.isAlive() && !sheep.isSheared() && sheep.getColor() != dye.getColor())
                {
                    sheep.setColor(dye.getColor());
                    this.usedItem.decrement(1);
                    this.agent.getWorld().playSound(sheep, sheep.getBlockPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    result = true;
                }
            }
            // Shear sheep, mooshroom, snow golem
            // WORKS
            if (this.usedItem.isOf(Items.SHEARS))
            {
                if (targetEntity instanceof SheepEntity sheep && sheep.isAlive() && sheep.isShearable())
                {
                    sheep.sheared(SoundCategory.NEUTRAL);
                    sheep.emitGameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (targetEntity instanceof MooshroomEntity cow && cow.isAlive() && cow.isShearable())
                {
                    cow.sheared(SoundCategory.NEUTRAL);
                    cow.emitGameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (targetEntity instanceof SnowGolemEntity golem && golem.isAlive() && golem.isShearable())
                {
                    golem.sheared(SoundCategory.NEUTRAL);
                    golem.emitGameEvent(GameEvent.SHEAR, this.agent);
                    result = true;
                }
                if (result && this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            }
            // Milk cow
            // WORKS
            if (this.usedItem.isOf(Items.BUCKET))
            {
                if (targetEntity instanceof CowEntity cow && cow.isAlive() && !cow.isBaby())
                {
                    this.agent.getWorld().playSound(cow, cow.getBlockPos(), SoundEvents.ENTITY_COW_MILK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    this.usedItem.decrement(1);
                    Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), new ItemStack(Items.MILK_BUCKET));
                    result = true;
                }
            }
            // Soup cow
            // WORKS
            if (this.usedItem.isOf(Items.BOWL))
            {
                if (targetEntity instanceof MooshroomEntity cow && cow.isAlive() && !cow.isBaby())
                {
                    ItemStack is;
                    SoundEvent se;
                    MixinMooshroomEntityAccessor mushroomCow = (MixinMooshroomEntityAccessor) cow;
                    if (mushroomCow.getEffect() != null)
                    {
                        is = new ItemStack(Items.SUSPICIOUS_STEW);
                        SuspiciousStewItem.addEffectToStew(is, mushroomCow.getEffect(), mushroomCow.getEffectDuration());
                        mushroomCow.setEffect(null);
                        mushroomCow.setEffectDuration(0);
                        se = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
                    }
                    else
                    {
                        is = new ItemStack(Items.MUSHROOM_STEW);
                        se = SoundEvents.ENTITY_MOOSHROOM_MILK;
                    }
                    cow.playSound(se, 1.0F, 1.0F);
                    this.usedItem.decrement(1);
                    Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), is);
                    result = true;
                }
            }
            // Saddle animal
            // WORKS
            if (this.usedItem.isOf(Items.SADDLE))
            {
                if (targetEntity instanceof Saddleable saddleable && targetEntity.isAlive() && saddleable.canBeSaddled() && !saddleable.isSaddled())
                {
                    saddleable.saddle(SoundCategory.NEUTRAL);
                    this.usedItem.decrement(1);
                    result = true;
                }
            }
            // Armor animal
            // WORKS
            if (!result && targetEntity instanceof AbstractHorseEntity horse
                    && horse.isAlive() && horse.hasArmorSlot() && horse.isHorseArmor(this.usedItem) && !horse.hasArmorInSlot() && horse.isTame())
            {
                if (this.usedItem.isIn(ItemTags.WOOL_CARPETS)
                        || this.usedItem.isOf(Items.LEATHER_HORSE_ARMOR) || this.usedItem.isOf(Items.IRON_HORSE_ARMOR)
                        || this.usedItem.isOf(Items.GOLDEN_HORSE_ARMOR) || this.usedItem.isOf(Items.DIAMOND_HORSE_ARMOR))
                {
                    horse.getStackReference(401).set(this.usedItem.split(1));
                    result = true;
                }
                if (this.usedItem.isOf(Items.CHEST) && targetEntity instanceof AbstractDonkeyEntity chestedHorse && !chestedHorse.hasChest())
                {
                    chestedHorse.getStackReference(499).set(this.usedItem.split(1));
                    result = true;
                }
            }
            // Breed animal
            // WORKS
            if (!result && targetEntity instanceof AnimalEntity animal && animal.isBreedingItem(this.usedItem))
            {
                int i = animal.getBreedingAge();
                if (i == 0 && animal.canEat())
                {
                    animal.lovePlayer(null);
                    this.usedItem.decrement(1);
                    result = true;
                }
                else if (animal.isBaby())
                {
                    animal.growUp(PassiveEntity.toGrowUpAge(-i), true);
                    this.usedItem.decrement(1);
                    result = true;
                }
            }
            // Armor others
            // WORKS
            if (!result && this.usedItem.getItem() instanceof ArmorItem)
            {
                if (targetEntity instanceof LivingEntity livingEntity && livingEntity.canEquip(this.usedItem))
                {
                    EquipmentSlot equipmentslot = LivingEntity.getPreferredEquipmentSlot(this.usedItem);
                    ItemStack itemstack = this.usedItem.split(1);
                    livingEntity.equipStack(equipmentslot, itemstack);
                    if (targetEntity instanceof MobEntity mob)
                    {
                        mob.setEquipmentDropChance(equipmentslot, 2.0F);
                        mob.setPersistent();
                    }
                    result = true;
                }
            }
            // Enter boat
            // Enter minecart
            // Feed powered minecart
            // Explode TNT minecart
            // Repair iron golem
            if (!result && this.usedItem.isOf(Items.IRON_INGOT) && targetEntity.getType().equals(EntityType.IRON_GOLEM) && targetEntity instanceof IronGolemEntity golem && targetEntity.isAlive() && golem.getHealth() == golem.getMaxHealth())
            {
                golem.heal(25);
                golem.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, 1.0F + (this.agent.getRandom().nextFloat() - this.agent.getRandom().nextFloat()) * 0.2F);
                this.usedItem.decrement(1);
                result = true;
            }
            // Item frame
            // WORKS
            if (!result && targetEntity instanceof ItemFrameEntity itemFrame && targetEntity.isAlive())
            {
                MixinItemFrameEntityAccessor itemFrameAccessor = (MixinItemFrameEntityAccessor) itemFrame;
                if (!itemFrameAccessor.getFixed())
                {
                    if (!itemFrame.getHeldItemStack().isEmpty())
                    {
                        itemFrame.playSound(itemFrame.getRotateItemSound(), 1.0F, 1.0F);
                        itemFrame.setRotation(itemFrame.getRotation() + 1);
                        itemFrame.emitGameEvent(GameEvent.BLOCK_CHANGE, this.agent);
                        result = true;
                    }
                    else
                    {
                        boolean canContinue = true;
                        if (this.usedItem.isOf(Items.FILLED_MAP)) {
                            MapState mapitemsaveddata = FilledMapItem.getMapState(this.usedItem, this.agent.getWorld());
                            if (mapitemsaveddata != null && mapitemsaveddata.iconCountNotLessThan(256)) {
                                canContinue = false;
                            }
                        }
                        if (canContinue)
                        {
                            itemFrame.setHeldItemStack(this.usedItem);
                            itemFrame.emitGameEvent(GameEvent.BLOCK_CHANGE, this.agent);
                            this.usedItem.decrement(1);
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

        //
        private boolean hoe(BlockPos bp)
        {
            boolean result = false;

            Block block = this.agent.getWorld().getBlockState(bp).getBlock();
            BlockState bsNew = null;
            if (block == Blocks.ROOTED_DIRT)
            {
                Block.dropStack(this.agent.getWorld(), bp, Direction.getEntityFacingOrder(this.agent)[0].getOpposite(), new ItemStack(Items.HANGING_ROOTS));
                bsNew = Blocks.DIRT.getDefaultState();
            }
            else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
                    this.agent.getWorld().getBlockState(bp.up()).isAir())
            {
                if (block == Blocks.COARSE_DIRT) bsNew = Blocks.DIRT.getDefaultState();
                else bsNew = Blocks.FARMLAND.getDefaultState();
            }
            if (bsNew != null)
            {
                this.agent.getWorld().setBlockState(bp, bsNew, 11);
                this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                result = true;
            }
            return result;
        }

        //
        private boolean axe(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            int type = 0;
            BlockState bsNew = ((MixinAxeItemAccessor)this.usedItem.getItem()).invoke_getStrippedState(bs).orElse(null);
            if (bsNew == null) {bsNew = Oxidizable.getDecreasedOxidationState(bs).orElse(null); type = 1;}
            if (bsNew == null) {bsNew = Optional.ofNullable(HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(bs.getBlock())).map(block -> block.getStateWithProperties(bs)).orElse(null); type = 2;}
            if (bsNew != null)
            {
                switch (type)
                {
                    case 0 ->
                            this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_AXE_STRIP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    case 1 ->
                    {
                        this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        this.agent.getWorld().syncWorldEvent(null, WorldEvents.BLOCK_SCRAPED, bp, 0);
                    }
                    case 2 ->
                    {
                        this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        this.agent.getWorld().syncWorldEvent(null, WorldEvents.WAX_REMOVED, bp, 0);
                    }
                }
                this.agent.getWorld().setBlockState(bp, bsNew, 11);
                if (this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                result = true;
            }
            return result;
        }

        //
        private boolean shovel(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            BlockState bsNew = null;
            BlockState bsPath = MixinShovelItemAccessor.getPathStates().get(bs.getBlock());
            if (bsPath != null && this.agent.getWorld().isAir(bp.up()))
            {
                this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                bsNew = bsPath;
            }
            else if (bs.getBlock() instanceof CampfireBlock && bs.get(CampfireBlock.LIT))
            {
                this.agent.getWorld().syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, bp, 0);
                CampfireBlock.extinguish(this.agent, this.agent.getWorld(), bp, bs);
                bsNew = bs.with(CampfireBlock.LIT, Boolean.valueOf(false));
            }
            if (bsNew != null)
            {
                this.agent.getWorld().setBlockState(bp, bsNew, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                this.agent.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Emitter.of(this.agent, bsNew));
                if (this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                result = true;
            }
            return result;
        }

        //
        private boolean flintAndSteel(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            BlockState bsNew = null;
            if (!CampfireBlock.canBeLit(bs) && !CandleBlock.canBeLit(bs) && !CandleCakeBlock.canBeLit(bs))
            {
                if (AbstractFireBlock.canPlaceAt(this.agent.getWorld(), bp, Direction.fromRotation(this.agent.getYaw()))) bsNew = AbstractFireBlock.getState(this.agent.getWorld(), bp);
            }
            else if (bs.getBlock() instanceof TntBlock)
            {
                MixinTntBlockAccessor.invoke_primeTnt(this.agent.getWorld(), bp, this.agent);
                bsNew = Blocks.AIR.getDefaultState();
            }
            else bsNew = bs.with(Properties.LIT, Boolean.valueOf(true));
            if (bsNew != null)
            {
                this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, this.agent.getWorld().getRandom().nextFloat() * 0.4F + 0.8F);
                this.agent.getWorld().setBlockState(bp, bsNew, 11);
                if (this.usedItem.isOf(Items.FIRE_CHARGE)) this.usedItem.decrement(1);
                if (this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                result = true;
            }
            return result;
        }

        //
        private boolean boat(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOpaque()) return false;
            Vec3d center = bp.toCenterPos();
            BoatEntity boat;
            if (this.usedItem.isIn(ItemTags.CHEST_BOATS)) boat = new ChestBoatEntity(this.agent.getWorld(), center.getX(),center.getY(),center.getZ());
            else boat = new BoatEntity(this.agent.getWorld(), center.getX(),center.getY(),center.getZ());
            BoatEntity.Type type = BoatEntity.Type.OAK;
            if (this.usedItem.isOf(Items.ACACIA_BOAT) || this.usedItem.isOf(Items.ACACIA_CHEST_BOAT)) type = BoatEntity.Type.ACACIA;
            if (this.usedItem.isOf(Items.BIRCH_BOAT) || this.usedItem.isOf(Items.BIRCH_CHEST_BOAT)) type = BoatEntity.Type.BIRCH;
            if (this.usedItem.isOf(Items.BIRCH_BOAT) || this.usedItem.isOf(Items.BIRCH_CHEST_BOAT)) type = BoatEntity.Type.BIRCH;
            if (this.usedItem.isOf(Items.CHERRY_BOAT) || this.usedItem.isOf(Items.CHERRY_CHEST_BOAT)) type = BoatEntity.Type.CHERRY;
            if (this.usedItem.isOf(Items.DARK_OAK_BOAT) || this.usedItem.isOf(Items.DARK_OAK_CHEST_BOAT)) type = BoatEntity.Type.DARK_OAK;
            if (this.usedItem.isOf(Items.JUNGLE_BOAT) || this.usedItem.isOf(Items.JUNGLE_CHEST_BOAT)) type = BoatEntity.Type.JUNGLE;
            if (this.usedItem.isOf(Items.MANGROVE_BOAT) || this.usedItem.isOf(Items.MANGROVE_CHEST_BOAT)) type = BoatEntity.Type.MANGROVE;
            if (this.usedItem.isOf(Items.SPRUCE_BOAT) || this.usedItem.isOf(Items.SPRUCE_CHEST_BOAT)) type = BoatEntity.Type.SPRUCE;
            boat.setVariant(type);
            boat.setYaw(Direction.fromRotation(this.agent.getYaw()).asRotation());
            this.agent.getWorld().spawnEntity(boat);
            this.agent.getWorld().emitGameEvent(this.agent, GameEvent.ENTITY_PLACE, bp.toCenterPos());
            this.usedItem.decrement(1);
            return true;
        }

        //
        private boolean waterBottle(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            boolean result = false;
            if (bs.isIn(BlockTags.CONVERTABLE_TO_MUD))
            {
                this.agent.getWorld().emitGameEvent(this.agent, GameEvent.FLUID_PLACE, bp);
                this.agent.getWorld().setBlockState(bp, Blocks.MUD.getDefaultState());
                if (this.agent.getWorld() instanceof ServerWorld level)
                    level.spawnParticles(ParticleTypes.SPLASH, (double)bp.getX() + level.random.nextDouble(), (double)(bp.getY() + 1), (double)bp.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                result = true;
            }
            else if (bs.isOf(Blocks.CAULDRON))
            {
                this.agent.getWorld().setBlockState(bp, Blocks.WATER_CAULDRON.getDefaultState());
                result = true;
            }
            else if (bs.isOf(Blocks.WATER_CAULDRON) && bs.get(LeveledCauldronBlock.LEVEL) < 3)
            {
                this.agent.getWorld().setBlockState(bp, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, bs.get(LeveledCauldronBlock.LEVEL) + 1));
                result = true;
            }
            if (result)
            {
                this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), new ItemStack(Items.GLASS_BOTTLE));
            }
            return result;
        }

        //
        private boolean bucket(BlockPos bp)
        {
            boolean result = false;
            World level = this.agent.getWorld();
            BlockState bs = level.getBlockState(bp);
            Fluid fluid = null;
            if (this.usedItem.isOf(Items.BUCKET))
            {
                ItemStack pickupItem = null;
                if (bs.getBlock() instanceof FluidDrainable fluidDrainable)
                {
                    pickupItem = fluidDrainable.tryDrainFluid(level, bp, bs);
                    fluidDrainable.getBucketFillSound().ifPresent(sound -> this.agent.playSound(sound, 1.0f, 1.0f));
                }
                else if (bs.isOf(Blocks.WATER_CAULDRON) && bs.get(LeveledCauldronBlock.LEVEL) == 3)
                {
                    this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.NEUTRAL, 1,1);
                    this.agent.getWorld().setBlockState(bp, Blocks.CAULDRON.getDefaultState());
                    pickupItem = new ItemStack(Items.WATER_BUCKET);
                }
                else if (bs.isOf(Blocks.LAVA_CAULDRON))
                {
                    this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.NEUTRAL, 1,1);
                    this.agent.getWorld().setBlockState(bp, Blocks.CAULDRON.getDefaultState());
                    pickupItem = new ItemStack(Items.LAVA_BUCKET);
                }
                else if (bs.isOf(Blocks.POWDER_SNOW_CAULDRON)  && bs.get(LeveledCauldronBlock.LEVEL) == 3)
                {
                    this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW, SoundCategory.NEUTRAL, 1,1);
                    this.agent.getWorld().setBlockState(bp, Blocks.CAULDRON.getDefaultState());
                    pickupItem = new ItemStack(Items.POWDER_SNOW_BUCKET);
                }
                if (pickupItem != null)
                {
                    this.usedItem.decrement(1);
                    Block.dropStack(level, this.agent.getSteppingPos().up(), pickupItem);
                    return true;
                }
            }
            else if (this.usedItem.isOf(Items.WATER_BUCKET)) fluid = Fluids.WATER;
            else if (this.usedItem.isOf(Items.LAVA_BUCKET)) fluid = Fluids.LAVA;
            if (fluid != null)
            {
                if (bs.isOf(Blocks.CAULDRON))
                {
                    if (fluid == Fluids.WATER)
                    {
                        BlockState bsNew = Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3);
                        this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.NEUTRAL, 1, 1);
                        this.agent.getWorld().setBlockState(bp, bsNew);
                        result = true;
                    }
                    else
                    {
                        this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.NEUTRAL, 1, 1);
                        this.agent.getWorld().setBlockState(bp, Blocks.LAVA_CAULDRON.getDefaultState());
                        result = true;
                    }
                }
                else
                {
                    BucketItem bucket = (BucketItem) this.usedItem.getItem();
                    result = bucket.placeFluid(null, this.agent.getWorld(), bp, null);
                    if (result) bucket.onEmptied(null, this.agent.getWorld(), this.usedItem, bp);
                }
                if (result)
                {
                    this.usedItem.decrement(1);
                    Block.dropStack(level, this.agent.getSteppingPos().up(), new ItemStack(Items.BUCKET));
                }
            }
            return result;
        }

        //
        private boolean glassBottle(BlockPos bp)
        {
            boolean result = false;
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isIn(BlockTags.BEEHIVES, state -> state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock beehive) && bs.get(BeehiveBlock.HONEY_LEVEL) >= 5)
            {
                ((BeehiveBlock)bs.getBlock()).takeHoney(this.agent.getWorld(), bs, bp, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), new ItemStack(Items.HONEY_BOTTLE));
                result = true;
            }
            else if (bs.isOf(Blocks.WATER_CAULDRON))
            {
                LeveledCauldronBlock.decrementFluidLevel(bs, this.agent.getWorld(), bp);
                this.agent.getWorld().playSound(this.agent, this.agent.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                this.agent.getWorld().emitGameEvent(this.agent, GameEvent.FLUID_PICKUP, bp);
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            }
            else if (this.agent.getWorld().getFluidState(bp).isIn(FluidTags.WATER))
            {
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                result = true;
            }
            return result;
        }

        //
        private boolean throwable()
        {
            SoundEvent se = null;
            World level = this.agent.getWorld();
            if (this.usedItem.isOf(Items.SNOWBALL))
            {
                se = SoundEvents.ENTITY_SNOWBALL_THROW;
                SnowballEntity snowball = new SnowballEntity(level, this.agent);
                snowball.setItem(this.usedItem);
                snowball.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), 0.0F, 1.5F, 1.0F);
                level.spawnEntity(snowball);
            }
            else if (this.usedItem.isOf(Items.EGG))
            {
                se = SoundEvents.ENTITY_EGG_THROW;
                EggEntity thrownegg = new EggEntity(level, this.agent);
                thrownegg.setItem(this.usedItem);
                thrownegg.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), 0.0F, 1.5F, 1.0F);
                level.spawnEntity(thrownegg);
            }
            else if (this.usedItem.isOf(Items.ENDER_PEARL))
            {
                se = SoundEvents.ENTITY_ENDER_PEARL_THROW;
                EnderPearlEntity thrownenderpearl = new EnderPearlEntity(level, this.agent);
                thrownenderpearl.setItem(this.usedItem);
                thrownenderpearl.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), 0.0F, 1.5F, 1.0F);
                level.spawnEntity(thrownenderpearl);
            }
            else if (this.usedItem.isOf(Items.EXPERIENCE_BOTTLE))
            {
                se = SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW;
                ExperienceBottleEntity thrownexperiencebottle = new ExperienceBottleEntity(level, this.agent);
                thrownexperiencebottle.setItem(this.usedItem);
                thrownexperiencebottle.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), -20.0F, 0.7F, 1.0F);
                level.spawnEntity(thrownexperiencebottle);
            }
            else
            {
                se = SoundEvents.ENTITY_LINGERING_POTION_THROW;
                if (this.usedItem.isOf(Items.SPLASH_POTION)) se = SoundEvents.ENTITY_SPLASH_POTION_THROW;
                PotionEntity thrownpotion = new PotionEntity(level, this.agent);
                thrownpotion.setItem(this.usedItem);
                thrownpotion.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), -20.0F, 0.5F, 1.0F);
                level.spawnEntity(thrownpotion);
            }
            this.usedItem.decrement(1);
            if (se != null) level.playSound(this.agent, this.agent.getSteppingPos().up(), se, SoundCategory.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            return true;
        }

        // Does not work (fishing rod does not currently support nonplayer entities)
        private boolean fishingRod()
        {
            return false;
        }

        //
        private boolean boneMeal(BlockPos bp)
        {
            if (BoneMealItem.useOnFertilizable(this.usedItem, this.agent.getWorld(), bp) && !BoneMealItem.useOnGround(this.usedItem, this.agent.getWorld(), bp, null))
            {
                this.agent.getWorld().syncWorldEvent(WorldEvents.BONE_MEAL_USED, bp, 0);
                return true;
            }
            return false;
        }

        //
        private boolean shears(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            Block block = bs.getBlock();
            if (block instanceof AbstractPlantStemBlock growingplantheadblock)
            {
                if (!growingplantheadblock.hasMaxAge(bs))
                {
                    this.agent.getWorld().playSound(this.agent, bp, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    BlockState bsNew = growingplantheadblock.withMaxAge(bs);
                    this.agent.getWorld().setBlockState(bp, bsNew);
                    this.agent.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Emitter.of(this.agent, bsNew));
                    if (this.usedItem.isDamageable()) this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                    return true;
                }
            }
            return false;
        }

        //
        private boolean enderEye(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.END_PORTAL_FRAME) && !bs.get(EndPortalFrameBlock.EYE))
            {
                BlockState bsNew = bs.with(EndPortalFrameBlock.EYE, Boolean.valueOf(true));
                Block.pushEntitiesUpBeforeBlockChange(bs, bsNew, this.agent.getWorld(), bp);
                this.agent.getWorld().setBlockState(bp, bsNew, 2);
                this.agent.getWorld().updateComparators(bp, Blocks.END_PORTAL_FRAME);
                this.usedItem.decrement(1);
                this.agent.getWorld().syncWorldEvent(WorldEvents.END_PORTAL_FRAME_FILLED, bp, 0);
                BlockPattern.Result blockpattern$blockpatternmatch = EndPortalFrameBlock.getCompletedFramePattern().searchAround(this.agent.getWorld(), bp);
                if (blockpattern$blockpatternmatch != null) {
                    BlockPos blockpos1 = blockpattern$blockpatternmatch.getFrontTopLeft().add(-3, 0, -3);
                    for(int i = 0; i < 3; ++i) {
                        for(int j = 0; j < 3; ++j) {
                            this.agent.getWorld().setBlockState(blockpos1.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), 2);
                        }
                    }
                    this.agent.getWorld().syncGlobalEvent(WorldEvents.END_PORTAL_OPENED, blockpos1.add(1, 0, 1), 0);
                }
            }
            else
            {
                ServerWorld level = (ServerWorld)this.agent.getWorld();
                BlockPos structureBp = level.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, this.agent.getBlockPos(), 100, false);
                if (structureBp != null)
                {
                    EyeOfEnderEntity eyeofender = new EyeOfEnderEntity(this.agent.getWorld(), this.agent.getX(), this.agent.getEyeY(), this.agent.getZ());
                    eyeofender.setItem(this.usedItem);
                    eyeofender.initTargetPos(structureBp);
                    this.agent.getWorld().emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeofender.getPos(), GameEvent.Emitter.of(this.agent));
                    this.agent.getWorld().spawnEntity(eyeofender);
                    this.agent.getWorld().playSound(null, this.agent.getX(), this.agent.getY(), this.agent.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (this.agent.getWorld().getRandom().nextFloat() * 0.4F + 0.8F));
                    this.agent.getWorld().syncWorldEvent(null, WorldEvents.EYE_OF_ENDER_LAUNCHES, this.agent.getBlockPos(), 0);
                    this.usedItem.decrement(1);
                    return true;
                }
            }
            return false;
        }

        //
        private boolean rocket(BlockPos bp)
        {
            ItemStack itemstack = this.usedItem;
            Vec3d vec3 = bp.toCenterPos();
            Direction direction = Direction.DOWN;
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(this.agent.getWorld(), this.agent, vec3.x + (double)direction.getOffsetX() * 0.15D, vec3.y + (double)direction.getOffsetY() * 0.15D, vec3.z + (double)direction.getOffsetZ() * 0.15D, itemstack);
            this.agent.getWorld().spawnEntity(fireworkrocketentity);
            itemstack.decrement(1);
            return true;
        }

        //
        private boolean lead(BlockPos bp)
        {
            if (this.agent.getWorld().getBlockState(bp).isIn(BlockTags.FENCES))
            {
                LeashKnotEntity leashfenceknotentity = null;
                boolean flag = false;
                double d0 = 7.0D;
                int i = bp.getX();
                int j = bp.getY();
                int k = bp.getZ();
                for (MobEntity mob : this.agent.getWorld().getNonSpectatingEntities(MobEntity.class, new Box((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0)))
                {
                    if (mob.getHoldingEntity() == this.agent)
                    {
                        if (leashfenceknotentity == null)
                        {
                            leashfenceknotentity = LeashKnotEntity.getOrCreate(this.agent.getWorld(), bp);
                            leashfenceknotentity.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
                        }
                        mob.attachLeash(leashfenceknotentity, true);
                        flag = true;
                    }
                }
                if (flag)
                {
                    this.agent.getWorld().emitGameEvent(GameEvent.BLOCK_ATTACH, bp, GameEvent.Emitter.of(this.agent));
                    this.usedItem.decrement(1);
                    return true;
                }
            }
            return false;
        }

        //
        private boolean record(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.JUKEBOX) && !bs.get(JukeboxBlock.HAS_RECORD))
            {
                BlockEntity blockentity = this.agent.getWorld().getBlockEntity(bp);
                if (blockentity instanceof JukeboxBlockEntity)
                {
                    JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
                    jukeboxblockentity.setDisc(this.usedItem.copy());
                    this.agent.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Emitter.of(this.agent, bs));
                    this.usedItem.decrement(1);
                    return true;
                }
            }
            return false;
        }

        //
        private boolean trident()
        {
            LivingEntity owner = this.agent.getOwner();
            if (owner == null) owner = this.agent;
            this.usedItem.damage(1, this.agent, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            TridentEntity throwntrident = new TridentEntity(this.agent.getWorld(), owner, this.usedItem);
            throwntrident.setPosition(this.agent.getEyePos());
            throwntrident.setVelocity(this.agent, this.agent.getPitch(), this.agent.getYaw(), 0.0F, 2.5F, 1.0F);
            this.agent.getWorld().spawnEntity(throwntrident);
            this.agent.getWorld().playSound(throwntrident, throwntrident.getBlockPos(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            this.usedItem.decrement(1);
            return true;
        }

        // WORKS
        private boolean instrument()
        {
            TagKey<Instrument> instruments = null;
            if (this.usedItem.isOf(Items.GOAT_HORN))
            {
                instruments = InstrumentTags.GOAT_HORNS;
            }
            if (instruments != null)
            {
                Optional<? extends RegistryEntry<Instrument>> optional = Optional.empty();
                NbtCompound compoundtag = this.usedItem.getNbt();
                if (compoundtag != null && compoundtag.contains("instrument", 8)) {
                    Identifier resourcelocation = Identifier.tryParse(compoundtag.getString("instrument"));
                    if (resourcelocation != null) {
                        optional = Registries.INSTRUMENT.getEntry(RegistryKey.of(RegistryKeys.INSTRUMENT, resourcelocation));
                    }
                }
                if (optional.isEmpty())
                {
                    Iterator<RegistryEntry<Instrument>> iterator = Registries.INSTRUMENT.iterateEntries(instruments).iterator();
                    optional = iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
                }

                Instrument instrument = optional.get().value();
                SoundEvent soundevent = instrument.soundEvent().value();
                float f = instrument.range() / 16.0F;
                this.agent.getWorld().playSound(this.agent, this.agent.getBlockPos(), soundevent, SoundCategory.RECORDS, f, 1.0F);
                this.agent.getWorld().emitGameEvent(GameEvent.INSTRUMENT_PLAY, this.agent.getPos(), GameEvent.Emitter.of(this.agent));
                return true;
            }
            return false;
        }

        //
        private boolean honeycomb(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            Optional<BlockState> optional = HoneycombItem.getWaxedState(bs);
            if (optional.isPresent())
            {
                this.agent.getWorld().setBlockState(bp, optional.get());
                this.agent.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Emitter.of(this.agent, bs));
                this.usedItem.decrement(1);
                return true;
            }
            return false;
        }

        //
        private boolean compass(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.LODESTONE))
            {
                this.agent.getWorld().playSound(this.agent, bp, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                NbtCompound compoundTag = this.usedItem.getOrCreateNbt();
                compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(bp));
                World.CODEC.encodeStart(NbtOps.INSTANCE, this.agent.getWorld().getRegistryKey()).resultOrPartial(LogUtils.getLogger()::error).ifPresent(nbtElement -> compoundTag.put("LodestoneDimension", (NbtElement)nbtElement));
                compoundTag.putBoolean("LodestoneTracked", true);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), this.usedItem.copy());
                this.usedItem.decrement(1);
                return true;
            }
            return false;
        }

        //
        private boolean glowstone(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.RESPAWN_ANCHOR) && (bs.get(RespawnAnchorBlock.CHARGES) != RespawnAnchorBlock.MAX_CHARGES))
            {
                RespawnAnchorBlock.charge(null, this.agent.getWorld(), bp, bs);
                this.usedItem.decrement(1);
                return true;
            }
            return false;
        }

        // WORKS
        private boolean armorStand(BlockPos bp)
        {
            ServerWorld level = (ServerWorld) this.agent.getWorld();
            Consumer<ArmorStandEntity> consumer = EntityType.copier((entity) -> {
                entity.setYaw(Direction.fromRotation(this.agent.getYaw()).asRotation());
            }, level, this.usedItem, null);
            ArmorStandEntity armorstand = EntityType.ARMOR_STAND.create(level, this.usedItem.getNbt(), consumer, bp, SpawnReason.SPAWN_EGG, false, false);
            if (armorstand != null)
            {
                armorstand.updatePositionAndAngles(armorstand.getX(), armorstand.getY(), armorstand.getZ(), armorstand.getYaw(), 0.0F);
                level.spawnEntity(armorstand);
                GolemFirstStoneMod.LOGGER.info("" + armorstand);
                level.playSound(armorstand, bp, SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.NEUTRAL, 0.75F, 0.8F);
                armorstand.emitGameEvent(GameEvent.ENTITY_PLACE, this.agent);
                this.usedItem.decrement(1);
                return true;
            }
            return false;
        }

        //
        private boolean minecart(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            AbstractMinecartEntity.Type type = AbstractMinecartEntity.Type.RIDEABLE;
            if (this.usedItem.isOf(Items.CHEST_MINECART)) type = AbstractMinecartEntity.Type.CHEST;
            if (this.usedItem.isOf(Items.TNT_MINECART)) type = AbstractMinecartEntity.Type.TNT;
            if (this.usedItem.isOf(Items.FURNACE_MINECART)) type = AbstractMinecartEntity.Type.FURNACE;
            if (this.usedItem.isOf(Items.HOPPER_MINECART)) type = AbstractMinecartEntity.Type.HOPPER;
            if (this.usedItem.isOf(Items.COMMAND_BLOCK_MINECART)) type = AbstractMinecartEntity.Type.COMMAND_BLOCK;
            if (bs.isIn(BlockTags.RAILS))
            {
                RailShape railShape = bs.getBlock() instanceof AbstractRailBlock ? bs.get(((AbstractRailBlock)bs.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                double d0 = 0.0D;
                if (railShape.isAscending()) {
                    d0 = 0.5D;
                }
                AbstractMinecartEntity abstractminecart = AbstractMinecartEntity.create(this.agent.getWorld(), (double)bp.getX() + 0.5D, (double)bp.getY() + 0.0625D + d0, (double)bp.getZ() + 0.5D, type);
                if (this.usedItem.hasCustomName()) abstractminecart.setCustomName(this.usedItem.getName());
                this.agent.getWorld().spawnEntity(abstractminecart);
                this.agent.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, bp, GameEvent.Emitter.of(this.agent, this.agent.getWorld().getBlockState(bp.down())));
                this.usedItem.decrement(1);
                return true;
            }
            return false;
        }

        //
        private boolean book(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.LECTERN))
            {
                LecternBlock.putBookIfAbsent(this.agent, this.agent.getWorld(), bp, bs, this.usedItem);
                return true;
            }
            else if (this.usedItem.isOf(Items.WRITABLE_BOOK))
            {
                if (!this.usedItem.hasNbt())
                {
                    ItemStack newBook = new ItemStack(Items.WRITTEN_BOOK);

                    newBook.setSubNbt("author", NbtString.of(this.agent.getDisplayName().getString()));
                    newBook.setSubNbt("title", NbtString.of(Text.translatable("book.golemfirststonemod.book_1").getString()));
                    newBook.setSubNbt("filtered_title", NbtString.of(Text.translatable("book.golemfirststonemod.book_1.filtered_title").getString()));

                    List<String> pages = Lists.newArrayList();
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_1").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_2").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_3").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_4").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_5").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_6").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_7").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_8").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_9").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_10").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_11").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_12").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_13").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_14").getString());
                    pages.add(Text.translatable("book.golemfirststonemod.book_1.page_15").getString());
                    NbtList listtag = new NbtList();
                    pages.stream().map(NbtString::of).forEach(listtag::add);
                    newBook.setSubNbt("pages", listtag);

                    Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), newBook);
                    this.usedItem.decrement(1);
                }
            }
            return false;
        }

        //
        private boolean chiseledBookshelf(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            BlockEntity be = this.agent.getWorld().getBlockEntity(bp);
            if (be instanceof ChiseledBookshelfBlockEntity shelfBe)
            {
                if (this.usedItem.isIn(ItemTags.BOOKSHELF_BOOKS))
                {
                    for (int i = 0; i < 6; i++)
                    {
                        if (!bs.get(ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i)))
                        {
                            SoundEvent soundevent = this.usedItem.isOf(Items.ENCHANTED_BOOK) ? SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT;
                            shelfBe.setStack(i, this.usedItem.split(1));
                            this.agent.getWorld().playSound(null, bp, soundevent, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                            this.agent.getWorld().emitGameEvent(this.agent, GameEvent.BLOCK_CHANGE, bp);
                            return true;
                        }
                    }
                }
                for (int i = 0; i < 6; i++)
                {
                    if (bs.get(ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i)))
                    {
                        ItemStack newBook = shelfBe.removeStack(i, 1);
                        SoundEvent soundevent = newBook.isOf(Items.ENCHANTED_BOOK) ? SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP;
                        this.agent.getWorld().playSound(null, bp, soundevent, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                        this.agent.getWorld().emitGameEvent(this.agent, GameEvent.BLOCK_CHANGE, bp);
                        Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), newBook);
                        return true;
                    }
                }
            }
            return false;
        }

        //
        private boolean washInCauldron(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            ItemStack newItem = null;
            if (this.usedItem.isIn(ItemTags.BANNERS) && BannerBlockEntity.getPatternCount(this.usedItem) > 0)
            {
                newItem = this.usedItem.copyWithCount(1);
                BannerBlockEntity.loadFromItemStack(newItem);
            }
            else if (Block.getBlockFromItem(this.usedItem.getItem()) instanceof ShulkerBoxBlock && !this.usedItem.isOf(Items.SHULKER_BOX))
            {
                newItem = new ItemStack(Items.SHULKER_BOX);
                if (this.usedItem.hasNbt()) newItem.setNbt(this.usedItem.getNbt().copy());
            }
            else if (this.usedItem.getItem() instanceof DyeableItem dyeableLeatherItem && dyeableLeatherItem.hasColor(this.usedItem))
            {
                newItem = this.usedItem.copy();
                dyeableLeatherItem.removeColor(newItem);
            }
            if (newItem != null)
            {
                LeveledCauldronBlock.decrementFluidLevel(bs, this.agent.getWorld(), bp);
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), newItem);
                return true;
            }
            return false;
        }

        //
        private boolean snowBucket(BlockPos bp)
        {
            BlockState bs = this.agent.getWorld().getBlockState(bp);
            if (bs.isOf(Blocks.CAULDRON) || (bs.isOf(Blocks.POWDER_SNOW_CAULDRON) && bs.get(LeveledCauldronBlock.LEVEL) < 3))
            {
                BlockState bsNew = Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3);
                this.agent.getWorld().playSound(null, bp, SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW, SoundCategory.NEUTRAL, 1, 1);
                this.agent.getWorld().setBlockState(bp, bsNew);
                this.usedItem.decrement(1);
                Block.dropStack(this.agent.getWorld(), this.agent.getSteppingPos().up(), new ItemStack(Items.BUCKET));
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
            TargetPredicate tp = TargetPredicate.createAttackable();
            Vec3d center = bp.toCenterPos();
            Box aabb = Box.of(center, 1,1,1);
            LivingEntity targetEntity = this.agent.getWorld().getClosestEntity(LivingEntity.class, tp, this.agent, center.getX(),center.getY(),center.getZ(), aabb);
            if (targetEntity != null && this.agent.canTarget(targetEntity))
            {
                if (this.attackingItem != null && !(this.attackingItem.isEmpty()))
                {
                    this.attackingItem.getItem().postHit(this.attackingItem, targetEntity, this.agent);
                }
                this.agent.tryAttack(targetEntity);
            }
            else
            {
                List<Entity> entityList = this.agent.getWorld().getOtherEntities(this.agent, Box.of(bp.toCenterPos(), 1, 1, 1));
                if (!entityList.isEmpty())
                {
                    Entity entity = entityList.get(0);
                    if (entity instanceof LivingEntity livingEntity)
                    {
                        if (this.agent.canTarget(livingEntity)) this.agent.tryAttack(entity);
                    }
                    else this.agent.tryAttack(entity);
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
