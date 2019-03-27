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

package com.alee.laf.viewport;

import com.alee.laf.WebUI;
import com.alee.utils.LafUtils;

import javax.swing.*;
import javax.swing.plaf.ViewportUI;

/**
 * Pluggable look and feel interface for {@link JViewport} component.
 *
 * @param <C> component type
 * @author Mikle Garin
 */
public abstract class WViewportUI<C extends JViewport> extends ViewportUI implements WebUI<C>
{
    /**
     * Runtime variables.
     */
    protected C viewport;

    @Override
    public void installUI ( final JComponent c )
    {
        // Saving viewport reference
        viewport = ( C ) c;

        // Installing default component settings
        installDefaults ();

        // Installing default component listeners
        installListeners ();
    }

    @Override
    public void uninstallUI ( final JComponent c )
    {
        // Uninstalling default component listeners
        uninstallListeners ();

        // Uninstalling default component settings
        uninstallDefaults ();

        // Removing viewport reference
        viewport = null;
    }

    @Override
    public String getPropertyPrefix ()
    {
        return "Viewport.";
    }

    /**
     * Installs default component settings.
     */
    protected void installDefaults ()
    {
        LafUtils.installDefaults ( viewport, getPropertyPrefix () );
    }

    /**
     * Uninstalls default component settings.
     */
    protected void uninstallDefaults ()
    {
        LafUtils.uninstallDefaults ( viewport );
    }

    /**
     * Installs default component listeners.
     */
    protected void installListeners ()
    {
        // Do nothing by default
    }

    /**
     * Uninstalls default component listeners.
     */
    protected void uninstallListeners ()
    {
        // Do nothing by default
    }
}