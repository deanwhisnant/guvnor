package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Based on GWT bikeshed example.
 */
public class SortableHeader<T, C extends Comparable> extends Header<String> {

    private static final TableImageResources TABLE_IMAGE_RESOURCES = GWT.create( TableImageResources.class );
    private static final String              DOWN_ARROW            = makeImage( TABLE_IMAGE_RESOURCES.downArrow() );
    private static final String              SMALL_DOWN_ARROW      = makeImage( TABLE_IMAGE_RESOURCES.smallDownArrow() );
    private static final String              UP_ARROW              = makeImage( TABLE_IMAGE_RESOURCES.upArrow() );
    private static final String              SMALL_UP_ARROW        = makeImage( TABLE_IMAGE_RESOURCES.smallUpArrow() );

    private static String makeImage(ImageResource resource) {
        AbstractImagePrototype prototype = AbstractImagePrototype.create( resource );
        return prototype.getHTML();
    }

    private final SortableHeaderGroup sortableHeaderGroup;
    private String                    text;
    private final Column<T, C>        column;
    private SortDirection             sortDirection = SortDirection.NONE;
    private int                       sortIndex     = -1;

    public SortableHeader(SortableHeaderGroup sortableHeaderGroup,
                          String text,
                          Column<T, C> column) {
        super( new ClickableTextCell() );
        this.sortableHeaderGroup = sortableHeaderGroup;
        this.text = text;
        this.column = column;
        setUpdater( new ValueUpdater<String>() {
            public void update(String s) {
                SortableHeader.this.sortableHeaderGroup.headerClicked( SortableHeader.this );
            }
        } );
    }

    /**
     * @return the header label
     */
    public String getValue() {
        return text;
    }

    /**
     * Set the Column header
     * 
     * @param text
     */
    public void setValue(String text) {
        this.text = text;
    }

    public Column<T, C> getColumn() {
        return column;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public void render(Cell.Context context,
                       SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<div style='position: relative; cursor: pointer; padding: 0px;'>" );
        sb.appendHtmlConstant( "<span style='padding-right: 10px'>" );
        sb.appendEscaped( text );
        sb.appendHtmlConstant( "</span>" );
        // sb.appendHtmlConstant("<div style='position:absolute;right:0px;top:0px;'></div>");
        switch ( sortDirection ) {
            case NONE :
                // nothing
                break;
            case ASCENDING :
                sb.appendHtmlConstant( sortIndex == 0 ? UP_ARROW : SMALL_UP_ARROW );
                break;
            case DESCENDING :
                sb.appendHtmlConstant( sortIndex == 0 ? DOWN_ARROW : SMALL_DOWN_ARROW );
                break;
            default :
                throw new IllegalArgumentException( "Unknown sortDirection ("
                                                    + sortDirection
                                                    + ")." );
        }
        sb.appendHtmlConstant( "</div>" );
        // sb.appendHtmlConstant("<div>");
        // sb.appendHtmlConstant("</div></div>");
    }

}
