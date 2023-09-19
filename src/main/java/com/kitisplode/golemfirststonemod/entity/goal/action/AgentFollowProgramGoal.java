package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;

public class AgentFollowProgramGoal extends Goal
{
    private final EntityGolemAgent agent;

    private ArrayList<Instruction> instructions = new ArrayList<>();

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
        if (instructions.isEmpty()) return;
        Instruction currentInstruction = instructions.get(0);
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
                instructions.remove(0);
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
            if (instruction instanceof InstructionIf instructionIf)
            {
                for (int o = 0; o < instructionIf.skipAmount(); o++) items.remove(0);
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
                if (instruction instanceof InstructionIf instructionIf)
                {
                    for (int o = 0; o < instructionIf.skipAmount(); o++) itemsInBox.remove(0);
                }
                list.add(instruction);
            }
            set = new InstructionSet(this.agent, list);
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
        if (this.itemIsShulkerBox(itemStack)) return this.shulkerBoxToInstruction(itemStack);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_BLOCK.get()) || itemStack.is(ModItems.ITEM_INSTRUCTION_IF_SOLID.get()))
            return this.ifItemToInstruction(items);
        return null;
    }

    private InstructionIf ifItemToInstruction(ArrayList<ItemStack> items)
    {
        ItemStack itemStack = items.get(0);
        Block blockType = null;
        ArrayList<ItemStack> tempList = (ArrayList<ItemStack>) items.clone();
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_BLOCK.get()))
        {
            if (items.size() < 3) return null;
            blockType = Block.byItem(items.get(1).getItem());
            tempList.remove(0); tempList.remove(0);
        }
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_SOLID.get()))
        {
            if (items.size() < 2) return null;
            tempList.remove(0);
        }

        Instruction nextInstruction = this.pullInstructionFromFirstItem(tempList);
        if (nextInstruction == null) return null;
        ArrayList<Instruction> list = new ArrayList<>();
        list.add(nextInstruction);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_BLOCK.get())) return new InstructionIfCheckBlock(this.agent, blockType, list);
        if (itemStack.is(ModItems.ITEM_INSTRUCTION_IF_SOLID.get())) return new InstructionIfCheckSolid(this.agent, list);
        return null;
    }

//======================================================================================================================

    abstract class Instruction
    {
        private boolean isRunning;
        public boolean isRunning()
        {
            return this.isRunning;
        }
        abstract public boolean isDone();
        public void start()
        {
            this.isRunning = true;
        }
        public void tick() {}
    }

    class InstructionMoveForward extends Instruction
    {
        private final EntityGolemAgent agent;
        private final int distance;
        private BlockPos blockPos;
        private int ticks = 10;
        private Vec3 previousPos;

        public InstructionMoveForward(EntityGolemAgent agent, int distance)
        {
            this.agent = agent;
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
        private final EntityGolemAgent agent;
        private final int directionMultiplier;
        private float directionToTurn;
        private int ticks = 10;
        private float previousDirection;

        public InstructionTurn(EntityGolemAgent agent, int directionMultiplier)
        {
            this.agent = agent;
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
            this.directionToTurn = this.agent.getYRot() + 90.0f * this.directionMultiplier;
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
        protected final EntityGolemAgent agent;
        protected final ArrayList<Instruction> instructionList;

        public InstructionSet(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            this.agent = agent;
            this.instructionList = instructionList;
        }

        @Override
        public boolean isDone()
        {
            return this.instructionList.isEmpty();
        }

        @Override
        public void tick()
        {
            super.tick();
            if (this.instructionList.isEmpty()) return;
            Instruction currentInstruction = this.instructionList.get(0);
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
                    this.instructionList.remove(0);
                }
            }
        }
    }

    abstract class InstructionIf extends InstructionSet
    {
        public InstructionIf(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
        }

        abstract public int skipAmount();

        @Override
        public void start()
        {
            super.start();
            if (!this.checkCondition()) this.instructionList.clear();
        }

        abstract public boolean checkCondition();
    }

    class InstructionIfCheckBlock extends InstructionIf
    {
        private final EntityGolemAgent agent;
        private final Block blockType;

        public InstructionIfCheckBlock(EntityGolemAgent agent, Block blockType, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
            this.agent = agent;
            this.blockType = blockType;
        }

        @Override
        public int skipAmount()
        {
            return 2;
        }

        @Override
        public boolean checkCondition()
        {
            Direction direction = Direction.orderedByNearest(this.agent)[0];
            Vec3 eyePos = this.agent.getEyePosition();
            BlockPos bp = new BlockPos((int) Math.floor(eyePos.x()), (int) Math.floor(eyePos.y()), (int) Math.floor(eyePos.z())).offset(direction.getNormal());
            return this.agent.level().getBlockState(bp).is(this.blockType);
        }
    }

    class InstructionIfCheckSolid extends InstructionIf
    {
        private final EntityGolemAgent agent;

        public InstructionIfCheckSolid(EntityGolemAgent agent, ArrayList<Instruction> instructionList)
        {
            super(agent, instructionList);
            this.agent = agent;
        }

        @Override
        public int skipAmount()
        {
            return 1;
        }

        @Override
        public boolean checkCondition()
        {
            Direction direction = Direction.orderedByNearest(this.agent)[0];
            Vec3 eyePos = this.agent.getEyePosition();
            BlockPos bp = new BlockPos((int) Math.floor(eyePos.x()), (int) Math.floor(eyePos.y()), (int) Math.floor(eyePos.z())).offset(direction.getNormal());
            return this.agent.level().getBlockState(bp).canOcclude();
        }
    }
}
