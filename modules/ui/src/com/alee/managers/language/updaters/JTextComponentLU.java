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

package com.alee.managers.language.updaters;

import com.alee.laf.IInputPrompt;
import com.alee.managers.language.Language;

import javax.swing.text.JTextComponent;

/**
 * Custom {@link LanguageUpdater} for {@link JTextComponent}.
 *
 * @param <C> component type
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-LanguageManager">How to use LanguageManager</a>
 * @see com.alee.managers.language.LanguageManager
 */
public class JTextComponentLU<C extends JTextComponent> extends ToolTipLU<C>
{
    @Override
    public Class getComponentClass ()
    {
        return JTextComponent.class;
    }

    @Override
    public void update ( final C component, final Language language, final String key, final Object... data )
    {
        super.update ( component, language, key, data );

        if ( language.containsText ( key ) )
        {
            component.setText ( language.get ( key, data ) );
        }
        if ( language.containsText ( key, INPUT_PROMPT ) )
        {
            if ( component instanceof IInputPrompt )
            {
                ( ( IInputPrompt ) component ).setInputPrompt ( language.getState ( key, INPUT_PROMPT, data ) );
            }
            else if ( component.getUI () instanceof IInputPrompt )
            {
                ( ( IInputPrompt ) component.getUI () ).setInputPrompt ( language.getState ( key, INPUT_PROMPT, data ) );
            }
        }
    }
}