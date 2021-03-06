package com.alee.laf.grouping;

import com.alee.api.annotations.NotNull;
import com.alee.api.annotations.Nullable;
import com.alee.api.data.BoxOrientation;
import com.alee.painter.PainterSupport;
import com.alee.painter.decoration.DecorationUtils;
import com.alee.utils.general.Pair;
import com.alee.utils.swing.SizeType;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Layout designed specifically for usage within {@link com.alee.laf.grouping.GroupPane} container.
 * It constructs a grid of components to be visually grouped and knows how to retrieve components at specific cells.
 *
 * @author Mikle Garin
 */
@XStreamAlias ( "GroupPaneLayout" )
public class GroupPaneLayout extends AbstractGroupingLayout implements SwingConstants
{
    /**
     * Components placement order orientation.
     */
    @XStreamAsAttribute
    protected int orientation;

    /**
     * Amount of columns used to place components.
     */
    @XStreamAsAttribute
    protected int columns;

    /**
     * Amount of rows used to place components.
     */
    @XStreamAsAttribute
    protected int rows;

    /**
     * Component constraints.
     */
    protected transient final Map<Component, GroupPaneConstraints> constraints = new HashMap<Component, GroupPaneConstraints> ( 5 );

    /**
     * Constructs default layout.
     */
    public GroupPaneLayout ()
    {
        this ( HORIZONTAL, Integer.MAX_VALUE, 1 );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param orientation components placement order orientation
     */
    public GroupPaneLayout ( final int orientation )
    {
        this ( orientation, Integer.MAX_VALUE, 1 );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param columns amount of columns used to place components
     * @param rows    amount of rows used to place components
     */
    public GroupPaneLayout ( final int columns, final int rows )
    {
        this ( HORIZONTAL, columns, rows );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param orientation components placement order orientation
     * @param columns     amount of columns used to place components
     * @param rows        amount of rows used to place components
     */
    public GroupPaneLayout ( final int orientation, final int columns, final int rows )
    {
        super ();
        setOrientation ( orientation );
        setColumns ( columns );
        setRows ( rows );
    }

    /**
     * Returns components placement order orientation.
     *
     * @return components placement order orientation
     */
    public int getOrientation ()
    {
        return orientation;
    }

    /**
     * Sets components placement order orientation.
     *
     * @param orientation components placement order orientation
     */
    public void setOrientation ( final int orientation )
    {
        this.orientation = orientation;
    }

    /**
     * Returns amount of columns used to place components.
     *
     * @return amount of columns used to place components
     */
    public int getColumns ()
    {
        return columns;
    }

    /**
     * Sets amount of columns used to place components.
     *
     * @param columns amount of columns to place components
     */
    public void setColumns ( final int columns )
    {
        this.columns = columns;
    }

    /**
     * Returns amount of rows used to place components.
     *
     * @return amount of rows used to place components
     */
    public int getRows ()
    {
        return rows;
    }

    /**
     * Sets amount of rows used to place components.
     *
     * @param rows amount of rows to place components
     */
    public void setRows ( final int rows )
    {
        this.rows = rows;
    }

    @Override
    public void addComponent ( @NotNull final Component component, @Nullable final Object constraints )
    {
        // Saving constraints
        if ( constraints != null && !( constraints instanceof GroupPaneConstraints ) )
        {
            throw new RuntimeException ( "Unsupported layout constraints: " + constraints );
        }
        this.constraints.put ( component, constraints != null ? ( GroupPaneConstraints ) constraints : getDefaultConstraint () );

        // Performing basic operations
        super.addComponent ( component, constraints );
    }

    /**
     * Returns default component constraints in this layout.
     *
     * @return default component constraints in this layout
     */
    protected GroupPaneConstraints getDefaultConstraint ()
    {
        return orientation == HORIZONTAL ? GroupPaneConstraints.VERTICAL_FILL : GroupPaneConstraints.HORIZONTAL_FILL;
    }

    @Override
    public void removeComponent ( @NotNull final Component component )
    {
        // Performing basic operations
        super.removeComponent ( component );

        // Removing saved constraints
        constraints.remove ( component );
    }

    @Override
    public void layoutContainer ( @NotNull final Container container )
    {
        // Retrieving actual grid size
        final GridSize gridSize = getActualGridSize ( container );

        // Calculating children preferred sizes
        final Pair<int[], int[]> sizes = calculateSizes ( container, gridSize, SizeType.current );

        // Laying out components
        // To do that we will simply iterate through the whole grid
        // Some cells we will iterate through won't have components, we will simply skip those
        final Insets border = container.getInsets ();
        int y = border.top;
        for ( int row = 0; row < gridSize.rows; row++ )
        {
            int x = border.left;
            for ( int column = 0; column < gridSize.columns; column++ )
            {
                // Converting grid point to component index
                final int index = pointToIndex ( container, column, row, gridSize );

                // Retrieving cell component if it exists
                final Component component = container.getComponent ( index );
                if ( component != null )
                {
                    // Updating its bounds
                    component.setBounds ( x, y, sizes.key[ column ], sizes.value[ row ] );
                }

                // Move forward into grid
                x += sizes.key[ column ];
            }

            // Move forward into grid
            y += sizes.value[ row ];
        }
    }

    @NotNull
    @Override
    public Dimension preferredLayoutSize ( @NotNull final Container container )
    {
        // Retrieving actual grid size
        final GridSize gridSize = getActualGridSize ( container );

        // Calculating children preferred sizes
        final Pair<int[], int[]> sizes = calculateSizes ( container, gridSize, SizeType.preferred );

        // Calculating preferred size
        final Dimension ps = new Dimension ( 0, 0 );
        for ( final Integer columnWith : sizes.key )
        {
            ps.width += columnWith;
        }
        for ( final Integer rowHeight : sizes.value )
        {
            ps.height += rowHeight;
        }
        final Insets border = container.getInsets ();
        ps.width += border.left + border.right;
        ps.height += border.top + border.bottom;

        return ps;
    }

    /**
     * Returns actual grid size according to container components amount.
     * Actual grid size is very important for all calculations as it defines the final size of the grid.
     *
     * For example: Layout settings are set to have 5 columns and 5 rows which in total requires 25 components to fill-in the grid.
     * Though there might not be enough components provided to fill the grid, in that case the actual grid size might be less.
     *
     * @param container group pane
     * @return actual grid size according to container components amount
     */
    protected GridSize getActualGridSize ( final Container container )
    {
        final int count = container.getComponentCount ();
        if ( orientation == HORIZONTAL )
        {
            return new GridSize ( Math.min ( count, columns ), ( count - 1 ) / columns + 1 );
        }
        else
        {
            return new GridSize ( ( count - 1 ) / rows + 1, Math.min ( count, rows ) );
        }
    }

    /**
     * Returns component at the specified cell.
     *
     * @param container group pane
     * @param column    component column
     * @param row       component row
     * @return component at the specified cell
     */
    protected Component getComponentAt ( final Container container, final int column, final int row )
    {
        final GridSize gridSize = getActualGridSize ( container );
        final int index = pointToIndex ( container, column, row, gridSize );
        final int count = container.getComponentCount ();
        return index < count ? container.getComponent ( index ) : null;
    }

    /**
     * Returns grid column in which component under the specified index is placed.
     *
     * @param container group pane
     * @param index     component index
     * @param gridSize  actual grid size
     * @return grid column in which component under the specified index is placed
     */
    protected int indexToColumn ( final Container container, final int index, final GridSize gridSize )
    {
        final boolean ltr = container.getComponentOrientation ().isLeftToRight ();
        final int column = orientation == HORIZONTAL ? index % columns : index / rows;
        return ltr ? column : gridSize.columns - 1 - column;
    }

    /**
     * Returns grid row in which component under the specified index is placed.
     *
     * @param index component index
     * @return grid row in which component under the specified index is placed
     */
    protected int indexToRow ( final int index )
    {
        return orientation == HORIZONTAL ? index / columns : index % rows;
    }

    /**
     * Returns index of the component placed in the specified grid cell or {@code null} if cell is empty.
     *
     * @param container group pane
     * @param column    grid column index
     * @param row       grid row index
     * @param gridSize  actual grid size
     * @return index of the component placed in the specified grid cell or {@code null} if cell is empty
     */
    protected int pointToIndex ( final Container container, final int column, final int row, final GridSize gridSize )
    {
        final boolean ltr = container.getComponentOrientation ().isLeftToRight ();
        final int c = ltr ? column : gridSize.columns - 1 - column;
        return orientation == HORIZONTAL ? row * columns + c : c * rows + row;
    }

    /**
     * Returns column and row sizes.
     *
     * @param container group pane
     * @param gridSize  actual grid size
     * @param type      requested sizes type
     * @return column and row sizes
     */
    protected Pair<int[], int[]> calculateSizes ( final Container container, final GridSize gridSize, final SizeType type )
    {
        final int count = container.getComponentCount ();

        // Calculating initially available column and row sizes
        final int cols = gridSize.columns;
        final int[] colWidths = new int[ cols ];
        final double[] colPercents = new double[ cols ];
        final int rows = gridSize.rows;
        final int[] rowHeights = new int[ rows ];
        final double[] rowPercents = new double[ rows ];
        for ( int i = 0; i < count; i++ )
        {
            final Component component = container.getComponent ( i );
            final GroupPaneConstraints c = constraints.get ( component );
            final Dimension ps = component.getPreferredSize ();

            final int col = indexToColumn ( container, i, gridSize );
            final int row = indexToRow ( i );

            colWidths[ col ] = Math.max ( colWidths[ col ], ( int ) Math.floor ( c.width > 1 ? c.width : ps.width ) );
            colPercents[ col ] = Math.max ( colPercents[ col ], 1 >= c.width && c.width > 0 ? c.width : 0 );
            rowHeights[ row ] = Math.max ( rowHeights[ row ], ( int ) Math.floor ( c.height > 1 ? c.height : ps.height ) );
            rowPercents[ row ] = Math.max ( rowPercents[ row ], 1 >= c.height && c.height > 0 ? c.height : 0 );
        }

        // Calculating resulting column and row sizes
        final Dimension size = container.getSize ();
        final Pair<Double, Integer> rc = calculateSizes ( cols, size.width, colWidths, colPercents );
        final Pair<Double, Integer> rr = calculateSizes ( rows, size.height, rowHeights, rowPercents );

        // Updating sizes with current values
        // This block is only performed for actual layout operation
        if ( type == SizeType.current )
        {
            for ( int i = 0; i < count; i++ )
            {
                final int col = indexToColumn ( container, i, gridSize );
                if ( colPercents[ col ] > 0 && colPercents[ col ] <= 1 )
                {
                    final int pw = ( int ) Math.floor ( rc.getValue () * colPercents[ col ] / rc.getKey () );
                    colWidths[ col ] = Math.max ( pw, colWidths[ col ] );
                }

                final int row = indexToRow ( i );
                if ( rowPercents[ row ] > 0 && rowPercents[ row ] <= 1 )
                {
                    final int ph = ( int ) Math.floor ( rr.getValue () * rowPercents[ row ] / rr.getKey () );
                    rowHeights[ row ] = Math.max ( ph, rowHeights[ row ] );
                }
            }
            appendDelta ( cols, colWidths, size.width );
            appendDelta ( rows, rowHeights, size.height );
        }

        return new Pair<int[], int[]> ( colWidths, rowHeights );
    }

    /**
     * Calculates proper component sizes along with percents summ and free size.
     *
     * @param count    parts count
     * @param size     total available size
     * @param sizes    part sizes
     * @param percents part percentages
     * @return percents summ and free size pair
     */
    protected Pair<Double, Integer> calculateSizes ( final int count, final int size, final int[] sizes, final double[] percents )
    {
        final int[] initSizes = Arrays.copyOf ( sizes, count );
        boolean changed;
        double maxWeight;
        double freePercents;
        int freeSize;
        do
        {
            changed = false;

            // Determining max column and row weights
            maxWeight = 0;
            for ( int i = 0; i < count; i++ )
            {
                if ( percents[ i ] > 0 )
                {
                    maxWeight = Math.max ( maxWeight, initSizes[ i ] / percents[ i ] );
                }
            }

            // Applying column and row weights
            for ( int i = 0; i < count; i++ )
            {
                if ( percents[ i ] > 0 )
                {
                    sizes[ i ] = ( int ) Math.floor ( maxWeight * percents[ i ] );
                }
                else
                {
                    sizes[ i ] = initSizes[ i ];
                }
            }

            // Calculating summary of percent sizes and free pixel size
            freeSize = size;
            freePercents = 0;
            for ( int i = 0; i < count; i++ )
            {
                freeSize -= percents[ i ] == 0 ? sizes[ i ] : 0;
                freePercents += percents[ i ];
            }

            // Normalize percents so that fill parts will be able to take less than 100% of free space
            // So far it have been disabled due to some minor shrinking issues
            // freePercents = Math.max ( 1, freePercents );

            // Stop parts from shrinking below their preferred size
            for ( int i = 0; i < count; i++ )
            {
                if ( percents[ i ] > 0 )
                {
                    final double availSize = freeSize * percents[ i ] / freePercents;
                    if ( sizes[ i ] > availSize && initSizes[ i ] > availSize )
                    {
                        percents[ i ] = 0;
                        changed = true;
                        break;
                    }
                }
            }
        }
        while ( changed );
        return new Pair<Double, Integer> ( freePercents, freeSize );
    }

    /**
     * Appends delta space equally to last elements to properly fill in all available space.
     *
     * @param count parts count
     * @param sizes part sizes
     * @param size  total available size
     */
    protected void appendDelta ( final int count, final int[] sizes, final int size )
    {
        int roughColSize = 0;
        for ( int i = 0; i < count; i++ )
        {
            roughColSize += sizes[ i ];
        }
        int delta = size - roughColSize;
        if ( delta < count )
        {
            for ( int i = count - 1; delta > 0; i--, delta-- )
            {
                sizes[ i ]++;
            }
        }
    }

    @NotNull
    @Override
    public Pair<String, String> getDescriptors ( @NotNull final Container container, @NotNull final Component component, final int index )
    {
        // Retrieving actual grid size
        final GridSize gridSize = getActualGridSize ( container );

        // Retrieving component position
        final int row = indexToRow ( index );
        final int col = indexToColumn ( container, index, gridSize );

        // Calculating descriptors values
        final boolean paintTop;
        final boolean paintTopLine;
        final boolean paintLeft;
        final boolean paintLeftLine;
        final boolean paintBottom;
        final boolean paintBottomLine;
        final boolean paintRight;
        final boolean paintRightLine;
        if ( isNeighbourDecoratable ( container, gridSize, col, row, BoxOrientation.top ) )
        {
            paintTop = false;
            paintTopLine = false;
        }
        else if ( !isPaintTop () && isAtBorder ( container, gridSize, col, row, BoxOrientation.top ) )
        {
            paintTop = false;
            paintTopLine = false;
        }
        else
        {
            paintTop = true;
            paintTopLine = false;
        }
        if ( isNeighbourDecoratable ( container, gridSize, col, row, BoxOrientation.left ) )
        {
            paintLeft = false;
            paintLeftLine = false;
        }
        else if ( !isPaintLeft () && isAtBorder ( container, gridSize, col, row, BoxOrientation.left ) )
        {
            paintLeft = false;
            paintLeftLine = false;
        }
        else
        {
            paintLeft = true;
            paintLeftLine = false;
        }
        if ( isNeighbourDecoratable ( container, gridSize, col, row, BoxOrientation.bottom ) )
        {
            paintBottom = false;
            paintBottomLine = true;
        }
        else if ( !isPaintBottom () && isAtBorder ( container, gridSize, col, row, BoxOrientation.bottom ) )
        {
            paintBottom = false;
            paintBottomLine = false;
        }
        else
        {
            paintBottom = true;
            paintBottomLine = false;
        }
        if ( isNeighbourDecoratable ( container, gridSize, col, row, BoxOrientation.right ) )
        {
            paintRight = false;
            paintRightLine = true;
        }
        else if ( !isPaintRight () && isAtBorder ( container, gridSize, col, row, BoxOrientation.right ) )
        {
            paintRight = false;
            paintRightLine = false;
        }
        else
        {
            paintRight = true;
            paintRightLine = true;
        }

        // Returning descriptors
        final String sides = DecorationUtils.toString ( paintTop, paintLeft, paintBottom, paintRight );
        final String lines = DecorationUtils.toString ( paintTopLine, paintLeftLine, paintBottomLine, paintRightLine );
        return new Pair<String, String> ( sides, lines );
    }


    /**
     * Returns whether or not neighbour painter for component at the specified column/row is decoratable.
     *
     * @param container container
     * @param gridSize  actual grid size
     * @param col       component column
     * @param row       component row
     * @param direction neighbour direction
     * @return {@code true} if neighbour painter for component at the specified column/row is decoratable, {@code false} otherwise
     */
    protected boolean isNeighbourDecoratable ( final Container container, final GridSize gridSize, final int col, final int row,
                                               final BoxOrientation direction )
    {
        final Component neighbour = getNeighbour ( container, gridSize, col, row, direction );
        return PainterSupport.isDecoratable ( neighbour );
    }

    /**
     * Returns neighbour for component at the specified column/row.
     *
     * @param container container
     * @param gridSize  actual grid size
     * @param col       component column
     * @param row       component row
     * @param direction neighbour direction
     * @return neighbour for component at the specified column/row
     */
    protected Component getNeighbour ( final Container container, final GridSize gridSize, final int col, final int row,
                                       final BoxOrientation direction )
    {
        final Component neighbour;
        final boolean ltr = container.getComponentOrientation ().isLeftToRight ();
        if ( direction.isTop () )
        {
            neighbour = row > 0 ? getComponentAt ( container, col, row - 1 ) : null;
        }
        else if ( direction.isBottom () )
        {
            neighbour = row < gridSize.rows - 1 ? getComponentAt ( container, col, row + 1 ) : null;
        }
        else if ( ltr ? direction.isLeft () : direction.isRight () )
        {
            neighbour = col > 0 ? getComponentAt ( container, col - 1, row ) : null;
        }
        else if ( ltr ? direction.isRight () : direction.isLeft () )
        {
            neighbour = col < gridSize.columns - 1 ? getComponentAt ( container, col + 1, row ) : null;
        }
        else
        {
            throw new IllegalArgumentException ( "Unknown neighbour direction: " + direction );
        }
        return neighbour;
    }

    /**
     * Returns whether or not component at the specified column/row is positioned right at container border.
     *
     * @param container container
     * @param gridSize  actual grid size
     * @param col       component column
     * @param row       component row
     * @param direction neighbour direction
     * @return {@code true} if component at the specified column/row is positioned right at container border, {@code false} otherwise
     */
    protected boolean isAtBorder ( final Container container, final GridSize gridSize, final int col, final int row,
                                   final BoxOrientation direction )
    {
        final boolean atBorder;
        final boolean ltr = container.getComponentOrientation ().isLeftToRight ();
        if ( direction.isTop () )
        {
            atBorder = row == 0;
        }
        else if ( direction.isBottom () )
        {
            atBorder = row == gridSize.rows - 1;
        }
        else if ( direction.isLeft () )
        {
            atBorder = col == ( ltr ? 0 : gridSize.columns - 1 );
        }
        else if ( direction.isRight () )
        {
            atBorder = col == ( ltr ? gridSize.columns - 1 : 0 );
        }
        else
        {
            throw new IllegalArgumentException ( "Unknown border direction: " + direction );
        }
        return atBorder;
    }
}