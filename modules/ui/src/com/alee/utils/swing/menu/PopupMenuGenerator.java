/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alee.utils.swing.menu;

import com.alee.laf.menu.WebPopupMenu;
import com.alee.managers.style.StyleId;

/**
 * Special generator that simplifies and shortens {@link WebPopupMenu} creation code.
 *
 * @author Mikle Garin
 * @see AbstractMenuGenerator
 */
public class PopupMenuGenerator extends AbstractMenuGenerator<WebPopupMenu>
{
    /**
     * Constructs new popup menu generator using default popup menu.
     */
    public PopupMenuGenerator ()
    {
        this ( new WebPopupMenu () );
    }

    /**
     * Constructs new popup menu generator using default popup menu using the specified style ID.
     *
     * @param id popup menu style ID
     */
    public PopupMenuGenerator ( final StyleId id )
    {
        this ( new WebPopupMenu ( id ) );
    }

    /**
     * Constructs new popup menu generator using the specified popup menu.
     *
     * @param menu popup menu
     */
    public PopupMenuGenerator ( final WebPopupMenu menu )
    {
        super ( menu );
    }
}