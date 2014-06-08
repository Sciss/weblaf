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

package com.alee.extended.tab;

import com.alee.laf.splitpane.WebSplitPane;
import com.alee.utils.swing.Customizer;

import java.awt.*;

/**
 * Data for single split pane within document pane.
 * It basically contains split pane and links to two other elements contained within split pane.
 *
 * @author Mikle Garin
 */

public final class SplitData<T extends DocumentData> implements StructureData<T>
{
    protected final WebSplitPane splitPane;
    protected int orientation;
    protected StructureData first;
    protected StructureData last;

    public SplitData ( final WebDocumentPane<T> documentPane, final int orientation, final StructureData first, final StructureData last )
    {
        super ();
        this.orientation = orientation;
        this.first = first;
        this.last = last;

        // Creating split pane
        this.splitPane = createSplit ( orientation, first, last );

        // Customizing split pane
        updateSplitPaneCustomizer ( documentPane );
    }

    protected WebSplitPane createSplit ( final int orientation, final StructureData first, final StructureData last )
    {
        final WebSplitPane splitPane = new WebSplitPane ( orientation, first.getComponent (), last.getComponent () );
        splitPane.putClientProperty ( WebDocumentPane.DATA_KEY, this );
        splitPane.setContinuousLayout ( true );
        splitPane.setDrawDividerBorder ( true );
        splitPane.setDividerSize ( 8 );
        splitPane.setResizeWeight ( 0.5f );
        return splitPane;
    }

    protected void changeSplitOrientation ()
    {
        orientation = orientation == WebSplitPane.HORIZONTAL_SPLIT ? WebSplitPane.VERTICAL_SPLIT : WebSplitPane.HORIZONTAL_SPLIT;
        splitPane.setOrientation ( orientation );
    }

    protected void updateSplitPaneCustomizer ( final WebDocumentPane<T> documentPane )
    {
        final Customizer<WebSplitPane> customizer = documentPane.getSplitPaneCustomizer ();
        if ( customizer != null )
        {
            customizer.customize ( splitPane );
        }
    }

    @Override
    public Component getComponent ()
    {
        return getSplitPane ();
    }

    @Override
    public PaneData<T> findClosestPane ()
    {
        return getFirst ().findClosestPane ();
    }

    public WebSplitPane getSplitPane ()
    {
        return splitPane;
    }

    public int getOrientation ()
    {
        return orientation;
    }

    public void setOrientation ( final int orientation )
    {
        this.orientation = orientation;
    }

    public StructureData getFirst ()
    {
        return first;
    }

    public void setFirst ( final StructureData first )
    {
        this.first = first;
        splitPane.setLeftComponent ( first.getComponent () );
    }

    public StructureData getLast ()
    {
        return last;
    }

    public void setLast ( final StructureData last )
    {
        this.last = last;
        splitPane.setRightComponent ( last.getComponent () );
    }

    public void replace ( final StructureData element, final StructureData replacement )
    {
        if ( first == element )
        {
            first = replacement;
            splitPane.setLeftComponent ( replacement.getComponent () );
        }
        if ( last == element )
        {
            last = replacement;
            splitPane.setRightComponent ( replacement.getComponent () );
        }
    }
}