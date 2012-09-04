package p2pct.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import p2pct.model.PlayListEntry;
import p2pct.model.SearchTermRankingEntry;
import p2pct.model.SongVoteEntry;

public class RatingTableModel implements TableModel {
	
	private String[] columns = new String[]{"SearchTerm", "Song", "Rating"};
	private int[] columnWidths = new int[]{130,220,50};
	private List<List<String>> data = new ArrayList<List<String>>();
    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    @Override
    public int getRowCount()
    {
    	synchronized (this.data) {
    		return data.size();
    	}
    }

    @Override
    public int getColumnCount()
    {
        return this.columns.length;
    }

    @Override
    public String getColumnName( int columnIndex )
    {
    	return this.columns[columnIndex];
    }

    @Override
    public Class<?> getColumnClass( int columnIndex )
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex )
    {
        return false;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
    	synchronized (this.data) {
            return data.get( rowIndex ).get( columnIndex );
		}
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex )
    {
        throw new RuntimeException( "cannot edit here" );
    }

    private void addRow(String searchTerm, String song, float rating)
    {
    	synchronized (this.data) {
	        List<String> tmp=new ArrayList<String>();            
	        tmp.add( searchTerm );
	        tmp.add( song );
	        tmp.add( Float.toString(rating) );
	        data.add( tmp );
    	}
    	notifyListeners();
    }

    @Override
    public void addTableModelListener( TableModelListener l )
    {
        listeners.add( l );
    }

    @Override
    public void removeTableModelListener( TableModelListener l )
    {
        listeners.remove( l );
    }

    private void notifyListeners()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                for ( TableModelListener l : listeners )
                {
                    l.tableChanged( new TableModelEvent( RatingTableModel.this ) );
                }
            }
        } );
    }

	public void update(List<SearchTermRankingEntry> entries) {
		synchronized (this.data) {
			this.data.clear();
			for (SearchTermRankingEntry entry : entries) {
				this.addRow(entry.getSearchTerm(), entry.getSong().getTitle()+ " ("+entry.getSong().getArtist()+")", entry.getRating());
			}
		}
        notifyListeners();
	}

	public void setWidths(TableColumnModel columnModel) {
		for (int i=0; i<columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
			columnModel.getColumn(i).setWidth(columnWidths[i]);
		}
	}
}