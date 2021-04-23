package com.mysteria.sanity.sanity.listeners;

import com.mysteria.sanity.SanityPlugin;
import com.mysteria.sanity.sanity.database.SanityDatabase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerCreateDataListener implements Listener {

	public PlayerCreateDataListener() {
		Bukkit.getPluginManager().registerEvents(this, SanityPlugin.getInstance());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlayerJoin(PlayerJoinEvent e) {

		UUID uuid = e.getPlayer().getUniqueId();
		SanityDatabase database = SanityPlugin.getDatabase();

		if (!database.hasData(uuid)) {
			database.createData(uuid);
		}

	}

}
