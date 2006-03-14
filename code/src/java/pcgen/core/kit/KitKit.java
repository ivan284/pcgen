/*
 * KitKit.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on October 15, 2005, 10:00 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Kit;
import java.util.ArrayList;

/**
 * Applies the Kit
 */
public class KitKit extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String kitStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Kit theKit = null;
	private transient List thingsToAdd = null;

	/**
	 * Constructor
	 * @param aKit
	 */
	public KitKit(final String aKit)
	{
		kitStr = aKit;
	}

	/**
	 * Actually applies the kit to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	public void apply(PlayerCharacter aPC)
	{
		theKit.processKit(aPC, thingsToAdd);
	}

	/**
	 * testApply
	 *
	 * @param aPC PlayerCharacter
	 * @param aKit Kit
	 * @param warnings List
	 * TODO Implement this pcgen.core.kit.BaseKit method
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		// Big hack
		String key = "None|" + kitStr;
		theKit = Globals.getKitKeyed(key);
		if (theKit == null)
		{
			warnings.add("KIT: Kit " + kitStr + " not found.");
			return false;
		}
		thingsToAdd = new ArrayList();
		theKit.testApplyKit(aPC, thingsToAdd, warnings);
		// We actually want this kit to get applied to the temp pc
		theKit.processKit(aPC, thingsToAdd);
		return true;
	}

	public Object clone()
	{
		KitKit aClone = (KitKit)super.clone();
		aClone.kitStr = kitStr;
		return aClone;
	}

	public String getObjectName()
	{
		return "Kit";
	}

	public String toString()
	{
		return kitStr;
	}
}
