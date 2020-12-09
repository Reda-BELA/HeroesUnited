package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Ability {

    public final AbilityType type;

    public Ability(AbilityType type) {
        this.type = type;
    }

    public boolean canActivate(PlayerEntity player) {
        return true;
    }

    @Nullable
    public List<String> getHoveredDescription() {
        return null;
    }

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
    }

    public void onDeactivated(PlayerEntity player) {
    }

    public void toggle(PlayerEntity player, int id, int action) {
    }

    //Client Stuff
    @OnlyIn(Dist.CLIENT)
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch){}

    @OnlyIn(Dist.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event){}

    @OnlyIn(Dist.CLIENT)
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {}

    @OnlyIn(Dist.CLIENT)
    public void renderPlayerPost(RenderPlayerEvent.Post event) {}

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side){}

    @OnlyIn(Dist.CLIENT)
    public boolean renderFirstPersonArm(PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawIcon(MatrixStack stack, int x, int y) {
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(new ItemStack(Items.DIAMOND), x, y);
    }
}
