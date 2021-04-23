package com.mysteria.sanity.sanity.events;

import com.mysteria.sanity.sanity.enums.SanityImpact;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class SanityImpactEvent extends Event implements Cancellable {

	private final Player player;
	private SanityImpact impact;
	private boolean isCancelled;
	private boolean isInsane;

	public SanityImpactEvent(@Nonnull Player player, @Nonnull SanityImpact impact, boolean isInsane) {
		this.player = player;
		this.impact = impact;
		this.isCancelled = false;
		this.isInsane = isInsane;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public @Nonnull
	HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Player getPlayer() {
		return player;
	}

	public SanityImpact getImpact() {
		return impact;
	}

	public void setImpact(@Nonnull SanityImpact impact) {
		this.impact = impact;
	}

	public boolean isInsane() {
		return isInsane;
	}

	public void setInsane(boolean insane) {
		isInsane = insane;
	}

}
