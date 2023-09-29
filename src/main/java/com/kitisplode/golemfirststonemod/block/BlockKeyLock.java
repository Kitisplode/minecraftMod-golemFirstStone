package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockKeyLock extends ObserverBlock implements EntityBlock
{
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
        BlockEntity be = pLevel.getBlockEntity(pPos);
        ItemStack playerItem = pPlayer.getItemInHand(pHand);
        if (be instanceof BlockEntityKeyLock blockEntityKeyLock)
        {
            if (blockEntityKeyLock.isEmpty())
            {
                if (playerItem.is(ModItems.ITEM_GOLEM_KEY.get()))
                {
                    blockEntityKeyLock.setItem(0, playerItem.copy());
                    playerItem.shrink(1);
                    pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_HAPPY.get(), SoundSource.NEUTRAL, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                    pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_UNLOCK.get(), SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                }
            }
            else if (playerItem.isEmpty())
            {
                ItemStack golemItem = blockEntityKeyLock.removeItem(0, 1);
                pPlayer.setItemInHand(pHand, golemItem);
                pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
                pLevel.playSound(null, pPos, ModSounds.ENTITY_GOLEM_KEY_UNLOCK.get(), SoundSource.BLOCKS, 1.0f, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof BlockEntityKeyLock blockEntityKeyLock) {
                if (!blockEntityKeyLock.isEmpty()) {
                    for(int i = 0; i < blockEntityKeyLock.getContainerSize(); ++i) {
                        ItemStack itemstack = blockEntityKeyLock.getItem(i);
                        if (!itemstack.isEmpty()) {
                            Containers.dropItemStack(pLevel, (double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), itemstack);
                        }
                    }
                    blockEntityKeyLock.clearContent();
                    pLevel.updateNeighbourForOutputSignal(pPos, this);
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}
