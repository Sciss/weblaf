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

package com.alee.painter.decoration.background;

import com.alee.api.annotations.NotNull;
import com.alee.api.annotations.Nullable;
import com.alee.painter.decoration.IDecoration;
import com.alee.utils.GraphicsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import javax.swing.*;
import java.awt.*;

/**
 * Single color background.
 * Fills component shape with a single color.
 *
 * @param <C> component type
 * @param <D> decoration type
 * @param <I> background type
 * @author Mikle Garin
 */
@XStreamAlias ( "ColorBackground" )
public class ColorBackground<C extends JComponent, D extends IDecoration<C, D>, I extends ColorBackground<C, D, I>>
        extends AbstractBackground<C, D, I>
{
    /**
     * Background color.
     */
    @Nullable
    @XStreamAsAttribute
    protected Color color;

    /**
     * Returns background color.
     *
     * @return background color
     */
    @NotNull
    protected Color getColor ()
    {
        return color != null ? color : Color.WHITE;
    }

    @Override
    public void paint ( final Graphics2D g2d, final Rectangle bounds, final C c, final D d, final Shape shape )
    {
        final float opacity = getOpacity ();
        if ( opacity > 0 )
        {
            final Composite oc = GraphicsUtils.setupAlphaComposite ( g2d, opacity, opacity < 1f );
            final Paint op = GraphicsUtils.setupPaint ( g2d, getColor () );
            g2d.fill ( shape );
            GraphicsUtils.restorePaint ( g2d, op );
            GraphicsUtils.restoreComposite ( g2d, oc, opacity < 1f );
        }
    }
}