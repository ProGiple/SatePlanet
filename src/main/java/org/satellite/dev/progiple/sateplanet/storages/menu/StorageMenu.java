package org.satellite.dev.progiple.sateplanet.storages.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.Menus.AMenu;
import org.novasparkle.lunaspring.API.Menus.Items.Item;
import org.novasparkle.lunaspring.API.Util.utilities.Utils;
import org.satellite.dev.progiple.sateplanet.SatePlanet;
import org.satellite.dev.progiple.sateplanet.configs.Config;
import org.satellite.dev.progiple.sateplanet.configs.MenuConfig;
import org.satellite.dev.progiple.sateplanet.configs.StorageData;
import org.satellite.dev.progiple.sateplanet.storages.Storage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageMenu extends AMenu {
    private final Set<LootItem> lootItems = new HashSet<>();
    private final Set<Item> panelButtons;
    private final ConfigurationSection itemsSection = StorageData.getSection("items");

    @Getter private final Storage storage;
    @Setter private byte uses = (byte) Config.getInt("storages.uses");
    public StorageMenu(Player player, Storage storage) {
        super(player, MenuConfig.getTitle(), MenuConfig.getSize(), MenuConfig.getSection("items.decorations"));
        this.storage = storage;

        ConfigurationSection panelSection = MenuConfig.getSection("items.panel");
        this.panelButtons = Utils.getSlotList(panelSection.getStringList("slots"))
                .stream()
                .map(s -> new Item(panelSection, s))
                .collect(Collectors.toSet());
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.panelButtons.forEach(b -> b.insert(this));
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        e.setCancelled(true);
        for (Item panelButton : this.panelButtons) {
            if (panelButton.getItemStack().equals(item) && panelButton.getSlot() == e.getSlot()) {
                this.uses--;

                LootItem lootItem = new LootItem(this.itemsSection);
                this.lootItems.add(lootItem);

                panelButton.remove(this);
                this.getInventory().setItem(panelButton.getSlot(), lootItem.getItem());

                if (this.uses <= 0)
                    Bukkit.getScheduler().runTaskLater(SatePlanet.getINSTANCE(), () -> {
                        this.getPlayer().closeInventory();
                    }, 30L);
                return;
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        this.storage.drop(this.lootItems);
    }
}
