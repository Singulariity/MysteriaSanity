package com.mysteria.sanity.sanity.listeners;

import com.mysteria.sanity.SanityPlugin;
import com.mysteria.sanity.sanity.enums.SanityPhase;
import com.mysteria.utils.MysteriaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeMilkListener implements Listener {

	public PlayerConsumeMilkListener() {
		Bukkit.getPluginManager().registerEvents(this, SanityPlugin.getInstance());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onMilkConsume(PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.MILK_BUCKET) {

			Player p = e.getPlayer();
			if (SanityPlugin.getSanityManager().isPassed(p, SanityPhase.PHASE_4)) {
				MysteriaUtils.sendMessageDarkRed(p, "You cannot consume milk when your sanity is low.");
				e.setCancelled(true);
			}

		}
	}

}
