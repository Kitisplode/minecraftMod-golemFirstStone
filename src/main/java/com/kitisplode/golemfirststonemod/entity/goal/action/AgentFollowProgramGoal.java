package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;

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
        this.instructions.clear();
        // Loop through the inventory.
        int inventorySize = this.agent.getInventory().getContainerSize();
        for (int i = 0; i < inventorySize; i++)
        {
            ItemStack itemStack = this.agent.getInventory().getItem(i);
            if (itemStack.is(ModItems.ITEM_INSTRUCTION_MOVE_FORWARD.get())) this.instructions.add(new InstructionMove(this.agent, itemStack.getCount()));
            if (itemStack.is(ModItems.ITEM_INSTRUCTION_TURN_LEFT_90.get())) this.instructions.add(new InstructionTurn(this.agent, -itemStack.getCount()));
            if (itemStack.is(ModItems.ITEM_INSTRUCTION_TURN_RIGHT_90.get())) this.instructions.add(new InstructionTurn(this.agent, itemStack.getCount()));
        }
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

    class InstructionMove extends Instruction
    {
        private Direction direction;
        private final EntityGolemAgent agent;
        private final int distance;
        private BlockPos blockPos;
        private int ticks = 10;
        private Vec3 previousPos;

        public InstructionMove(EntityGolemAgent agent, int distance)
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
            this.direction = Direction.orderedByNearest(this.agent)[0];
            this.blockPos = this.agent.getOnPos().offset(this.direction.getNormal().multiply(this.distance));
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
}
