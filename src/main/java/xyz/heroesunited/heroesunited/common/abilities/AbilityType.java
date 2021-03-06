package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityType extends ForgeRegistryEntry<AbilityType> {

    public static IForgeRegistry<AbilityType> ABILITIES;
    private Supplier<Ability> supplier;

    public AbilityType(Supplier<Ability> supplier) {
        this.supplier = supplier;
    }

    public AbilityType(Supplier<Ability> supplier, String modid, String name) {
        this.supplier = supplier;
        this.setRegistryName(modid, name);
    }

    public Ability create(String id) {
        Ability a = this.supplier.get();
        a.name = id;
        return a;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("ability", this.getRegistryName()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        ABILITIES = new RegistryBuilder<AbilityType>().setName(new ResourceLocation(HeroesUnited.MODID, "ability_types")).setType(AbilityType.class).setIDRange(0, 2048).create();
    }

    public static final AbilityType ATTRIBUTE_MODIFIER = new AbilityType(AttributeModifierAbility::new, HeroesUnited.MODID, "attribute_modifier");
    public static final AbilityType FLIGHT = new AbilityType(FlightAbility::new, HeroesUnited.MODID, "flight");
    public static final AbilityType SLOW_MO = new AbilityType(SlowMoAbility::new, HeroesUnited.MODID, "slow_mo");
    public static final AbilityType GECKO = new AbilityType(GeckoAbility::new, HeroesUnited.MODID, "gecko");
    public static final AbilityType HIDE_BODY_PARTS = new AbilityType(HideBodyPartsAbility::new, HeroesUnited.MODID, "hide_body_parts");
    public static final AbilityType ROTATE_PARTS = new AbilityType(RotatePartsAbility::new, HeroesUnited.MODID, "rotate_parts");
    public static final AbilityType EYE_HEIGHT = new AbilityType(EyeHeightAbility::new, HeroesUnited.MODID, "eye_height");
    public static final AbilityType COMMAND = new AbilityType(CommandAbility::new, HeroesUnited.MODID, "command");
    public static final AbilityType DAMAGE_IMMUNITY = new AbilityType(DamageImmunityAbility::new, HeroesUnited.MODID, "damage_immunity");

    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<AbilityType> e) {
        e.getRegistry().register(ATTRIBUTE_MODIFIER);
        e.getRegistry().register(FLIGHT);
        e.getRegistry().register(SLOW_MO);
        e.getRegistry().register(GECKO);
        e.getRegistry().register(HIDE_BODY_PARTS);
        e.getRegistry().register(EYE_HEIGHT);
        e.getRegistry().register(COMMAND);
        e.getRegistry().register(DAMAGE_IMMUNITY);
        e.getRegistry().register(ROTATE_PARTS);
    }
}