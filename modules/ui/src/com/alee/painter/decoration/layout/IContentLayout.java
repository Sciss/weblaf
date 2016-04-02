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

package com.alee.painter.decoration.layout;

import com.alee.api.Identifiable;
import com.alee.api.Mergeable;
import com.alee.managers.style.Bounds;
import com.alee.painter.decoration.IDecoration;
import com.alee.painter.decoration.content.IContent;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * This interface is a base for any custom component content layout.
 * Content layout is optional and will handle layout of {@link com.alee.painter.decoration.content.IContent} placed in decoration.
 *
 * @param <E> component type
 * @param <D> decoration type
 * @param <I> layout type
 * @author Mikle Garin
 */

public interface IContentLayout<E extends JComponent, D extends IDecoration<E, D>, I extends IContentLayout<E, D, I>>
        extends Serializable, Cloneable, Mergeable<I>, Identifiable
{
    /**
     * Returns layout bounds type.
     * Will affect bounds provided into "layout" method.
     *
     * @return content bounds type
     */
    public Bounds getBoundsType ();

    /**
     * Returns contents painting bounds.
     *
     * @param bounds   painting bounds
     * @param c        painted component
     * @param d        painted decoration state
     * @param contents painted contents
     * @return contents painting bounds
     */
    public List<Rectangle> layout ( Rectangle bounds, E c, D d, List<? extends IContent> contents );
}