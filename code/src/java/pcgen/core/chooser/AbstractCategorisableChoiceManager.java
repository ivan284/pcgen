/**
 * AbstractCategorisableChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pcgen.core.Categorisable;
import pcgen.core.CategorisableStore;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * @author Andrew Wilson
 *
 */
public abstract class AbstractCategorisableChoiceManager extends
		AbstractComplexChoiceManager implements ChoiceManagerCategorisable {

	HashMap nameMap    = new HashMap();
	HashMap catMap     = new HashMap();
	boolean useNameMap = true;

	
	/**
	 * @param aPObject
	 * @param aPC
	 */
	public AbstractCategorisableChoiceManager(
			PObject aPObject,
			PlayerCharacter aPC) {
		super(aPObject, aPC);
	}


	/**
	 * 
	 * @param inNumberOfChoices
	 * @param inRequestedSelections
	 * @param inMaxNewSelections
	 */
	public void initialise(
			int inNumberOfChoices,
			int inRequestedSelections,
			int inMaxNewSelections)
	{
		this.numberOfChoices     = inNumberOfChoices;
		this.requestedSelections = inRequestedSelections;
		this.maxNewSelections    = inMaxNewSelections;
	}


	/**
	 * Override Do chooser from the superclass, make sure it does nothing.
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 * @param selectedBonusList
	 * @return an empty list
	 */
	public List doChooser (
		    PlayerCharacter aPc,
		    final List      availableList,
		    final List      selectedList,
		    final List      selectedBonusList)
	{
		Logging.errorPrint("Wrong doChooser called, there is a bug somewhere" );
		return Collections.EMPTY_LIST;
	}

	/**
	 * Choose one or more object from a CategorisableStore.  You may pass in a
	 * list of previous choices, but if it is passed, each item in that list
	 * must be an object in the categorisable store
	 * 
	 * @param store
	 * @param previousSelections
	 * @return a list of the categorisable objects chosen
	 */
	public List doChooser (
			final CategorisableStore 	store,
			final List					previousSelections)
		{

		if (requestedSelections < 0)
		{
			this.requestedSelections = this.maxNewSelections;
		}
		else
		{
			this.requestedSelections -= previousSelections.size();
			this.requestedSelections = Math.min(this.requestedSelections, this.maxNewSelections);
		}

		final int preChooserChoices = previousSelections.size();

		if (this.numberOfChoices > 0)
		{
			// Make sure that we don't try to make the user choose more selections
			// than are available or we'll be in an infinite loop...

			this.numberOfChoices     = Math.min(this.numberOfChoices, store.size() - preChooserChoices);
			this.requestedSelections = this.numberOfChoices;
		}

		boolean showChooser = true;

		for (Iterator abIt = store.getKeyIterator("ALL"); abIt.hasNext();)
		{
			addToMaps((Categorisable) abIt.next());
		}
		
		List availableList = this.useNameMap ? 
				new ArrayList(this.nameMap.keySet()) :
					new ArrayList(this.catMap.keySet());
				
		List selectedList = new ArrayList(); 
					

		/* Convert the list of previous choice objects into a list of keys to
		 * access those in the relevant name or category map.  That is, 
		 * convert them into the format that will be returned by the chooser */
		
		for (Iterator abIt = previousSelections.iterator(); abIt.hasNext();)
		{
			Categorisable Info = (Categorisable) abIt.next();

			if (store.getKeyed(Info.getCategory(), Info.getKeyName()) != null) {
				selectedList.add(
						this.useNameMap ? 
								Info.getName() : 
									Info.getCategory() + " " + Info.getName());
			}
			else
			{
				Logging.errorPrint("alleged previous choice is not valid, Category = " +
						Info.getCategory() + ", Key = " + Info.getName());	
			}
		}

					
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);
		chooser.setAllowsDups(this.dupsAllowed);
		chooser.setVisible(false);
		chooser.setPool(this.requestedSelections);

		title = title + " (" + pobject.getName() + ')';
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);

		while (true)
		{
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.setVisible(showChooser);

			final int selectedSize = chooser.getSelectedList().size() - preChooserChoices;

			if (this.numberOfChoices > 0)
			{
				if (selectedSize != this.numberOfChoices)
				{
					ShowMessageDelegate.showMessageDialog("You must make " +
							(this.numberOfChoices - selectedSize) + " more selection(s).",
							Constants.s_APPNAME, MessageType.INFORMATION);
					continue;
				}
			}

			break;
		}
		
		List chosen = new ArrayList();
		
		for (Iterator abIt = chooser.getSelectedList().iterator(); abIt.hasNext();)
		{
			final String  choice = (String) abIt.next();
			Categorisable Info = this.useNameMap ?
				(Categorisable) this.nameMap.get(choice):
				(Categorisable) this.catMap.get(choice);

			chosen.add(Info);
		}

		return chosen;
	}


	/**
	 * Add the Categorisable object to the maps that will be used to put strings
	 * in the chooser and translate back to the chosen object.
	 * 
	 * @param categorisableObj
	 */
	protected void addToMaps(
			Categorisable categorisableObj)
	{
		Categorisable current = (Categorisable) nameMap.put(categorisableObj.getName(), categorisableObj);
		catMap.put(categorisableObj.getCategory() + " " + categorisableObj.getName(), categorisableObj);

		if (current != null) { useNameMap = false; }
	}

	
}
