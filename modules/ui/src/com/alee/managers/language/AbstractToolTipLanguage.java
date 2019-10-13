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

package com.alee.managers.language;

import javax.swing.*;

/**
 * This is a base class for any global tooltip {@link LanguageUpdater}s.
 * These might not be used directly, but {@link ToolTipLU} would still use them.
 *
 * @param <C> component type
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-LanguageManager">How to use LanguageManager</a>
 * @see com.alee.managers.language.LanguageManager
 */
public abstract class AbstractToolTipLanguage<C extends JComponent> implements LanguageUpdater<C>
{
    /**
     * Default type for tooltips generated by {@link AbstractToolTipLanguage} implementations.
     */
    protected static String defaultToolTipType;

    /**
     * Returns default type for tooltips generated by {@link AbstractToolTipLanguage} implementations.
     *
     * @return default type for tooltips generated by {@link AbstractToolTipLanguage} implementations
     */
    public static String getDefaultToolTipType ()
    {
        return AbstractToolTipLanguage.defaultToolTipType;
    }

    /**
     * Sets default type for tooltips generated by {@link AbstractToolTipLanguage} implementations.
     *
     * @param type default type for tooltips generated by {@link AbstractToolTipLanguage} implementations
     */
    public static void setDefaultToolTipType ( final String type )
    {
        AbstractToolTipLanguage.defaultToolTipType = type;
    }
}