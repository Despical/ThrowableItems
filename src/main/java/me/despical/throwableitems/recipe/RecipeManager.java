package me.despical.throwableitems.recipe;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.despical.commons.compat.XMaterial;
import me.despical.commons.string.StringUtils;
import me.despical.commons.util.Collections;
import me.despical.throwableitems.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

/**
 * @author Despical
 * <p>
 * Created at 2.08.2022
 */
public class RecipeManager {

	public RecipeManager(Main plugin) {
		final Material gunPowder = XMaterial.GUNPOWDER.parseMaterial();

		for (String type : Collections.setOf("WOODEN", "STONE", "IRON", "GOLDEN", "DIAMOND", "NETHERITE")) {
			for (String itemName : Collections.setOf("AXE", "HOE", "PICKAXE", "SHOVEL", "SWORD")) {
				final String name = type + "_" + itemName;

				XMaterial.matchXMaterial(name).ifPresent(xMaterial -> {
					ItemStack itemStack = xMaterial.parseItem();

					NBTItem nbtItem = new NBTItem(itemStack);
					nbtItem.setBoolean("ThrowableItem", true);

					itemStack = nbtItem.getItem();

					NamespacedKey key = new NamespacedKey(plugin, "custom_" + name);

					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setDisplayName(ChatColor.RESET + "Throwable " + StringUtils.capitalize(name.toLowerCase(Locale.ENGLISH).replace('_', ' '), ' '));
					itemStack.setItemMeta(itemMeta);

					ShapelessRecipe recipe = new ShapelessRecipe(key, itemStack);
					recipe.addIngredient(xMaterial.parseMaterial());
					recipe.addIngredient(gunPowder);

					plugin.getServer().addRecipe(recipe);
				});
			}
		}
	}
}