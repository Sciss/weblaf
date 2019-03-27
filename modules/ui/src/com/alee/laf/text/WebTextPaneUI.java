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

package com.alee.laf.text;

import com.alee.api.jdk.Consumer;
import com.alee.api.jdk.Objects;
import com.alee.managers.style.*;
import com.alee.painter.DefaultPainter;
import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.utils.ReflectUtils;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Custom UI for {@link JTextPane} component.
 *
 * @author Mikle Garin
 * @author Alexandr Zernov
 */
public class WebTextPaneUI extends WTextPaneUI implements ShapeSupport, MarginSupport, PaddingSupport
{
    /**
     * Input prompt text.
     */
    protected String inputPrompt;

    /**
     * Component painter.
     */
    @DefaultPainter ( TextPanePainter.class )
    protected ITextPanePainter painter;

    /**
     * Runtime variables.
     */
    protected transient JTextPane textPane = null;

    /**
     * Returns an instance of the {@link WebTextPaneUI} for the specified component.
     * This tricky method is used by {@link UIManager} to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the {@link WebTextPaneUI}
     */
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebTextPaneUI ();
    }

    @Override
    public void installUI ( final JComponent c )
    {
        // Saving text pane reference
        textPane = ( JTextPane ) c;

        super.installUI ( c );

        // Applying skin
        StyleManager.installSkin ( textPane );
    }

    @Override
    public void uninstallUI ( final JComponent c )
    {
        // Uninstalling applied skin
        StyleManager.uninstallSkin ( textPane );

        super.uninstallUI ( c );

        // Removing text pane reference
        textPane = null;
    }

    @Override
    public Shape getShape ()
    {
        return PainterSupport.getShape ( textPane, painter );
    }

    @Override
    public boolean isShapeDetectionEnabled ()
    {
        return PainterSupport.isShapeDetectionEnabled ( textPane, painter );
    }

    @Override
    public void setShapeDetectionEnabled ( final boolean enabled )
    {
        PainterSupport.setShapeDetectionEnabled ( textPane, painter, enabled );
    }

    @Override
    public Insets getMargin ()
    {
        return PainterSupport.getMargin ( textPane );
    }

    @Override
    public void setMargin ( final Insets margin )
    {
        PainterSupport.setMargin ( textPane, margin );
    }

    @Override
    public Insets getPadding ()
    {
        return PainterSupport.getPadding ( textPane );
    }

    @Override
    public void setPadding ( final Insets padding )
    {
        PainterSupport.setPadding ( textPane, padding );
    }

    /**
     * Returns text pane painter.
     *
     * @return text pane painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getPainter ( painter );
    }

    /**
     * Sets text pane painter.
     * Pass null to remove text pane painter.
     *
     * @param painter new text pane painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( textPane, new Consumer<ITextPanePainter> ()
        {
            @Override
            public void accept ( final ITextPanePainter newPainter )
            {
                WebTextPaneUI.this.painter = newPainter;
            }
        }, this.painter, painter, ITextPanePainter.class, AdaptiveTextPanePainter.class );
    }

    @Override
    public String getInputPrompt ()
    {
        return inputPrompt;
    }

    @Override
    public void setInputPrompt ( final String text )
    {
        if ( Objects.notEquals ( text, this.inputPrompt ) )
        {
            this.inputPrompt = text;
            textPane.repaint ();
        }
    }

    @Override
    public boolean contains ( final JComponent c, final int x, final int y )
    {
        return PainterSupport.contains ( c, this, painter, x, y );
    }

    @Override
    protected void paintSafely ( final Graphics g )
    {
        if ( painter != null )
        {
            // Updating painted field
            // This is important for proper basic UI usage
            ReflectUtils.setFieldValueSafely ( this, "painted", true );

            // Painting text component
            final JComponent c = getComponent ();
            painter.paint ( ( Graphics2D ) g, c, this, new Bounds ( c ) );
        }
    }

    @Override
    public Dimension getPreferredSize ( final JComponent c )
    {
        return PainterSupport.getPreferredSize ( c, super.getPreferredSize ( c ), painter );
    }
}