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

import com.alee.api.annotations.NotNull;
import com.alee.api.jdk.Function;
import com.alee.extended.behavior.VisibilityBehavior;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.drag.DragManager;
import com.alee.managers.settings.Configuration;
import com.alee.managers.settings.SettingsProcessor;
import com.alee.managers.settings.UISettingsManager;
import com.alee.managers.style.StyleId;
import com.alee.utils.CollectionUtils;
import com.alee.utils.TextUtils;
import com.alee.utils.general.Pair;
import com.alee.utils.swing.Customizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link WebDocumentPane} is a special container for multiple documents described by {@link DocumentData} class.
 * This component uses either single or multiple {@link JTabbedPane}s and allow tabs reorder, drag, split and closeability.
 * It also allows usage of custom {@link DocumentData} implementations to adjust it's functionality or add custom data into it.
 *
 * @param <T> document type
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-WebDocumentPane">How to use WebDocumentPane</a>
 * @see PaneData
 * @see SplitData
 * @see DocumentData
 */
public class WebDocumentPane<T extends DocumentData> extends WebPanel implements DocumentPaneEventMethods<T>
{
    /**
     * todo 1. Separate UI and make use of styling system
     * todo 2. Possibility to edit tab title
     */

    /**
     * Constant key used to put pane element data into the UI component.
     */
    protected static final String DATA_KEY = "document.pane.data";

    /**
     * Unique document pane ID.
     * Used to allow or disallow documents drag between different document panes.
     */
    protected final String id;

    /**
     * Root structure element.
     * Might either be PaneData or SplitData.
     */
    protected StructureData root;

    /**
     * Last active pane.
     */
    protected PaneData<T> activePane;

    /**
     * Tabbed panes customizer.
     */
    protected Customizer<WebTabbedPane> tabbedPaneCustomizer;

    /**
     * Document tab title view customizer.
     */
    protected TabTitleComponentProvider<T> tabTitleComponentProvider;

    /**
     * Document customizer.
     */
    protected Customizer<WebSplitPane> splitPaneCustomizer;

    /**
     * Whether documents can be closed or not.
     */
    protected boolean closable = true;

    /**
     * Whether documents drag enabled or not.
     */
    protected boolean dragEnabled = true;

    /**
     * Whether documents drag between tabbed panes is enabled or not.
     */
    protected boolean dragBetweenPanesEnabled = false;

    /**
     * Whether split creation is enabled or not.
     */
    protected boolean splitEnabled = true;

    /**
     * Whether tab menu is enabled or not.
     */
    protected boolean tabMenuEnabled = true;

    /**
     * Previously selected document.
     */
    protected WeakReference<T> previouslySelected = new WeakReference<T> ( null );

    /**
     * {@link Function} that resolves {@link DocumentData} for provided identifier.
     * It is used in to open documents by their identifiers instead of document references.
     * This {@link Function} is essential for proper state restoration functioning.
     */
    protected Function<String, T> documentsProvider = null;

    /**
     * Document drag view handler.
     */
    protected final DocumentDragViewHandler dragViewHandler;

    /**
     * Constructs new document pane.
     */
    public WebDocumentPane ()
    {
        this ( StyleId.documentpane, null, null, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param tabbedPaneCustomizer tabbed pane customizer
     */
    public WebDocumentPane ( final Customizer<WebTabbedPane> tabbedPaneCustomizer )
    {
        this ( StyleId.documentpane, null, tabbedPaneCustomizer, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        this ( StyleId.documentpane, null, null, tabTitleComponentProvider );
    }

    /**
     * Constructs new document pane.
     *
     * @param tabbedPaneCustomizer      tabbed pane customizer
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final Customizer<WebTabbedPane> tabbedPaneCustomizer,
                             final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        this ( StyleId.documentpane, null, tabbedPaneCustomizer, tabTitleComponentProvider );
    }

    /**
     * Constructs new document pane.
     *
     * @param splitPaneCustomizer  split pane customizer
     * @param tabbedPaneCustomizer tabbed pane customizer
     */
    public WebDocumentPane ( final Customizer<WebSplitPane> splitPaneCustomizer, final Customizer<WebTabbedPane> tabbedPaneCustomizer )
    {
        this ( StyleId.documentpane, splitPaneCustomizer, tabbedPaneCustomizer, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param splitPaneCustomizer       split pane customizer
     * @param tabbedPaneCustomizer      tabbed pane customizer
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final Customizer<WebSplitPane> splitPaneCustomizer, final Customizer<WebTabbedPane> tabbedPaneCustomizer,
                             final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        this ( StyleId.documentpane, splitPaneCustomizer, tabbedPaneCustomizer, tabTitleComponentProvider );
    }

    /**
     * Constructs new document pane.
     *
     * @param id style ID
     */
    public WebDocumentPane ( final StyleId id )
    {
        this ( id, null, null, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param id                   style ID
     * @param tabbedPaneCustomizer tabbed pane customizer
     */
    public WebDocumentPane ( final StyleId id, final Customizer<WebTabbedPane> tabbedPaneCustomizer )
    {
        this ( id, null, tabbedPaneCustomizer, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param id                        style ID
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final StyleId id, final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        this ( id, null, null, tabTitleComponentProvider );
    }

    /**
     * Constructs new document pane.
     *
     * @param id                        style ID
     * @param tabbedPaneCustomizer      tabbed pane customizer
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final StyleId id, final Customizer<WebTabbedPane> tabbedPaneCustomizer,
                             final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        this ( id, null, tabbedPaneCustomizer, tabTitleComponentProvider );
    }

    /**
     * Constructs new document pane.
     *
     * @param id                   style ID
     * @param splitPaneCustomizer  split pane customizer
     * @param tabbedPaneCustomizer tabbed pane customizer
     */
    public WebDocumentPane ( final StyleId id, final Customizer<WebSplitPane> splitPaneCustomizer,
                             final Customizer<WebTabbedPane> tabbedPaneCustomizer )
    {
        this ( id, splitPaneCustomizer, tabbedPaneCustomizer, null );
    }

    /**
     * Constructs new document pane.
     *
     * @param id                        style ID
     * @param splitPaneCustomizer       split pane customizer
     * @param tabbedPaneCustomizer      tabbed pane customizer
     * @param tabTitleComponentProvider tab title component customizer
     */
    public WebDocumentPane ( final StyleId id, final Customizer<WebSplitPane> splitPaneCustomizer,
                             final Customizer<WebTabbedPane> tabbedPaneCustomizer,
                             final TabTitleComponentProvider<T> tabTitleComponentProvider )
    {
        super ( id );

        // Customizers
        this.tabbedPaneCustomizer = tabbedPaneCustomizer;
        this.splitPaneCustomizer = splitPaneCustomizer;

        // Tab title component provider
        this.tabTitleComponentProvider =
                tabTitleComponentProvider != null ? tabTitleComponentProvider : createDefaultTabTitleComponentProvider ();

        // Generating unique document pane ID
        this.id = TextUtils.generateId ( "WDP" );

        // Registering drag view handler
        dragViewHandler = new DocumentDragViewHandler ( this );

        // Special behavior to keep view handler only while document pane is visible
        new VisibilityBehavior<WebDocumentPane<T>> ( this )
        {
            @Override
            protected void displayed ( @NotNull final WebDocumentPane<T> component )
            {
                DragManager.registerViewHandler ( dragViewHandler );
            }

            @Override
            protected void hidden ( @NotNull final WebDocumentPane<T> component )
            {
                DragManager.unregisterViewHandler ( dragViewHandler );
            }
        }.install ();

        // Adding initial pane
        initialize ();
    }

    /**
     * Returns default tab title component provider.
     *
     * @return default tab title component provider
     */
    protected TabTitleComponentProvider<T> createDefaultTabTitleComponentProvider ()
    {
        return new DefaultTabTitleComponentProvider<T> ();
    }

    /**
     * Returns unique document pane ID.
     * Might be used within D&amp;D functionality to determine whether drag source is the same as destination.
     *
     * @return unique document pane ID
     */
    public String getId ()
    {
        return id;
    }

    /**
     * Returns tabbed pane customizer.
     * It is null by default.
     *
     * @return tabbed pane customizer
     */
    public Customizer<WebTabbedPane> getTabbedPaneCustomizer ()
    {
        return tabbedPaneCustomizer;
    }

    /**
     * Sets tabbed pane customizer and applies it to existing panes.
     * Note that changes made by previously set customizers are not reverted even if you set this to null.
     *
     * @param customizer new tabbed pane customizer
     */
    public void setTabbedPaneCustomizer ( final Customizer<WebTabbedPane> customizer )
    {
        this.tabbedPaneCustomizer = customizer;
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            paneData.updateTabbedPaneCustomizer ( this );
        }
    }

    /**
     * Returns document tab title view customizer.
     *
     * @return document tab title view customizer
     */
    public TabTitleComponentProvider<T> getTabTitleComponentProvider ()
    {
        return tabTitleComponentProvider;
    }

    /**
     * Sets document tab title component provider.
     *
     * @param provider new document tab title component provider
     */
    public void setTabTitleComponentProvider ( final TabTitleComponentProvider<T> provider )
    {
        this.tabTitleComponentProvider = provider != null ? provider : createDefaultTabTitleComponentProvider ();
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            paneData.updateTabTitleComponents ();
        }
    }

    /**
     * Returns split pane customizer.
     * It is null by default.
     *
     * @return split pane customizer
     */
    public Customizer<WebSplitPane> getSplitPaneCustomizer ()
    {
        return splitPaneCustomizer;
    }

    /**
     * Sets split pane customizer and applies it to existing panes.
     * Note that changes made by previously set customizers are not reverted even if you set this to null.
     *
     * @param customizer new split pane customizer
     */
    public void setSplitPaneCustomizer ( final Customizer<WebSplitPane> customizer )
    {
        this.splitPaneCustomizer = customizer;
        for ( final SplitData<T> paneData : getAllSplitPanes () )
        {
            paneData.updateSplitPaneCustomizer ( this );
        }
    }

    /**
     * Returns whether tabs in this document pane are globally closable or not.
     *
     * @return true if tabs in this document pane are globally closable, false otherwise
     */
    public boolean isClosable ()
    {
        return closable;
    }

    /**
     * Sets whether tabs in this document pane should be globally closable or not.
     *
     * @param closable whether tabs in this document pane should be globally closable or not
     */
    public void setClosable ( final boolean closable )
    {
        this.closable = closable;
    }

    /**
     * Returns whether tabs drag is enabled or not.
     *
     * @return true if tabs drag is enabled, false otherwise
     */
    public boolean isDragEnabled ()
    {
        return dragEnabled;
    }

    /**
     * Sets whether tabs drag is enabled or not.
     *
     * @param dragEnabled whether tabs drag is enabled or not
     */
    public void setDragEnabled ( final boolean dragEnabled )
    {
        this.dragEnabled = dragEnabled;
    }

    /**
     * Returns whether tabs drag between different tabbed panes is enabled or not.
     *
     * @return true if tabs drag between different tabbed panes is enabled, false otherwise
     */
    public boolean isDragBetweenPanesEnabled ()
    {
        return dragBetweenPanesEnabled;
    }

    /**
     * Sets whether tabs drag between different tabbed panes is enabled or not.
     *
     * @param dragBetweenPanesEnabled whether tabs drag between different tabbed panes is enabled or not
     */
    public void setDragBetweenPanesEnabled ( final boolean dragBetweenPanesEnabled )
    {
        this.dragBetweenPanesEnabled = dragBetweenPanesEnabled;
    }

    /**
     * Returns whether split creation is enabled or not.
     *
     * @return true if split creation is enabled, false otherwise
     */
    public boolean isSplitEnabled ()
    {
        return splitEnabled;
    }

    /**
     * Sets whether split creation is enabled or not.
     *
     * @param splitEnabled true if split creation is enabled, false otherwise
     */
    public void setSplitEnabled ( final boolean splitEnabled )
    {
        this.splitEnabled = splitEnabled;
    }

    /**
     * Returns whether tab menu is enabled or not.
     *
     * @return true if tab menu is enabled, false otherwise
     */
    public boolean isTabMenuEnabled ()
    {
        return tabMenuEnabled;
    }

    /**
     * Sets whether tab menu is enabled or not.
     *
     * @param tabMenuEnabled whether tab menu is enabled or not
     */
    public void setTabMenuEnabled ( final boolean tabMenuEnabled )
    {
        this.tabMenuEnabled = tabMenuEnabled;
    }

    /**
     * Returns current root element data.
     * This is either SplitData or PaneData object.
     *
     * @return current root element data
     */
    public StructureData getStructureRoot ()
    {
        return root;
    }

    /**
     * Sets new root element data.
     * This call replaces all data stored in this document pane with new one.
     *
     * @param root new root element data
     */
    public void setStructureRoot ( final StructureData root )
    {
        // Clearing root component
        if ( this.root != null )
        {
            remove ( this.root.getComponent () );
        }

        // Initializing new root
        if ( root != null )
        {
            // Adding root component
            add ( root.getComponent (), BorderLayout.CENTER );

            // Changing root
            this.root = root;
            this.activePane = root.findClosestPane ();

            // Updating document pane view
            revalidate ();
            repaint ();
        }
        else
        {
            // Add initial pane
            initialize ();
        }
    }

    /**
     * Initializes root and active pane.
     */
    protected void initialize ()
    {
        // Creating data for root pane
        final PaneData rootPane = new PaneData<T> ( this );

        // Adding root pane
        add ( rootPane.getTabbedPane (), BorderLayout.CENTER );

        // Applying initial values
        root = rootPane;
        activePane = rootPane;
    }

    /**
     * Splits document's pane into two panes using the specified direction to decide split settings.
     *
     * @param movedDocument document that should be moved to new pane
     * @param direction     split direction
     */
    public void split ( final T movedDocument, final int direction )
    {
        final PaneData<T> pane = getPane ( movedDocument );
        if ( pane != null )
        {
            split ( pane, movedDocument, direction );
        }
    }

    /**
     * Splits specified pane into two panes using the specified direction to decide split settings.
     *
     * @param splittedPane  pane that will be splitted
     * @param movedDocument document that should be moved from splitted pane to new one
     * @param direction     split direction
     * @return second pane created in the split process
     */
    protected PaneData<T> split ( final PaneData<T> splittedPane, final T movedDocument, final int direction )
    {
        final PaneData<T> otherPane;
        if ( splittedPane != null )
        {
            // Choosing course of action depending on splitted pane parent
            final boolean ltr = direction == SwingConstants.RIGHT || direction == SwingConstants.BOTTOM;
            final int orientation = direction == SwingConstants.LEFT || direction == SwingConstants.RIGHT ?
                    SwingConstants.VERTICAL : SwingConstants.HORIZONTAL;
            final SplitData<T> splitData;
            if ( splittedPane.getTabbedPane ().getParent () == WebDocumentPane.this )
            {
                // Creating data for new pane
                otherPane = new PaneData<T> ( this );

                // Saving sizes to restore split locations
                final Dimension size = splittedPane.getTabbedPane ().getSize ();

                // Adding root split
                final PaneData<T> first = ltr ? splittedPane : otherPane;
                final PaneData<T> last = ltr ? otherPane : splittedPane;
                splitData = new SplitData<T> ( WebDocumentPane.this, orientation, first, last );
                remove ( splittedPane.getTabbedPane () );
                add ( splitData.getSplitPane (), BorderLayout.CENTER );

                // Restoring split locations
                splitData.getSplitPane ().setDividerLocation ( orientation == SwingConstants.VERTICAL ? size.width / 2 : size.height / 2 );

                // Changing root
                root = splitData;
            }
            else
            {
                // Determining parent split
                final WebSplitPane parentSplit = ( WebSplitPane ) splittedPane.getTabbedPane ().getParent ();
                final SplitData<T> parentSplitData = getData ( parentSplit );
                if ( parentSplitData.getOrientation () == orientation && ltr && parentSplitData.getFirst () == splittedPane &&
                        parentSplitData.getLast () instanceof PaneData )
                {
                    // Using existing split and pane
                    splitData = parentSplitData;
                    otherPane = ( PaneData<T> ) parentSplitData.getLast ();
                }
                else if ( parentSplitData.getOrientation () == orientation && !ltr && parentSplitData.getLast () == splittedPane &&
                        parentSplitData.getFirst () instanceof PaneData )
                {
                    // Using existing split and pane
                    splitData = parentSplitData;
                    otherPane = ( PaneData<T> ) parentSplitData.getFirst ();
                }
                else
                {
                    // Creating data for new pane
                    otherPane = new PaneData<T> ( this );

                    // Saving sizes to restore split locations
                    final int parentSplitLocation = parentSplitData.getSplitPane ().getDividerLocation ();
                    final Dimension size = splittedPane.getTabbedPane ().getSize ();

                    // Adding inner split
                    final PaneData<T> first = ltr ? splittedPane : otherPane;
                    final PaneData<T> last = ltr ? otherPane : splittedPane;
                    splitData = new SplitData<T> ( WebDocumentPane.this, orientation, first, last );
                    parentSplitData.replace ( splittedPane, splitData );

                    // Restoring split locations
                    final int location = orientation == SwingConstants.VERTICAL ? size.width / 2 : size.height / 2;
                    splitData.getSplitPane ().setDividerLocation ( location );
                    parentSplitData.getSplitPane ().setDividerLocation ( parentSplitLocation );
                }
            }

            // Moving document to new pane if it is specified
            if ( movedDocument != null )
            {
                splittedPane.remove ( movedDocument );
                otherPane.add ( movedDocument );
            }

            // Updating document pane view
            revalidate ();
            repaint ();

            // Activating other split
            otherPane.getTabbedPane ().requestFocusInWindow ();
            otherPane.activate ();

            // Firing splitted event
            fireSplitted ( splittedPane, splitData );
        }
        else
        {
            // Its not possible to split unspecified pane
            otherPane = null;
        }
        return otherPane;
    }

    /**
     * Merges specified structure element and its sub-elements if it is possible.
     * If PaneData provided its parent split will be merged.
     * If SplitData provided it will be merged.
     *
     * @param toMerge structure element to merge
     */
    public void merge ( final StructureData toMerge )
    {
        // Retrieving split data that should be merged
        if ( toMerge instanceof PaneData )
        {
            // When pane is forced to merge with opposite
            final PaneData<T> mergedPane = ( PaneData<T> ) toMerge;
            final Container parent = mergedPane.getTabbedPane ().getParent ();

            // Merge only if actually inside of a split
            // Otherwise this is a root pane which can't be merged
            if ( parent instanceof WebSplitPane )
            {
                final WebSplitPane splitPane = ( WebSplitPane ) parent;
                mergeImpl ( ( SplitData<T> ) getData ( splitPane ) );

                // Updating document pane view
                revalidate ();
                repaint ();
            }
        }
        else
        {
            // When split is forced to merge into single pane
            mergeImpl ( ( SplitData<T> ) toMerge );

            // Updating document pane view
            revalidate ();
            repaint ();
        }

        // Checking selection state
        checkSelection ();
    }

    /**
     * Merges specified split element and its sub-elements if it is possible.
     *
     * @param splitData split element to merge
     */
    protected void mergeImpl ( final SplitData<T> splitData )
    {
        StructureData first = splitData.getFirst ();
        StructureData last = splitData.getLast ();

        // Determining the resulting element
        final StructureData<T> result;
        if ( isEmptyPane ( first ) || isEmptyPane ( last ) )
        {
            result = isEmptyPane ( first ) ? last : first;
        }
        else
        {
            // Merge inner content first so we have split with tabs only inside
            if ( first instanceof SplitData )
            {
                mergeImpl ( ( SplitData<T> ) first );
                first = splitData.getFirst ();
            }
            if ( last instanceof SplitData )
            {
                mergeImpl ( ( SplitData<T> ) last );
                last = splitData.getLast ();
            }

            // Moving all documents from second pane to first
            final PaneData<T> firstPane = ( PaneData<T> ) first;
            final PaneData<T> lastPane = ( PaneData<T> ) last;
            final PaneData<T> toPane = firstPane.count () > lastPane.count () ? firstPane : lastPane;
            final PaneData<T> fromPane = firstPane.count () > lastPane.count () ? lastPane : firstPane;
            for ( final T document : CollectionUtils.copy ( fromPane.getData () ) )
            {
                fromPane.remove ( document );
                toPane.add ( document );
            }
            result = toPane;
        }

        // Update active pane
        if ( activePane == first || activePane == last )
        {
            activePane = result.findClosestPane ();
        }

        // Removing merged split
        final WebSplitPane splitPane = splitData.getSplitPane ();
        if ( splitPane.getParent () == WebDocumentPane.this )
        {
            // Removing root split and adding tab pane
            remove ( splitPane );
            add ( result.getComponent (), BorderLayout.CENTER );

            // Changing root
            root = result;
        }
        else
        {
            // Retrieving parent split
            final WebSplitPane parentSplit = ( WebSplitPane ) splitPane.getParent ();
            final SplitData<T> parentSplitData = getData ( parentSplit );
            final int dividerLocation = parentSplit.getDividerLocation ();

            // Changing parent split component
            if ( parentSplit.getLeftComponent () == splitPane )
            {
                parentSplitData.setFirst ( result );
            }
            else
            {
                parentSplitData.setLast ( result );
            }

            // Restoring divider location
            parentSplit.setDividerLocation ( dividerLocation );
        }

        // Firing merged event
        fireMerged ( splitData, result );
    }

    /**
     * Returns currently active pane data.
     * This is the last pane that had focus within this document pane.
     *
     * @return currently active pane data
     */
    public PaneData<T> getActivePane ()
    {
        return activePane;
    }

    /**
     * Sets active pane.
     *
     * @param paneData new active pane
     */
    protected void activate ( final PaneData<T> paneData )
    {
        if ( paneData != null && paneData != activePane )
        {
            activePane = paneData;
            checkSelection ();
        }
    }

    /**
     * Sets active pane.
     *
     * @param document document to activate
     */
    protected void activate ( final T document )
    {
        activate ( getPane ( document ) );
        setSelected ( document );
    }

    /**
     * Returns selected document data.
     *
     * @return selected document data
     */
    public T getSelectedDocument ()
    {
        return activePane != null ? activePane.getSelected () : null;
    }

    /**
     * Returns document at the specified tab index of the active pane.
     *
     * @param index active pane tab index
     * @return document at the specified tab index of the active pane
     */
    public T getDocument ( final int index )
    {
        return activePane != null ? activePane.get ( index ) : null;
    }

    /**
     * Returns document with the specified ID or null if it is not inside this document pane.
     *
     * @param id document ID
     * @return document with the specified ID or null if it is not inside this document pane
     */
    public T getDocument ( final String id )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            final T document = paneData.get ( id );
            if ( document != null )
            {
                return document;
            }
        }
        return null;
    }

    /**
     * Returns all documents opened in this document pane.
     *
     * @return all documents opened in this document pane
     */
    public List<T> getDocuments ()
    {
        final List<T> documents = new ArrayList<T> ();
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            documents.addAll ( paneData.getData () );
        }
        return documents;
    }

    /**
     * Returns amount of documents opened in this document pane.
     *
     * @return amount of documents opened in this document pane
     */
    public int getDocumentsCount ()
    {
        int count = 0;
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            count += paneData.count ();
        }
        return count;
    }

    /**
     * Returns list of all available panes within this document pane.
     *
     * @return list of all available panes within this document pane
     */
    public List<PaneData<T>> getAllPanes ()
    {
        final List<PaneData<T>> panes = new ArrayList<PaneData<T>> ();
        collectPanes ( root, panes );
        return panes;
    }

    /**
     * Collects all PaneData available under the specified structure element into list.
     *
     * @param structureData structure element
     * @param panes         PaneData list
     */
    protected void collectPanes ( final StructureData structureData, final List<PaneData<T>> panes )
    {
        if ( structureData instanceof PaneData )
        {
            panes.add ( ( PaneData<T> ) structureData );
        }
        else if ( structureData instanceof SplitData )
        {
            final SplitData<T> splitData = ( SplitData<T> ) structureData;
            collectPanes ( splitData.getFirst (), panes );
            collectPanes ( splitData.getLast (), panes );
        }
        else
        {
            throw new RuntimeException ( "Unknown structure data type: " + structureData.getClass () );
        }
    }

    /**
     * Returns list of all available split panes within this document pane.
     *
     * @return list of all available split panes within this document pane
     */
    public List<SplitData<T>> getAllSplitPanes ()
    {
        final List<SplitData<T>> panes = new ArrayList<SplitData<T>> ();
        collectSplitPanes ( root, panes );
        return panes;
    }

    /**
     * Collects all SplitData available under the specified structure element into list.
     *
     * @param structureData structure element
     * @param splits        SplitData list
     */
    protected void collectSplitPanes ( final StructureData structureData, final List<SplitData<T>> splits )
    {
        if ( structureData instanceof SplitData )
        {
            final SplitData<T> splitData = ( SplitData<T> ) structureData;
            splits.add ( splitData );
            collectSplitPanes ( splitData.getFirst (), splits );
            collectSplitPanes ( splitData.getLast (), splits );
        }
    }

    /**
     * Returns pane that contains specified document.
     *
     * @param document document to look for
     * @return pane that contains specified document
     */
    public PaneData<T> getPane ( final T document )
    {
        return document != null ? getPane ( document.getId () ) : null;
    }

    /**
     * Returns pane that contains document with the specified ID.
     *
     * @param documentId ID of the document to look for
     * @return pane that contains document with the specified ID
     */
    public PaneData<T> getPane ( final String documentId )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            if ( paneData.contains ( documentId ) )
            {
                return paneData;
            }
        }
        return null;
    }

    /**
     * Sets selected document index inside the active pane.
     *
     * @param index index of the document to select
     */
    public void setSelected ( final int index )
    {
        if ( activePane != null )
        {
            activePane.setSelected ( index );
        }
    }

    /**
     * Decrements selected document index inside the active pane.
     */
    public void selectPrevious ()
    {
        if ( activePane != null )
        {
            activePane.selectPrevious ();
        }
    }

    /**
     * Increments selected document index inside the active pane.
     */
    public void selectNext ()
    {
        if ( activePane != null )
        {
            activePane.selectNext ();
        }
    }

    /**
     * Sets document selected inside its pane.
     *
     * @param document document to select
     */
    public void setSelected ( final DocumentData document )
    {
        setSelected ( document.getId () );
    }

    /**
     * Sets document with the specified ID selected inside its pane.
     *
     * @param id ID of the document to select
     */
    public void setSelected ( final String id )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            final T document = paneData.get ( id );
            if ( document != null )
            {
                paneData.setSelected ( document );
                paneData.activate ();
            }
        }
    }

    /**
     * Returns whether specified document is opened inside this document pane or not.
     *
     * @param document document to look for
     * @return true if specified document is opened inside this document pane, false otherwise
     */
    public boolean isDocumentOpened ( final T document )
    {
        return isDocumentOpened ( document.getId () );
    }

    /**
     * Returns whether document with the specified ID is opened inside this document pane or not.
     *
     * @param documentId ID of the document to look for
     * @return true if document with the specified ID is opened inside this document pane, false otherwise
     */
    public boolean isDocumentOpened ( final String documentId )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            if ( paneData.contains ( documentId ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens document with the specified ID in this document pane.
     * This method won't work in case you didn't set custom DocumentDataProvider.
     *
     * @param documentId ID of the document to open
     */
    public void openDocument ( final String documentId )
    {
        openDocument ( documentId, true );
    }

    /**
     * Opens document with the specified ID in this document pane.
     * This method won't work in case you didn't set custom DocumentDataProvider.
     *
     * @param documentId ID of the document to open
     * @param select     whether or not document tab should be selected
     */
    public void openDocument ( final String documentId, final boolean select )
    {
        if ( documentsProvider != null )
        {
            openDocument ( documentsProvider.apply ( documentId ), select );
        }
    }

    /**
     * Opens document in this document pane.
     *
     * @param document document to open
     */
    public void openDocument ( final T document )
    {
        openDocument ( document, true );
    }

    /**
     * Opens document in this document pane.
     *
     * @param document document to open
     * @param select   whether or not document tab should be selected
     */
    public void openDocument ( final T document, final boolean select )
    {
        if ( document != null )
        {
            if ( isDocumentOpened ( document ) )
            {
                if ( select )
                {
                    setSelected ( document );
                }
            }
            else if ( activePane != null )
            {
                activePane.open ( document );
                if ( select )
                {
                    activePane.setSelected ( document );
                }
            }
            else
            {
                throw new NullPointerException ( "Something went wrong, active pane is not available" );
            }
        }
    }

    /**
     * Closes document at the specified index in the active pane.
     *
     * @param index index of the document to close
     * @return {@code true} if document was successfully closed, {@code false} otherwise
     */
    public boolean closeDocument ( final int index )
    {
        return activePane != null && activePane.close ( index );
    }

    /**
     * Closes document with the specified ID.
     *
     * @param id ID of the document to close
     * @return {@code true} if document was successfully closed, {@code false} otherwise
     */
    public boolean closeDocument ( final String id )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            if ( paneData.close ( id ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Closes the specified document.
     *
     * @param document document to close
     * @return {@code true} if document was successfully closed, {@code false} otherwise
     */
    public boolean closeDocument ( final T document )
    {
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            if ( paneData.close ( document ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Closes all documents.
     *
     * @return {@code true} if all documents were successfully closed, {@code false} otherwise
     */
    public boolean closeAll ()
    {
        boolean success = true;
        for ( final PaneData<T> paneData : getAllPanes () )
        {
            success &= paneData.closeAll ();
        }
        return success;
    }

    /**
     * Checks selection state and fires selection event if required.
     * This method is separated from actual selection due to various difficulties implementing it on-place.
     * Selection is checked on pane activation, documents tab changes.
     */
    protected void checkSelection ()
    {
        final T selected = getSelectedDocument ();
        if ( previouslySelected.get () != selected )
        {
            // Updating reference
            previouslySelected = new WeakReference<T> ( selected );

            // Firing event only when something was actually selected
            if ( selected != null )
            {
                final PaneData<T> pane = getPane ( selected );
                fireDocumentSelected ( selected, pane, pane.indexOf ( selected ) );
            }
        }
    }

    /**
     * Returns {@link Function} that resolves {@link DocumentData} for provided identifier.
     * It is used in to open documents by their identifiers instead of document references.
     * This {@link Function} is essential for proper state restoration functioning.
     *
     * @return {@link Function} that resolves {@link DocumentData} for provided identifier
     */
    public Function<String, T> getDocumentsProvider ()
    {
        return documentsProvider;
    }

    /**
     * Sets {@link Function} that resolves {@link DocumentData} for provided identifier.
     * It is used in to open documents by their identifiers instead of document references.
     * This {@link Function} is essential for proper state restoration functioning.
     *
     * @param provider new {@link Function} for resolving {@link DocumentData} by provided identifier
     */
    public void setDocumentsProvider ( final Function<String, T> provider )
    {
        this.documentsProvider = provider;
    }

    /**
     * Returns current {@link DocumentPaneState} for this {@link WebDocumentPane}.
     * This {@link DocumentPaneState} contains opened document IDs references and structure composition.
     * It can be used to save and restore {@link WebDocumentPane} structure and opened documents.
     *
     * @return current {@link DocumentPaneState} for this {@link WebDocumentPane}
     * @see DocumentPaneState
     * @see #setDocumentPaneState(DocumentPaneState)
     */
    public DocumentPaneState getDocumentPaneState ()
    {
        return root != null ? root.getDocumentPaneState () : null;
    }

    /**
     * Updates document pane state.
     * This will remove all added documents and reopen them according to restored state.
     * Make sure that you add all required documents before loading restoring the state.
     * Otherwise in some cases you might get unwanted effect like lost structure parts.
     * <p>
     * Also be aware that this call might generate some unwanted events like documents close and such.
     * So make sure to add your listeners after this call in case you don't want to listen to initial state restore events.
     *
     * @param state document pane state to restore
     * @see DocumentPaneState
     * @see #getDocumentPaneState()
     */
    public void setDocumentPaneState ( final DocumentPaneState state )
    {
        // Mapping all opened documents
        // In case some are missing they can be filled-in later using document provider
        // It doesn't really matter where these documents come from but we assume that they might already be opened
        final List<T> openedDocuments = getDocuments ();
        final Map<String, T> documents = new HashMap<String, T> ( openedDocuments.size () );
        for ( final T document : openedDocuments )
        {
            documents.put ( document.getId (), document );
        }

        // Temporarily removing all documents to place them properly after
        closeAll ();

        // Restoring document pane state
        setStructureRoot ( restoreStructureStateImpl ( state, documents ) );
    }

    /**
     * Restores {@link StructureData} restored from provided {@link DocumentPaneState}.
     *
     * @param state     document pane state to restore
     * @param documents existing documents
     * @return {@link StructureData} restored from provided {@link DocumentPaneState}
     */
    protected StructureData<T> restoreStructureStateImpl ( final DocumentPaneState state, final Map<String, T> documents )
    {
        final StructureData<T> restored;
        if ( state.isSplit () )
        {
            // Restoring split data
            final Pair<DocumentPaneState, DocumentPaneState> splitState = state.getSplitState ();
            final StructureData<T> first = restoreStructureStateImpl ( splitState.getKey (), documents );
            final StructureData<T> last = restoreStructureStateImpl ( splitState.getValue (), documents );
            final SplitData<T> splitData = new SplitData<T> ( this, state.getSplitOrientation (), first, last );

            // todo Location updates should be queued and performed 1 by 1 from top to bottom to avoid incorrect layout
            // We have to wait until split is properly sized before restoring divider location
            splitData.getSplitPane ().addComponentListener ( new ComponentAdapter ()
            {
                @Override
                public void componentResized ( final ComponentEvent e )
                {
                    // Waiting until split is properly sized
                    final WebSplitPane splitPane = splitData.getSplitPane ();
                    if ( splitPane.getWidth () > 0 || splitPane.getHeight () > 0 )
                    {
                        // Updating proportional divider location
                        splitData.setDividerLocation ( state.getDividerLocation () );
                        splitPane.removeComponentListener ( this );
                    }
                }
            } );

            restored = splitData;
        }
        else
        {
            // Restoring pane data
            final PaneData<T> paneData = new PaneData<T> ( this );
            if ( state.getDocumentIds () != null )
            {
                for ( final String id : state.getDocumentIds () )
                {
                    // In case document doesn't exist, try requesting it from provider if we have one
                    if ( documentsProvider != null && !documents.containsKey ( id ) )
                    {
                        documents.put ( id, documentsProvider.apply ( id ) );
                    }

                    // Simply open document if it exists
                    if ( documents.containsKey ( id ) )
                    {
                        final T document = documents.get ( id );
                        if ( document != null )
                        {
                            paneData.open ( document );
                        }
                    }
                }
            }

            // Update selected document
            final String selectedId = state.getSelectedId ();
            if ( selectedId != null && documents.containsKey ( selectedId ) )
            {
                paneData.setSelected ( documents.get ( selectedId ) );
            }

            restored = paneData;
        }
        return restored;
    }

    /**
     * Adds document pane listener.
     *
     * @param listener new document pane listener
     */
    public void addDocumentPaneListener ( final DocumentPaneListener<T> listener )
    {
        listenerList.add ( DocumentPaneListener.class, listener );
    }

    /**
     * Removes document pane listener.
     *
     * @param listener document pane listener
     */
    public void removeDocumentPaneListener ( final DocumentPaneListener<T> listener )
    {
        listenerList.remove ( DocumentPaneListener.class, listener );
    }

    /**
     * Fires PaneData split event.
     *
     * @param splittedPane splitted PaneData
     * @param newSplitData newly created SplitData
     */
    public void fireSplitted ( final PaneData<T> splittedPane, final SplitData<T> newSplitData )
    {
        for ( final DocumentPaneListener<T> listener : listenerList.getListeners ( DocumentPaneListener.class ) )
        {
            listener.splitted ( this, splittedPane, newSplitData );
        }
    }

    /**
     * Fires SplitData merge event.
     *
     * @param mergedSplit      merged SplitData
     * @param newStructureData newly created StructureData
     */
    public void fireMerged ( final SplitData<T> mergedSplit, final StructureData<T> newStructureData )
    {
        for ( final DocumentPaneListener<T> listener : listenerList.getListeners ( DocumentPaneListener.class ) )
        {
            listener.merged ( this, mergedSplit, newStructureData );
        }
    }

    /**
     * Fires SplitData orientation change event.
     *
     * @param splitData SplitData which orientation has changed
     */
    public void fireOrientationChanged ( final SplitData<T> splitData )
    {
        for ( final DocumentPaneListener<T> listener : listenerList.getListeners ( DocumentPaneListener.class ) )
        {
            listener.orientationChanged ( this, splitData );
        }
    }

    /**
     * Fires SplitData sides swap event.
     *
     * @param splitData SplitData which sides were swapped
     */
    public void fireSidesSwapped ( final SplitData<T> splitData )
    {
        for ( final DocumentPaneListener<T> listener : listenerList.getListeners ( DocumentPaneListener.class ) )
        {
            listener.sidesSwapped ( this, splitData );
        }
    }

    /**
     * Fires SplitData divider location change event.
     *
     * @param splitData SplitData which divider location has changed
     */
    public void fireDividerLocationChanged ( final SplitData<T> splitData )
    {
        for ( final DocumentPaneListener<T> listener : listenerList.getListeners ( DocumentPaneListener.class ) )
        {
            listener.dividerLocationChanged ( this, splitData );
        }
    }

    /**
     * Adds document listener.
     *
     * @param listener new document listener
     */
    public void addDocumentListener ( final DocumentListener<T> listener )
    {
        listenerList.add ( DocumentListener.class, listener );
    }

    /**
     * Removes document listener.
     *
     * @param listener document listener
     */
    public void removeDocumentListener ( final DocumentListener<T> listener )
    {
        listenerList.remove ( DocumentListener.class, listener );
    }

    /**
     * Fires document opened event.
     *
     * @param document opened document
     * @param pane     document's pane
     * @param index    document's index
     */
    public void fireDocumentOpened ( final T document, final PaneData<T> pane, final int index )
    {
        for ( final DocumentListener<T> listener : listenerList.getListeners ( DocumentListener.class ) )
        {
            listener.opened ( document, pane, index );
        }
    }

    /**
     * Fires document selected event.
     *
     * @param document selected document
     * @param pane     document's pane
     * @param index    document's index
     */
    public void fireDocumentSelected ( final T document, final PaneData<T> pane, final int index )
    {
        for ( final DocumentListener<T> listener : listenerList.getListeners ( DocumentListener.class ) )
        {
            listener.selected ( document, pane, index );
        }
    }

    /**
     * Fires document closing event.
     * Returns whether document is allowed to close or not.
     *
     * @param document closing document
     * @param pane     document's pane
     * @param index    document's index
     * @return true if document is allowed to close, false otherwise
     */
    public boolean fireDocumentClosing ( final T document, final PaneData<T> pane, final int index )
    {
        boolean allow = true;
        for ( final DocumentListener<T> listener : listenerList.getListeners ( DocumentListener.class ) )
        {
            allow = allow && listener.closing ( document, pane, index );
        }
        return allow;
    }

    /**
     * Fires document closed event.
     *
     * @param document closed document
     * @param pane     document's pane
     * @param index    document's index
     */
    public void fireDocumentClosed ( final T document, final PaneData<T> pane, final int index )
    {
        for ( final DocumentListener<T> listener : listenerList.getListeners ( DocumentListener.class ) )
        {
            listener.closed ( document, pane, index );
        }
    }

    @Override
    public DocumentListener<T> onDocumentOpen ( final DocumentDataRunnable<T> runnable )
    {
        return DocumentPaneEventMethodsImpl.onDocumentOpen ( this, runnable );
    }

    @Override
    public DocumentListener<T> onDocumentSelection ( final DocumentDataRunnable<T> runnable )
    {
        return DocumentPaneEventMethodsImpl.onDocumentSelection ( this, runnable );
    }

    @Override
    public DocumentListener<T> onDocumentClosing ( final DocumentDataCancellableRunnable<T> runnable )
    {
        return DocumentPaneEventMethodsImpl.onDocumentClosing ( this, runnable );
    }

    @Override
    public DocumentListener<T> onDocumentClose ( final DocumentDataRunnable<T> runnable )
    {
        return DocumentPaneEventMethodsImpl.onDocumentClose ( this, runnable );
    }

    @Override
    public void registerSettings ( final Configuration configuration )
    {
        UISettingsManager.registerComponent ( this, configuration );
    }

    @Override
    public void registerSettings ( final SettingsProcessor processor )
    {
        UISettingsManager.registerComponent ( this, processor );
    }

    @Override
    public void unregisterSettings ()
    {
        UISettingsManager.unregisterComponent ( this );
    }

    @Override
    public void loadSettings ()
    {
        UISettingsManager.loadSettings ( this );
    }

    @Override
    public void saveSettings ()
    {
        UISettingsManager.saveSettings ( this );
    }

    /**
     * Returns pane data stored inside the tabbed pane component.
     *
     * @param tabbedPane tabbed pane component
     * @param <T>        document type
     * @return pane data stored inside the tabbed pane component
     */
    public static <T extends DocumentData> PaneData<T> getData ( final WebTabbedPane tabbedPane )
    {
        return ( PaneData<T> ) tabbedPane.getClientProperty ( DATA_KEY );
    }

    /**
     * Returns split data stored inside the split pane component.
     *
     * @param splitPane split pane component
     * @param <T>       document type
     * @return split data stored inside the split pane component
     */
    public static <T extends DocumentData> SplitData<T> getData ( final WebSplitPane splitPane )
    {
        return ( SplitData<T> ) splitPane.getClientProperty ( DATA_KEY );
    }

    /**
     * Returns whether the specified element is an empty pane or not.
     *
     * @param data structure element to check
     * @return true if the specified element is an empty pane, false otherwise
     */
    public static boolean isEmptyPane ( final StructureData data )
    {
        return data instanceof PaneData && ( ( PaneData ) data ).count () == 0;
    }
}