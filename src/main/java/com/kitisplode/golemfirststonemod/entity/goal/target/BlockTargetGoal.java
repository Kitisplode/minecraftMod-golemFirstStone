package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class BlockTargetGoal extends Goal
{
    protected final MobEntity mob;
    protected final IEntityCanAttackBlocks blockAttacker;
    private static final int DEFAULT_RECIPROCAL_CHANCE = 10;
    protected final int reciprocalChance;
    @Nullable
    protected BlockPos targetBlock;
    protected final Predicate<BlockState> targetPredicate;

    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    protected final boolean checkVisibility;
    private int timeWithoutVisibility;
    protected int maxTimeWithoutVisibility = 60;
    private final boolean randomizeChoice;

    private Vec3i lastPosition = null;
    private List<BlockPos> posList;

    private final int range;

    public BlockTargetGoal(MobEntity mob, int range, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<BlockState> targetPredicate, boolean randomizeChoice)
    {
        assert(mob instanceof IEntityCanAttackBlocks);
        this.mob = mob;
        this.blockAttacker = (IEntityCanAttackBlocks) mob;
        this.targetPredicate = targetPredicate;
        this.range = range;
        this.reciprocalChance = ActiveTargetGoal.toGoalTicks(reciprocalChance);
        this.setControls(EnumSet.of(Control.TARGET));
        this.checkCanNavigate = checkCanNavigate;
        this.checkVisibility = checkVisibility;
        this.randomizeChoice = randomizeChoice;
    }

    @Override
    public boolean canStart()
    {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        }
        if (!this.randomizeChoice) this.findClosestTarget();
        else this.findRandomTarget();
        return this.targetBlock != null;
    }

    @Override
    public boolean shouldContinue() {
        BlockPos posTarget = this.blockAttacker.getBlockTarget();
        if (posTarget == null) return false;
        if (!this.blockAttacker.canTargetBlock(posTarget)) return false;
        double d = this.getFollowRange();
        if (this.mob.squaredDistanceTo(posTarget.toCenterPos()) > MathHelper.square(d)) return false;
        if (this.checkVisibility) {
            if (this.canSeeBlock(RaycastContext.FluidHandling.ANY)) this.timeWithoutVisibility = 0;
            else if (++this.timeWithoutVisibility > toGoalTicks(this.maxTimeWithoutVisibility)) return false;
        }
        this.blockAttacker.setBlockTarget(posTarget);
        return true;
    }

    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
        this.blockAttacker.setBlockTarget(this.targetBlock);
        super.start();
    }

    @Override
    public void stop() {
        this.blockAttacker.setBlockTarget(null);
        this.targetBlock = null;
    }

    public void setTargetBlock(@Nullable BlockPos bp) {
        this.targetBlock = bp;
    }

    protected void findRandomTarget()
    {
        Vec3i mobPos = this.mob.getBlockPos();
        if (!mobPos.equals(this.lastPosition))
        {
            this.lastPosition = mobPos;
            this.posList = getQualifyingBlocks(this.mob.getBlockPos(), this.range, this.targetPredicate, this.mob.getWorld());
        }
        this.targetBlock = getRandomBlockFromList(this.posList);
    }

    protected void findClosestTarget() {
        Vec3i mobPos = this.mob.getBlockPos();
        if (!mobPos.equals(this.lastPosition))
        {
            this.lastPosition = mobPos;
            this.posList = getQualifyingBlocks(this.mob.getBlockPos(), this.range, this.targetPredicate, this.mob.getWorld());
        }
        this.targetBlock = getClosestBlockFromList(this.posList, this.mob.getX(), this.mob.getY(), this.mob.getZ());
    }

    protected List<BlockPos> getQualifyingBlocks(BlockPos bp, int radius, Predicate<BlockState> predicate, World world)
    {
        List<BlockPos> posList = new ArrayList<>();
        int diameter = radius * 2;
        BlockPos startBp = bp.add(-radius,-radius,-radius);

        for (int y = 0; y < diameter; y++)
        {
            if (y <= world.getBottomY()) continue;
            for (int x = 0; x < diameter; x++)
            {
                for (int z = 0; z < diameter; z++)
                {
                    BlockPos currentBp = startBp.add(x,y,z);
                    net.minecraft.block.BlockState bs = world.getBlockState(currentBp);
                    if (bs == null) continue;
                    if (!predicate.test(bs)) continue;
                    posList.add(currentBp);
                }
            }
        }
        return posList;
    }

    protected BlockPos getClosestBlockFromList(List<BlockPos> posList, double x, double y, double z)
    {
        BlockPos bestPos = null;
        double d = -1.0;
        for(BlockPos pos : posList)
        {
            double e = pos.getSquaredDistance(x,y,z);
            if (d != -1.0 && !(e < d)) continue;
            d = e;
            bestPos = pos;
        }
        return bestPos;
    }

    protected BlockPos getRandomBlockFromList(List<BlockPos> posList)
    {
        BlockPos bestPos = null;
        for(BlockPos pos : posList)
        {
            BlockState bs = this.mob.getWorld().getBlockState(pos);
            if (this.mob.getRandom().nextInt(100) > this.blockAttacker.blockPreference(bs)) continue;
            bestPos = pos;
        }
        return bestPos;
    }

    protected boolean canSeeBlock(RaycastContext.FluidHandling fh)
    {
        if (this.targetBlock == null) return false;
        Vec3d targetCenter = this.targetBlock.toCenterPos();
        double distanceToTarget = this.mob.squaredDistanceTo(targetCenter);
        BlockHitResult ray = this.mob.getWorld().raycast(new RaycastContext(this.mob.getEyePos(), targetCenter, RaycastContext.ShapeType.OUTLINE, fh, this.mob));
        return distanceToTarget < 3.0d || !ray.getBlockPos().isWithinDistance(this.mob.getEyePos(), Math.max(distanceToTarget - 1.0d, 0.5d));
    }

    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, 4.0, distance);
    }

    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    protected boolean canTrack(@Nullable BlockPos target, Predicate<BlockState> targetPredicate) {
        BlockState bs = this.mob.getWorld().getBlockState(target);

        if (target == null || bs == null) return false;
        if (!targetPredicate.test(bs)) return false;
        if (!this.mob.isInWalkTargetRange(target)) return false;
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) this.canNavigateFlag = 0;
            if (this.canNavigateFlag == 0) {
                int n = this.canNavigateFlag = this.canNavigateToEntity(target) ? 1 : 2;
            }
            if (this.canNavigateFlag == 2) return false;
        }
        return true;
    }

    private boolean canNavigateToEntity(BlockPos pos) {
        int j;
        this.checkCanNavigateCooldown = TrackTargetGoal.toGoalTicks(10 + this.mob.getRandom().nextInt(5));
        Path path = this.mob.getNavigation().findPathTo(pos, 0);
        if (path == null) {
            return false;
        }
        PathNode pathNode = path.getEnd();
        if (pathNode == null) {
            return false;
        }
        int i = pathNode.x - pos.getX();
        return (double)(i * i + (j = pathNode.z - pos.getZ()) * j) <= 2.25;
    }

    public BlockTargetGoal setMaxTimeWithoutVisibility(int time) {
        this.maxTimeWithoutVisibility = time;
        return this;
    }
}
