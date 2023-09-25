package com.kitisplode.golemfirststonemod.entity.entity.golem.other;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.action.AgentFollowProgramGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.kitisplode.golemfirststonemod.mixin.MixinServerPlayerEntityAccessor;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import com.kitisplode.golemfirststonemod.networking.packet.S2CPacketAgentScreenOpen;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemAgent extends AbstractGolemDandoriFollower implements InventoryChangedListener, RideableInventory, GeoEntity, IEntityDandoriFollower
{
    public static final Identifier MODEL = new Identifier(GolemFirstStoneMod.MOD_ID, "geo/golem_agent.geo.json");
    public static final Identifier TEXTURE_OFF = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_off.png");
    public static final Identifier TEXTURE_ON = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent.png");
    public static final Identifier GLOWMASK = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/other/golem_agent_glowmask.png");
    public static final Identifier ANIMATIONS = new Identifier(GolemFirstStoneMod.MOD_ID, "animations/golem_agent.animation.json");

    private static final TrackedData<Boolean> ACTIVE = DataTracker.registerData(EntityGolemAgent.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SWINGING_ARM = DataTracker.registerData(EntityGolemAgent.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> ARM_SWING = DataTracker.registerData(EntityGolemAgent.class, TrackedDataHandlerRegistry.FLOAT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final float armSwingAmount = 0.15f;
    private static final float armSwingAmountStart = 180.0f;
    protected SimpleInventory inventory;

    public EntityGolemAgent(EntityType<? extends IronGolemEntity> entityType, World world)
    {
        super(entityType, world);
        this.setStepHeight(0.5f);
        this.onChestedStatusChanged();
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.25f);
    }


    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        if (!this.dataTracker.containsKey(ACTIVE)) this.dataTracker.startTracking(ACTIVE, false);
        if (!this.dataTracker.containsKey(SWINGING_ARM)) this.dataTracker.startTracking(SWINGING_ARM, false);
        if (!this.dataTracker.containsKey(ARM_SWING)) this.dataTracker.startTracking(ARM_SWING, 0.0f);
    }
    public boolean getActive()
    {
        return this.dataTracker.get(ACTIVE);
    }
    public void setActive(boolean pBoolean)
    {
        this.dataTracker.set(ACTIVE, pBoolean);
    }
    public float getArmSwing()
    {
        return this.dataTracker.get(ARM_SWING);
    }
    private void setArmSwing(float pFloat)
    {
        this.dataTracker.set(ARM_SWING, pFloat);
    }
    public void swingArm()
    {
        this.dataTracker.set(ARM_SWING, armSwingAmountStart);
    }
    public boolean getSwingingArm()
    {
        return this.dataTracker.get(SWINGING_ARM);
    }
    public void setSwingingArm(boolean pBoolean)
    {
        this.dataTracker.set(SWINGING_ARM, pBoolean);
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        this.writeInventoryToTag(nbt);
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.readInventoryFromTag(nbt);
    }
    public void writeInventoryToTag(NbtCompound pCompound)
    {
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            itemStack.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        pCompound.put("Items", nbtList);
    }
    public void readInventoryFromTag(NbtCompound pCompound)
    {
        NbtList nbtList = pCompound.getList("Items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            if (j >= this.inventory.size()) continue;
            this.inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
        }
    }

    public SimpleInventory getInventory()
    {
        return this.inventory;
    }

    public ItemStack getHeldItem()
    {
        return this.inventory.getStack(0);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void initGoals()
    {
        this.goalSelector.add(0, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.add(1, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 6));

        this.goalSelector.add(2, new AgentFollowProgramGoal(this));
        this.goalSelector.add(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(4, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));
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
            BlockPos bp = this.getBlockPos();
            if (this.getWorld().isReceivingRedstonePower(bp))
            {
                this.setActive(true);
            }
        }
        if (this.getArmSwing() > 0.0f)
        {
            float swing = MathHelper.lerp(armSwingAmount, this.getArmSwing(), 0.0f);
            this.setArmSwing(swing);
            this.setSwingingArm(true);
        }
        else this.setSwingingArm(false);
    }
    @Override
    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() != null)
        {
            if (this.squaredDistanceTo(this.getDeployPosition().toCenterPos()) < 4) this.setDeployPosition(null);
        }
    }

    protected int getInventorySize()
    {
        return 16;
    }
    protected void onChestedStatusChanged()
    {
        SimpleInventory simpleInventory = this.inventory;
        this.inventory = new SimpleInventory(this.getInventorySize());
        if (simpleInventory != null) {
            simpleInventory.removeListener(this);
            int i = Math.min(simpleInventory.size(), this.inventory.size());
            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = simpleInventory.getStack(j);
                if (itemStack.isEmpty()) continue;
                this.inventory.setStack(j, itemStack.copy());
            }
        }
        this.inventory.addListener(this);
    }
    @Override
    public void onInventoryChanged(Inventory sender)
    {
        if (!this.getWorld().isClient())
            this.equipStack(EquipmentSlot.MAINHAND, this.getHeldItem());
    }
    public boolean areInventoriesDifferent(Inventory inventory)
    {
        return this.inventory != inventory;
    }
    public void openInventory(PlayerEntity player)
    {
        if (!this.getWorld().isClient)
        {
            this.openInventory((ServerPlayerEntity) player);
        }
    }
    private void openInventory(ServerPlayerEntity pPlayer)
    {
        if (pPlayer.currentScreenHandler != pPlayer.playerScreenHandler) {
            pPlayer.closeHandledScreen();
        }
        ((MixinServerPlayerEntityAccessor)pPlayer).invoke_incrementScreenHandlerSyncId();
        S2CPacketAgentScreenOpen packet = new S2CPacketAgentScreenOpen(((MixinServerPlayerEntityAccessor)pPlayer).getScreenHandlerSyncId(), inventory.size(), this.getId());
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        ServerPlayNetworking.send(pPlayer, ModMessages.SCREEN_AGENT_OPEN, PacketByteBufs.copy(buf));
        pPlayer.currentScreenHandler = new InventoryMenuAgent(((MixinServerPlayerEntityAccessor)pPlayer).getScreenHandlerSyncId(), pPlayer.getInventory(), inventory, this);
        ((MixinServerPlayerEntityAccessor)pPlayer).invoke_onScreenHandlerOpened(pPlayer.currentScreenHandler);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand)
    {
        ItemStack playerItem = player.getStackInHand(hand);
        if (playerItem.isOf(ModItems.ITEM_DANDORI_STAFF)) return ActionResult.PASS;
        if (this.getActive()) return ActionResult.PASS;
        this.openInventory(player);
        return ActionResult.success(this.getWorld().isClient());
    }

    public boolean tryAttack(Entity target)
    {
        int i;
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float g = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
            g += (float)EnchantmentHelper.getKnockback(this);
        }
        if ((i = EnchantmentHelper.getFireAspect(this)) > 0) {
            target.setOnFireFor(i * 4);
        }
        boolean bl = target.damage(this.getDamageSources().mobAttack(this), f);
        if (bl) {
            if (g > 0.0f && target instanceof LivingEntity) {
                ((LivingEntity)target).takeKnockback(g * 0.5f, MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (target instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)target;
                this.disablePlayerShield(playerEntity, this.getMainHandStack(), playerEntity.isUsingItem() ? playerEntity.getActiveItem() : ItemStack.EMPTY);
            }
            this.applyDamageEffects(this, target);
            this.onAttacking(target);
        }
        return bl;
    }
    private void disablePlayerShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack) {
        if (!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.getItem() instanceof AxeItem && playerStack.isOf(Items.SHIELD)) {
            float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < f) {
                player.getItemCooldownManager().set(Items.SHIELD, 100);
                this.getWorld().sendEntityStatus(player, EntityStatuses.BREAK_SHIELD);
            }
        }
    }

    @Override
    public boolean canTarget(LivingEntity pTarget) {
        if (this.getOwner() == null) return super.canTarget(pTarget);
        if (pTarget instanceof IEntityDandoriFollower dandoriFollower)
        {
            if (this.getOwner() == dandoriFollower.getOwner()) return false;
            if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower targetOwner)
            {
                if (this.getOwner() == targetOwner.getOwner()) return false;
            }
        }
        if (pTarget instanceof TameableEntity tamableAnimal && this.getOwner() == tamableAnimal.getOwner()) return false;
        return super.canTarget(pTarget);
    }

    public Identifier getModelLocation()
    {
        return MODEL;
    }

    public Identifier getTextureLocation()
    {
        if (!this.getActive())
            return TEXTURE_OFF;
        return TEXTURE_ON;
    }

    public Identifier getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            event.getController().setAnimationSpeed(1.00);
            if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
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
