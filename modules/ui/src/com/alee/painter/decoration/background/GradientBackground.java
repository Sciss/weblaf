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
import com.alee.api.merge.behavior.OverwriteOnMerge;
import com.alee.painter.decoration.DecorationUtils;
import com.alee.painter.decoration.IDecoration;
import com.alee.utils.GraphicsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Gradient {@link IBackground} implementation.
 * Fills component shape with a gradient using multiple colors.
 *
 * @param <C> component type
 * @param <D> decoration type
 * @param <I> background type
 * @author Mikle Garin
 */
@XStreamAlias ( "GradientBackground" )
public class GradientBackground<C extends JComponent, D extends IDecoration<C, D>, I extends GradientBackground<C, D, I>>
        extends AbstractBackground<C, D, I>
{
    /**
     * Gradient type.
     * {@link GradientType#linear} is used if it is not specified.
     */
    @Nullable
    @XStreamAsAttribute
    protected GradientType type;

    /**
     * Bounds width/height percentage representing gradient start point.
     * Point [0,0] is used by default if this value is not specified.
     */
    @Nullable
    @XStreamAsAttribute
    protected Point2D.Float from;

    /**
     * Bounds width/height percentage representing gradient end point.
     * Point [0,1] is used by default if this value is not specified.
     */
    @Nullable
    @XStreamAsAttribute
    protected Point2D.Float to;

    /**
     * {@link List} of {@link GradientColor}s.
     * Must be provided to make background visible.
     */
    @Nullable
    @XStreamImplicit ( itemFieldName = "color" )
    @OverwriteOnMerge
    protected List<GradientColor> colors;

    /**
     * Returns gradient type.
     *
     * @return gradient type
     */
    @NotNull
    protected GradientType getType ()
    {
        return type != null ? type : GradientType.linear;
    }

    /**
     * Returns bounds width/height percentage representing gradient start point.
     *
     * @return bounds width/height percentage representing gradient start point
     */
    @NotNull
    protected Point2D.Float getFrom ()
    {
        return from != null ? from : new Point2D.Float ( 0, 0 );
    }

    /**
     * Returns bounds width/height percentage representing gradient end point.
     *
     * @return bounds width/height percentage representing gradient end point
     */
    @NotNull
    protected Point2D.Float getTo ()
    {
        return to != null ? to : new Point2D.Float ( 0, 1 );
    }

    /**
     * Returns {@link List} of {@link GradientColor}s.
     *
     * @return {@link List} of {@link GradientColor}s
     */
    @Nullable
    public List<GradientColor> getColors ()
    {
        return colors;
    }

    @Override
    public void paint ( final Graphics2D g2d, final Rectangle bounds, final C c, final D d, final Shape shape )
    {
        final float opacity = getOpacity ();
        if ( opacity > 0 )
        {
            final List<GradientColor> colors = getColors ();
            if ( colors != null )
            {
                final Composite oc = GraphicsUtils.setupAlphaComposite ( g2d, opacity, opacity < 1f );
                final Rectangle b = shape.getBounds ();
                final Point2D.Float from = getFrom ();
                final Point2D.Float to = getTo ();
                final int x1 = ( int ) Math.round ( b.x + b.width * from.getX () );
                final int y1 = ( int ) Math.round ( b.y + b.height * from.getY () );
                final int x2 = ( int ) Math.round ( b.x + b.width * to.getX () );
                final int y2 = ( int ) Math.round ( b.y + b.height * to.getY () );
                final Paint paint = DecorationUtils.getPaint ( getType (), colors, x1, y1, x2, y2 );
                final Paint op = GraphicsUtils.setupPaint ( g2d, paint );
                g2d.fill ( shape );
                GraphicsUtils.restorePaint ( g2d, op );
                GraphicsUtils.restoreComposite ( g2d, oc, opacity < 1f );
            }
        }
    }
}