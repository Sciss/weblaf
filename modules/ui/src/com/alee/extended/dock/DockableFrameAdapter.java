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

package com.alee.extended.dock;

import com.alee.painter.decoration.states.CompassDirection;

/**
 * {@link com.alee.extended.dock.DockableFrameListener} adapter.
 *
 * @author Mikle Garin
 * @see com.alee.extended.dock.DockableFrameListener
 * @see com.alee.extended.dock.WebDockableFrame
 */

public abstract class DockableFrameAdapter implements DockableFrameListener
{
    @Override
    public void frameOpened ( final WebDockableFrame frame )
    {
        // Do nothing by default
    }

    @Override
    public void frameStateChanged ( final WebDockableFrame frame, final DockableFrameState oldState, final DockableFrameState newState )
    {
        // Do nothing by default
    }

    @Override
    public void frameMoved ( final WebDockableFrame frame, final CompassDirection position )
    {
        // Do nothing by default
    }

    @Override
    public void frameClosed ( final WebDockableFrame frame )
    {
        // Do nothing by default
    }
}