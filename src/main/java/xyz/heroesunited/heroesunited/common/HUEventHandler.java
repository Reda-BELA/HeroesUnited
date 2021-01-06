package xyz.heroesunited.heroesunited.common;

import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.abilities.ITimerAbility;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.Map;
import java.util.Objects;

public class HUEventHandler {

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving() != null) {
            PlayerEntity pl = (PlayerEntity) event.getEntityLiving();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                AbilityHelper.getAbilities(pl).forEach(type -> {
                    type.onUpdate(pl);
                    if (type instanceof ITimerAbility) {
                        ITimerAbility timer = (ITimerAbility) type;
                        if (a.isInTimer() && a.getTimer() < timer.maxTimer()) {
                            a.setTimer(a.getTimer() + 1);
                        } else if (!a.isInTimer() & a.getTimer() > 0) {
                            a.setTimer(a.getTimer() - 1);
                        }
                    }
                });

                for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                    Ability ability = e.getValue();
                    if (ability != null && ability.alwaysActive()) {
                        a.enable(e.getKey(), ability);
                    }
                }

                if (Suit.getSuit(pl) != null) {
                    Suit.getSuit(pl).onUpdate(pl);
                }

                if (a.isFlying() && !pl.isOnGround() && pl.isSprinting()) {
                    pl.setPose(Pose.SWIMMING);
                }

                if (a.getCooldown() > 0) {
                    a.setCooldown(a.getCooldown() - 1);
                }

                if (a.getAnimationTimer() > 0) a.setAnimationTimer(a.getAnimationTimer() + 1);
                if (a.getAnimationTimer() >= 3600) a.setAnimationTimer(3600);

                if (a.isFlying() && !pl.isOnGround()) {
                    HUPlayerUtil.playSoundToAll(pl.world, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING : HUSounds.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);
                    if (pl.moveForward > 0F) {
                        Vector3d vec = pl.getLookVec();
                        double speed = pl.isSprinting() ? 2.5f : 1f;
                        pl.setMotion(vec.x * speed, vec.y * speed - (pl.isSneaking() ? pl.getHeight() * 0.2F : 0), vec.z * speed);
                    } else if (pl.isSneaking())
                        pl.setMotion(new Vector3d(pl.getMotion().x, pl.getHeight() * -0.2F, pl.getMotion().z));
                    else
                        pl.setMotion(new Vector3d(pl.getMotion().x, Math.sin(pl.ticksExisted / 10F) / 100F, pl.getMotion().z));
                }
            });
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HUCoreCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onChangeEquipment(LivingEquipmentChangeEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity && e.getSlot().getSlotType() == EquipmentSlotType.Group.ARMOR) {
            PlayerEntity player = (PlayerEntity) e.getEntityLiving();
            if (e.getTo().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) e.getTo().getItem();
                if (Suit.getSuit(player) != null) {
                    suitItem.getSuit().onActivated(player);
                    for (Ability ability : AbilityHelper.getAbilities(player)) {
                        if (ability != null && !suitItem.getSuit().canCombineWithAbility(ability, player)) {
                            AbilityHelper.disable(player);
                        }
                    }
                }
            } else if (e.getFrom().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) e.getFrom().getItem();
                suitItem.getSuit().onDeactivated(player);
            }
        }
    }

    @SubscribeEvent
    public void LivingFallEvent(LivingFallEvent e) {
        ModifiableAttributeInstance fallAttribute = e.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
        if (fallAttribute != null) {
            fallAttribute.setBaseValue(e.getDamageMultiplier());
            e.setDamageMultiplier((float) fallAttribute.getValue());
        }
    }

    @SubscribeEvent
    public void LivingJumpEvent(LivingEvent.LivingJumpEvent e) {
        if (!e.getEntityLiving().isCrouching()) {
            e.getEntityLiving().setMotion(e.getEntity().getMotion().x, e.getEntity().getMotion().y + 0.1F * e.getEntityLiving().getAttribute(HUAttributes.JUMP_BOOST).getValue(), e.getEntity().getMotion().z);
        }
    }

    @SubscribeEvent
    public void addListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new HUPackSuperpowers());
    }

    @SubscribeEvent
    public void biomeLoading(BiomeLoadingEvent event) {
        if (BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(event.getName())), BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                    HUBlocks.TITANIUM_ORE.getDefaultState(), 4)).range(32).square().func_242731_b(2));
        }
    }
}
