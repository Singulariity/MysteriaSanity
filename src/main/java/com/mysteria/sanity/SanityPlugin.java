package com.mysteria.sanity;

import com.mysteria.sanity.sanity.SanityManager;
import com.mysteria.sanity.sanity.database.SanityDatabase;
import com.mysteria.sanity.sanity.listeners.PlayerConsumeMilkListener;
import com.mysteria.sanity.sanity.listeners.PlayerCreateDataListener;
import com.mysteria.sanity.sanity.listeners.PlayerDeathListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SanityPlugin extends JavaPlugin {

	private static SanityPlugin instance;
	private static SanityManager sanityManager;
	private static SanityDatabase database;
	private static Connection connection;

	public SanityPlugin() {
		if (instance != null) throw new IllegalStateException();
		instance = this;
	}

	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("MysteriaUtils")) {
			getLogger().severe("*** MysteriaUtils is not installed or not enabled. ***");
			getLogger().severe("*** This plugin will be disabled. ***");
			this.setEnabled(false);
			return;
		}

		setupDatabase();
		sanityManager = new SanityManager();

		registerListeners();
	}

	private void registerListeners() {
		new PlayerConsumeMilkListener();
		new PlayerCreateDataListener();
		new PlayerDeathListener();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void setupDatabase() {
		HikariConfig config = new HikariConfig();

		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		try {
			String url = "jdbc:sqlite:./" + this.getDataFolder() + "/sanity_data.db";

			config.setJdbcUrl(url);
			config.setUsername("mysteria");

			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			HikariDataSource ds = new HikariDataSource(config);

			connection = ds.getConnection();
			Statement st = connection.createStatement();
			st.execute("CREATE TABLE IF NOT EXISTS sanity_data (" +
					"PLAYER UUID NOT NULL PRIMARY KEY, " +
					"SANITY INT NOT NULL DEFAULT '100', " +
					"SANITY_MAX INT NOT NULL DEFAULT '100')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		database = new SanityDatabase();
	}



	@Nonnull
	public static SanityPlugin getInstance() {
		if (instance == null) throw new IllegalStateException();
		return instance;
	}

	public static SanityManager getSanityManager() {
		return sanityManager;
	}

	public static SanityDatabase getDatabase() {
		return database;
	}

	public static Connection getConnection() {
		return connection;
	}
}
