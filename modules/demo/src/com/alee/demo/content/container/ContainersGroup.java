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

package com.alee.demo.content.container;

import com.alee.api.annotations.NotNull;
import com.alee.demo.api.example.AbstractExampleGroup;
import com.alee.utils.CollectionUtils;

import java.util.List;

/**
 * @author Mikle Garin
 */
public class ContainersGroup extends AbstractExampleGroup
{
    @NotNull
    @Override
    public String getId ()
    {
        return "containers";
    }

    @Override
    protected List<Class> getExampleClasses ()
    {
        return CollectionUtils.<Class>asList (
                JPanelExample.class,
                WebPanelExample.class,
                JScrollPaneExample.class,
                JSplitPaneExample.class,
                WebMultiSplitPaneExample.class,
                JToolBarExample.class,
                JTabbedPaneExample.class,
                WebDocumentPaneExample.class,
                GroupPaneExample.class,
                WebBreadcrumbExample.class,
                WebCollapsiblePaneExample.class,
                WebAccordionExample.class
        );
    }
}