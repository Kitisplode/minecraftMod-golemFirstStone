package com.kitisplode.golemfirststonemod.entity.entity.golem.other;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.action.AgentFollowProgramGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import com.kitisplode.golemfirststonemod.networking.packet.S2CPacketAgentScreenOpen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemAgent extends AbstractGolemDandoriFollower implements ContainerListener, InventoryCarrier, HasCustomInventoryScreen, GeoEntity, IEntityDandoriFollower
{
    public static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/golem_agent.geo.json");
    public static final ResourceLocation TEXTURE_OFF = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_off.png");
    public static final ResourceLocation TEXTURE_ON = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent.png");
    public static final ResourceLocation GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_glowmask.png");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/golem_agent.animation.json");

    private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected SimpleContainer inventory;
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    public EntityGolemAgent(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setMaxUpStep(0.5f);
        this.createInventory();
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(ACTIVE)) this.entityData.define(ACTIVE, false);
    }
    public boolean getActive()
    {
        return this.entityData.get(ACTIVE);
    }
    public void setActive(boolean pBoolean)
    {
        this.entityData.set(ACTIVE, pBoolean);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.writeInventoryToTag(pCompound);
    }
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);
        this.readInventoryFromTag(pCompound);
    }

    @Override
    public void writeInventoryToTag(CompoundTag pCompound)
    {
        ListTag listtag = new ListTag();
        for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }
        pCompound.put("Items", listtag);
    }
    @Override
    public void readInventoryFromTag(CompoundTag pCompound)
    {
        this.createInventory();
        ListTag listtag = pCompound.getList("Items", 10);
        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j < this.inventory.getContainerSize()) {
                this.inventory.setItem(j, ItemStack.of(compoundtag));
            }
        }
    }

    @Override
    public SimpleContainer getInventory()
    {
        return this.inventory;
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    public float getMovementSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2,dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 6));

        this.goalSelector.addGoal(2, new AgentFollowProgramGoal(this));
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

//        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
//        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isPushable()
    {
        return !getActive();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!this.getActive())
        {
            BlockPos bp = this.getOnPos();
            if (this.level().hasNeighborSignal(bp))
            {
                this.setActive(true);
            }
        }
    }

    @Override
    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() != null)
        {
            if (this.distanceToSqr(this.getDeployPosition().getCenter()) < 4) this.setDeployPosition(null);
        }
    }

    protected int getInventorySize() {
        return 16;
    }
    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }
    @Override
    public void containerChanged(Container pContainer)
    {
    }
    public boolean hasInventoryChanged(Container pInventory) {
        return this.inventory != pInventory;
    }
    @Override
    public void openCustomInventoryScreen(Player pPlayer)
    {
        if (!this.level().isClientSide) {
            this.openInventory((ServerPlayer) pPlayer);
        }
    }
    private void openInventory(ServerPlayer pPlayer)
    {
        if (pPlayer.containerMenu != pPlayer.inventoryMenu) {
            pPlayer.closeContainer();
        }

        pPlayer.nextContainerCounter();
        ModMessages.sendToPlayer(new S2CPacketAgentScreenOpen(pPlayer.containerCounter, this.inventory.getContainerSize(), this.getId()), pPlayer);
        pPlayer.containerMenu = new InventoryMenuAgent(pPlayer.containerCounter, pPlayer.getInventory(), this.inventory, this);
        pPlayer.initMenu(pPlayer.containerMenu);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(pPlayer, pPlayer.containerMenu));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand)
    {
        if (this.getActive()) return InteractionResult.PASS;
        this.openCustomInventoryScreen(pPlayer);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }

    public ResourceLocation getTextureLocation()
    {
        if (!this.getActive())
            return TEXTURE_OFF;
        return TEXTURE_ON;
    }

    public ResourceLocation getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
//            EntityGolemAgent pGolem = event.getAnimatable();

            event.getController().setAnimationSpeed(1.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_agent.walk"));

            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_agent.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
