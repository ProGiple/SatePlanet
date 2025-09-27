package org.satellite.dev.progiple.sateplanet.planets.menu;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.MoveIgnored;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.menus.items.NonMenuItem;
import org.satellite.dev.progiple.sateplanet.Tools;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;

import java.util.ArrayList;
import java.util.List;

@MoveIgnored @Getter
public class EditMenu extends AMenu {
    private final int pageIndex;
    public EditMenu(@NotNull Player player, int pageIndex) {
        super(player, Config.getString("messages.editMenu_title"), (byte) 54);
        this.pageIndex = pageIndex;

        this.addItems(true,
                new Item(Material.TIPPED_ARROW, "&b<- Обратно", new ArrayList<>(), 1, (byte) 0) {
                    @Override
                    public Item onClick(InventoryClickEvent e) {
                        if (pageIndex > 0) Tools.openEditMenu(player, pageIndex - 1);
                        return super.onClick(e);
                    }
                }.setSlot((byte) 52),
                new Item(Material.TIPPED_ARROW, "&bДалее ->", new ArrayList<>(), 1, (byte) 0) {
                    @Override
                    public Item onClick(InventoryClickEvent e) {
                        if (pageIndex < 24) Tools.openEditMenu(player, pageIndex + 1);
                        return super.onClick(e);
                    }
                }.setSlot((byte) 53));

        ConfigurationSection section = StorageData.getSection("items");
        if (section == null) return;

        int dub = 54 * pageIndex;
        for (int i = dub; i < dub + 54; i++) {
            ConfigurationSection itemSection = section.getConfigurationSection(String.valueOf(i));
            if (itemSection == null) {
                ItemStack itemStack = section.getItemStack(String.valueOf(i));
                if (itemStack == null) continue;

                this.getInventory().setItem(i - dub, itemStack);
            }
            else {
                NonMenuItem nonMenuItem = new NonMenuItem(itemSection);
                this.getInventory().setItem(i - dub, nonMenuItem.getItemStack());
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            return;
        }

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir()) return;

        for (Item item : this.getItemList()) {
            if (item.getItemStack().equals(itemStack) && item.getSlot() == e.getSlot()) {
                item.onClick(e);
                return;
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        for (int i = 0; i < 52; i++) {
            ItemStack stack = inventory.getItem(i);
            StorageData.setItem(i + (this.pageIndex * 54), stack);
        }
        StorageData.save();
    }

    @Override
    public void onDrag(InventoryDragEvent e) {
    }
}
