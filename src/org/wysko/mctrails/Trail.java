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

import org.bukkit.Material;
import org.bukkit.Particle;

import java.io.Serializable;

public class Trail implements Serializable {
	
	private static final long serialVersionUID = 58979L;
	
	final public static Trail DEFAULT_TRAIL = new Trail("Default", Particle.SNEEZE, Material.STONE, 200, 1);
	
	final String trailName;
	final Particle particle;
	final Material guiIcon;
	int rate;
	int amount;
	
	public Trail(String trailName, Particle particle, Material guiIcon, int rate, int amount) {
		this.trailName = trailName;
		this.particle = particle;
		this.guiIcon = guiIcon;
		this.rate = rate;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "Trail{" + "trailName='" + trailName + '\'' +
				", particle=" + particle +
				", guiIcon=" + guiIcon +
				'}';
	}
}
