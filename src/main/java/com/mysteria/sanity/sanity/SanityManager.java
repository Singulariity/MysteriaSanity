package com.mysteria.sanity.sanity;

import com.mysteria.customapi.CustomAPIPlugin;
import com.mysteria.customapi.deathmessage.PlayerDeathManager;
import com.mysteria.customapi.deathmessage.enums.CustomDeathReason;
import com.mysteria.customapi.sounds.CustomSound;
import com.mysteria.sanity.SanityPlugin;
import com.mysteria.sanity.sanity.database.Column;
import com.mysteria.sanity.sanity.enums.SanityEffect;
import com.mysteria.sanity.sanity.enums.SanityImpact;
import com.mysteria.sanity.sanity.enums.SanityPhase;
import com.mysteria.sanity.sanity.events.SanityImpactEvent;
import com.mysteria.utils.MysteriaUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;

public class SanityManager {

	public SanityManager() {
		if (SanityPlugin.getSanityManager() != null) {
			throw new IllegalStateException();
		}
		sanityOperationCheck();
		sanityImpactCheck();
		createActionBar();
	}

	private void createActionBar() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.isValid() && p.getGameMode() == GameMode.SURVIVAL) {
						SanityPhase phase = getSanityPhase(p);

						p.sendActionBar(Component.text(phase.getChar() +
								"                                                                       "));
					}
				}

			}
		}.runTaskTimer(SanityPlugin.getInstance(), 0, 40);
	}

	private void sanityOperationCheck() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.isValid() || p.getGameMode() != GameMode.SURVIVAL) continue;

					byte light = p.getLocation().getBlock().getLightLevel();
					int amount = 0;

					if (light <= 8) {
						amount -= new Random().nextInt(2) + 1;
						if (light <= 4) {
							amount -= 1;
							if (light <= 1) {
								amount -= new Random().nextInt(3) + 1;
							}
						}
					} else if (light >= 13) amount += new Random().nextInt(2) + 1;
					else amount++;

					sanityOperation(p, amount);

				}
				sanityOperationCheck();
			}
		}.runTaskLater(SanityPlugin.getInstance(), 20 * (new Random().nextInt(5) + 20));
	}

	private void sanityImpactCheck() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!p.isValid() || p.getGameMode() != GameMode.SURVIVAL) continue;

					int sanity = getSanity(p);
					int chance = (int) ((15 / (((double) sanity) + 1)) * 172);

					if (isPassed(p, SanityPhase.PHASE_3) && MysteriaUtils.chance(chance)) {
						boolean insane = isPassed(p, SanityPhase.PHASE_7);
						giveSanityImpact(p, SanityImpact.values()[new Random().nextInt(SanityImpact.values().length)], insane);
					}

				}
				sanityImpactCheck();
			}
		}.runTaskLater(SanityPlugin.getInstance(), 20 * (new Random().nextInt(11) + 25));
	}

	public void giveSanityEffect(@Nonnull Player p, @Nonnull SanityEffect effect) {
		switch (effect) {
			case HEARTBEAT:
				Bukkit.getScheduler().runTaskLater(SanityPlugin.getInstance(), () -> {
					if (!p.isValid()) return;
					CustomSound.play(p, CustomSound.SANITY_HEARTBEAT, 0.8f, 1);
				}, 15);
				break;
			case BLINDNESS:
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (new Random().nextInt(8) + 4), 0, true, false));
				break;
			case SOUND_LAUGH:
				CustomSound.play(p, CustomSound.SANITY_LAUGH, 1, 1);
				break;
			default:
				break;
		}
	}

	public void giveSanityImpact(@Nonnull Player p, @Nonnull SanityImpact impact, boolean insane) {
		if (!p.isValid()) return;

		SanityImpactEvent sanityImpactEvent = new SanityImpactEvent(p, impact, insane);
		Bukkit.getPluginManager().callEvent(sanityImpactEvent);

		sanityImpactEvent.setImpact(SanityImpact.DAMAGE);
		sanityImpactEvent.setInsane(true);

		if (sanityImpactEvent.isCancelled()) return;

		giveSanityEffect(p, SanityEffect.BLINDNESS);
		if (MysteriaUtils.chance(50)) giveSanityEffect(p, SanityEffect.HEARTBEAT);

		switch (sanityImpactEvent.getImpact()) {
			case WHISPERS:
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 70, 0, true, false));
				CustomSound.stopAll(p);
				CustomSound.play(p, CustomSound.SANITY_WHISPERS, 1, 1);
				break;

			case PUSH:
				giveSanityEffect(p, SanityEffect.SOUND_LAUGH);

				PlayerDeathManager deathManager = CustomAPIPlugin.getDeathManager();
				deathManager.putReason(p, CustomDeathReason.VOID_CREATURE);

				p.damage(new Random().nextInt(4) + 3);

				p.setVelocity(p.getVelocity().add(new Vector(0, 1, 0).multiply(1)));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!p.isValid()) return;
						double x_add = new Random().nextInt(3) - 1;
						double z_add = new Random().nextInt(3) - 1;
						p.setVelocity(p.getVelocity().add(new Vector(x_add, 1, z_add).multiply(1)));
					}
				}.runTaskLater(SanityPlugin.getInstance(), 15);
				break;

			case DAMAGE:
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!p.isValid()) return;
						CustomSound.play(p, CustomSound.OTHER_SWORD_SLICE, 1, 1);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (!p.isValid()) return;

								PlayerDeathManager deathManager = CustomAPIPlugin.getDeathManager();
								deathManager.putReason(p, CustomDeathReason.VOID_CREATURE);

								if (sanityImpactEvent.isInsane()) p.setHealth(0);
								else p.damage(new Random().nextInt(3) + 4);

								giveSanityEffect(p, SanityEffect.SOUND_LAUGH);
							}
						}.runTaskLater(SanityPlugin.getInstance(), 7);
					}
				}.runTaskLater(SanityPlugin.getInstance(), 40);
				break;

			case DIRECTION_CHANGE:
				Location loc = p.getLocation().clone();
				loc.setPitch(new Random().nextInt(181) - 90);
				loc.setYaw(new Random().nextInt(361));
				p.teleport(loc);
				break;

			default:
				break;
		}

	}

	@Nonnull
	public SanityPhase getSanityPhase(@Nonnull Player p) {
		int sanity = getSanity(p);
		int sanityMax = getMaxSanity(p);
		if (sanityMax != 100) sanity = (int) (((double) sanity / sanityMax) * 100);

		if (sanity > 90) {
			return SanityPhase.PHASE_0;
		} else if (sanity > 80) {
			return SanityPhase.PHASE_1;
		} else if (sanity > 70) {
			return SanityPhase.PHASE_2;
		} else if (sanity > 60) {
			return SanityPhase.PHASE_3;
		} else if (sanity > 50) {
			return SanityPhase.PHASE_4;
		} else if (sanity > 40) {
			return SanityPhase.PHASE_5;
		} else if (sanity > 30) {
			return SanityPhase.PHASE_6;
		} else if (sanity > 20) {
			return SanityPhase.PHASE_7;
		} else return SanityPhase.PHASE_8;
	}

	public boolean isPassed(@Nonnull Player p, @Nonnull SanityPhase phase) {
		SanityPhase playerPhase = getSanityPhase(p);

		return playerPhase.getWeight() >= phase.getWeight();
	}

	public int sanityOperation(@Nonnull Player p, @Nonnull Integer i) {
		int sanity = getSanity(p);
		if (i == 0) return sanity;
		int sanityMax = getMaxSanity(p);

		if (sanity + i > sanityMax) {
			sanity = sanityMax;
		} else sanity = Math.max(sanity + i, 0);

		setSanity(p, sanity);
		return sanity;
	}

	public void setSanity(@Nonnull Player p, int value) {
		SanityPlugin.getDatabase().setInt(p.getUniqueId(), Column.SANITY, value);
	}

	public int getSanity(@Nonnull Player p) {
		Integer sanity = SanityPlugin.getDatabase().getInt(p.getUniqueId(), Column.SANITY);
		return sanity != null ? sanity : 0;
	}

	public int getMaxSanity(@Nonnull Player p) {
		Integer sanity_max = SanityPlugin.getDatabase().getInt(p.getUniqueId(), Column.SANITY_MAX);
		return sanity_max != null ? sanity_max : 100;
	}
}
