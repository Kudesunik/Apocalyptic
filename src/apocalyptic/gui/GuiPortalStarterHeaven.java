package apocalyptic.gui;

import apocalyptic.container.ContainerPortalHeaven;
import apocalyptic.tile.TileEntityPortalStarterHeaven;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortalStarterHeaven extends GuiContainer {
	public TileEntityPortalStarterHeaven tileentity;

    public GuiPortalStarterHeaven(InventoryPlayer player_inventory, TileEntityPortalStarterHeaven tile_entity){
            super(new ContainerPortalHeaven(tile_entity, player_inventory));
            this.tileentity = tile_entity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j){
            fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 92 , 0xffffff);
            fontRenderer.drawString("\u041A\u043E\u043D\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044F \u043F\u043E\u0440\u0442\u0430\u043B\u0430: \u0420\u0430\u0439", 10, 8, 0x3aff00);
            fontRenderer.drawString("\u0421\u0442\u0430\u0442\u0443\u0441: " + tileentity.getStatus(), 10, 16, 0x3aff00);
            fontRenderer.drawString("\u0422\u0440\u0435\u0431\u0443\u0435\u043C\u0430\u044F \u044D\u043D\u0435\u0440\u0433\u0438\u044F: " + tileentity.getCapacity() + " Eu", 10, 32, 0x3aff00);
            fontRenderer.drawString("\u042D\u043D\u0435\u0440\u0433\u0438\u044F \u043F\u043E\u0434\u0434\u0435\u0440\u0436\u0430\u043D\u0438\u044F: 0 Eu/t", 10, 40, 0x3aff00);
            fontRenderer.drawString("\u041D\u0430\u043A\u043E\u043F\u043B\u0435\u043D\u043E: " + tileentity.getStored() + " Eu", 10, 48, 0x3aff00);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j){
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            this.mc.renderEngine.bindTexture("/apocalyptic/sprites/GUIPortalHeaven.png");

            int x = (width - xSize) / 2;

            int y = (height - ySize) / 2;

            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}