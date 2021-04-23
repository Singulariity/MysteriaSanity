package com.mysteria.sanity.sanity.listeners;

import com.mysteria.sanity.SanityPlugin;
import com.mysteria.sanity.sanity.SanityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

	public PlayerDeathListener() {
		Bukkit.getPluginManager().registerEvents(this, SanityPlugin.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		SanityManager sanityManager = SanityPlugin.getSanityManager();

		int sanity_max = sanityManager.getMaxSanity(p);
		sanityManager.setSanity(p, sanity_max);

	}

}
