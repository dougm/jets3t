package org.jets3t.apps.cockpit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jets3t.service.model.S3Object;

public class ObjectTableModel extends DefaultTableModel {
    private JTable objectsTable = null;
    private ArrayList objectList = new ArrayList();
    
    public ObjectTableModel(JTable objectsTable) {
        super(new String[] {"Object Key","Size","Last Modified"}, 0);
        this.objectsTable = objectsTable;
    }
    
    public void addObject(S3Object object, boolean highlightNewObject) {
        int insertRow = 
            Collections.binarySearch(objectList, object, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((S3Object)o1).getKey().compareToIgnoreCase(((S3Object)o2).getKey());
                }
            });
        if (insertRow >= 0) {
            // We already have an item with this key, replace it.
            objectList.remove(insertRow);
            this.removeRow(insertRow);                
        } else {
            insertRow = (-insertRow) - 1;                
        }
        // New object to insert.
        objectList.add(insertRow, object);
        this.insertRow(insertRow, new Object[] {object.getKey(), 
            new Long(object.getContentLength()), object.getLastModifiedDate()});
        
        // Automatically select (highlight) a newly aded object, if required.
        if (highlightNewObject) {
            objectsTable.addRowSelectionInterval(insertRow, insertRow);
        }
    }
    
    public void addObjects(S3Object[] objects, boolean highlightNewObject) {
        for (int i = 0; i < objects.length; i++) {
            addObject(objects[i], highlightNewObject);
        }
    }
    
    public void removeObject(S3Object object) {
        int index = objectList.indexOf(object);
        this.removeRow(index);
        objectList.remove(object);
    }
    
    public void removeAllObjects() {
        int rowCount = this.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            this.removeRow(0);
        }
        objectList.clear();
    }
    
    public S3Object getObject(int row) {
        synchronized (objectList) {
            return (S3Object) objectList.get(row);
        }
    }
    
    public S3Object[] getObjects() {
        synchronized (objectList) {
            return (S3Object[]) objectList.toArray(new S3Object[] {});
        }            
    }
    
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        } else if (columnIndex == 1) {
            return Long.class;
        } else {
            return Date.class;
        }
    }
    
}