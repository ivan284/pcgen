/*
 * MessageType.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.utils;

public class MessageType {
    private final String name;

    private MessageType(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    // Prevent subclasses from overriding Object.equals
    public final boolean equals(final Object that) {
        return super.equals(that);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public static final MessageType INFORMATION = new MessageType("Information"); //$NON-NLS-1$

    public static final MessageType WARNING = new MessageType("Warning"); //$NON-NLS-1$

    public static final MessageType ERROR = new MessageType("Error"); //$NON-NLS-1$

    public static final MessageType QUESTION = new MessageType("Question"); //$NON-NLS-1$
}