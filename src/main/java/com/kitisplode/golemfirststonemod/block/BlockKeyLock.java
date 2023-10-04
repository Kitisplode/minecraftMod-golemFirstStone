package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockKeyLock extends ObserverBlock implements EntityBlock
{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public BlockKeyLock(Properties pProperties)
    {
        super(pProperties);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos)
    {
        return pState;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return new BlockEntityKeyLock(pPos, pState);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        InteractionResult result = this.useInner(pState, pLevel, pPos, pPlayer, pHand, pHit);
        if (result != null) return result;
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    private InteractionResult useInner(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        ItemStack playerItem = pPlayer.getItemInHand(pHand);
        if (be instanceof BlockEntityKeyLock blockEntityKeyLock)
        {
            if (pHit.getDirection() == pState.getValue(DirectionalBlock.FACING).getOpposite())
            {
                if (playerItem.isEmpty()) blockEntityKeyLock.setItem(1, ItemStack.EMPTY);
                else blockEntityKeyLock.setItem(1, playerItem.copy());
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
            else if (pHit.getDirection() == pState.getValue(DirectionalBlock.FACING))
            {
                if (blockEntityKeyLock.getItem(0).isEmpty())
                {
                    if (playerItem.is(ModItems.ITEM_GOLEM_KEY.get()))
                    {
                        if (blockEntityKeyLock.isUnlocked(playerItem))
                        {
                            blockEntityKeyLock.setItem(0, playerItem.copy());
                            playerItem.shrink(1);
                            pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_HAPPY.get(), SoundSource.NEUTRAL, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                            pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_UNLOCK.get(), SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                            return InteractionResult.sidedSuccess(pLevel.isClientSide);
                        }
                        else
                        {
                            pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_LOCK.get(), SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                            return InteractionResult.CONSUME;
                        }
                    }
                } else if (playerItem.isEmpty())
                {
                    ItemStack golemItem = blockEntityKeyLock.removeItem(0, 1);
                    pPlayer.setItemInHand(pHand, golemItem);
                    pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
                    pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_LOCK.get(), SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }
        }
        return null;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof BlockEntityKeyLock blockEntityKeyLock) {
                if (!blockEntityKeyLock.isEmpty()) {
                    ItemStack itemstack = blockEntityKeyLock.getItem(0);
                    if (!itemstack.isEmpty()) {
                        Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), itemstack);
                    }
                    blockEntityKeyLock.clearContent();
                    pLevel.updateNeighbourForOutputSignal(pPos, this);
                }
            }
            if (!pState.is(pNewState.getBlock())) {
                if (!pLevel.isClientSide && pState.getValue(POWERED) && pLevel.getBlockTicks().hasScheduledTick(pPos, this)) {
                    this.updateNeighborsInFront(pLevel, pPos, pState.setValue(POWERED, Boolean.valueOf(false)));
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    protected void updateNeighborsInFront(Level pLevel, BlockPos pPos, BlockState pState) {
        Direction direction = pState.getValue(FACING);
        BlockPos blockpos = pPos.relative(direction.getOpposite());
        pLevel.neighborChanged(blockpos, this, pPos);
        pLevel.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
    }
    public boolean isSignalSource(BlockState pState) {
        return true;
    }
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getSignal(pBlockAccess, pPos, pSide);
    }
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return pBlockState.getValue(POWERED) && pBlockState.getValue(FACING) == pSide ? 15 : 0;
    }
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving)
    {
        if (!pState.is(pOldState.getBlock()))
        {
            if (!pLevel.isClientSide() && pState.getValue(POWERED) && !pLevel.getBlockTicks().hasScheduledTick(pPos, this))
            {
                BlockState blockstate = pState.setValue(POWERED, Boolean.valueOf(false));
                pLevel.setBlock(pPos, blockstate, 18);
                this.updateNeighborsInFront(pLevel, pPos, blockstate);
            }
        }
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}
