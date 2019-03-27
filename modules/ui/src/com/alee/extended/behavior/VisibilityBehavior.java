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

package com.alee.extended.behavior;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * Custom {@link Behavior} that allows you to track {@link Component} visibility.
 * You need to specify {@link Component} for which visibility state will be tracked.
 * Use {@link #install()} and {@link #uninstall()} methods to setup and remove this behavior.
 *
 * @param <C> {@link Component} type
 * @author Mikle Garin
 */
public abstract class VisibilityBehavior<C extends Component> extends AbstractComponentBehavior<C> implements HierarchyListener
{
    /**
     * Whether or not should artificially trigger events on {@link #install()} and {@link #uninstall()}.
     * If set to {@code true} - {@link #displayed()} and {@link #hidden()} will be triggered according to {@link #component} visibility.
     */
    private final boolean initTriggers;

    /**
     * Whether or not {@link #component} is currently visible and placed on a displayable {@link Window}.
     * This doesn't always mean that {@link #component} is actually visible on the screen for user right now.
     * It can be hidden behind a scroll, overlaying component or some other Swing shenanigans.
     * But those cases are really hard to track and it is not necessary anyway.
     */
    protected boolean visible;

    /**
     * Constructs new {@link VisibilityBehavior} for the specified {@link Component}.
     *
     * @param component {@link Component} into which this behavior is installed
     */
    public VisibilityBehavior ( final C component )
    {
        this ( component, false );
    }

    /**
     * Constructs new {@link VisibilityBehavior} for the specified {@link Component}.
     *
     * @param component    {@link Component} into which this behavior is installed
     * @param initTriggers whether or not should artificially trigger events on {@link #install()} and {@link #uninstall()}
     */
    public VisibilityBehavior ( final C component, final boolean initTriggers )
    {
        super ( component );
        this.initTriggers = initTriggers;
        this.visible = false;
    }

    /**
     * Installs behavior into {@link Component}.
     */
    public void install ()
    {
        visible = component.isShowing ();
        component.addHierarchyListener ( this );
        if ( initTriggers && visible )
        {
            displayed ();
        }
    }

    /**
     * Uninstalls behavior from the {@link Component}.
     */
    public void uninstall ()
    {
        if ( initTriggers && visible )
        {
            hidden ();
        }
        component.removeHierarchyListener ( this );
        visible = false;
    }

    /**
     * Returns {@link Component} into which this behavior is installed.
     *
     * @return {@link Component} into which this behavior is installed
     */
    public C getComponent ()
    {
        return component;
    }

    /**
     * Returns whether or not {@link Component} is currently visible.
     *
     * @return {@code true} if {@link Component} is currently visible, {@code false} otherwise
     */
    public boolean isVisible ()
    {
        return visible;
    }

    @Override
    public void hierarchyChanged ( final HierarchyEvent e )
    {
        if ( e.getID () == HierarchyEvent.HIERARCHY_CHANGED )
        {
            checkVisibility ();
        }
    }

    /**
     * Performs {@link Component} visibility check.
     */
    protected void checkVisibility ()
    {
        final boolean nowVisible = component.isShowing ();
        if ( visible != nowVisible )
        {
            if ( nowVisible )
            {
                visible = true;
                displayed ();
            }
            else
            {
                visible = false;
                hidden ();
            }
        }
    }

    /**
     * Called when {@link Component} becomes visible according to behavior conditions.
     */
    public abstract void displayed ();

    /**
     * Called when {@link Component} becomes hidden according to behavior conditions.
     */
    public abstract void hidden ();
}