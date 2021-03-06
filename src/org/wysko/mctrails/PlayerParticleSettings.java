/*
 * MIT License
 *
 * Copyright (c) 2020 Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.wysko.mctrails;

import java.io.Serializable;
import java.util.UUID;

public class PlayerParticleSettings implements Serializable {
	
	private static final long serialVersionUID = 92653L;
	
	
	/**
	 * The UUID of the Player that these settings apply to.
	 */
	final UUID playerUUID;
	
	/**
	 * The trail that should generate around the player.
	 */
	Trail trail;
	
	/**
	 * @param playerUUID the UUID of the player that these settings relate to
	 * @param trail      the Trail to display
	 */
	public PlayerParticleSettings(UUID playerUUID, Trail trail) {
		this.playerUUID = playerUUID;
		this.trail = trail;
	}
	
	@Override
	public String toString() {
		return "PlayerParticleSettings{" + "playerUUID=" + playerUUID +
				", trail=" + trail +
				'}';
	}
}
