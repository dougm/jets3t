package org.jets3t.apps.cockpit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.table.DefaultTableModel;

import org.jets3t.service.model.S3Bucket;

public class BucketTableModel extends DefaultTableModel {
    
    ArrayList bucketList = new ArrayList();
    
    public BucketTableModel() {
        super(new String[] {"Buckets"}, 0);
    }
    
    public int addBucket(S3Bucket bucket) {
        int insertRow = 
            Collections.binarySearch(bucketList, bucket, new Comparator() {
                public int compare(Object o1, Object o2) {
                    String b1Name = ((S3Bucket)o1).getName();
                    String b2Name = ((S3Bucket)o2).getName();
                    int result =  b1Name.compareToIgnoreCase(b2Name);
                    return result;
                }
            });
        if (insertRow >= 0) {
            // We already have an item with this key, replace it.
            bucketList.remove(insertRow);
            this.removeRow(insertRow);                
        } else {
            insertRow = (-insertRow) - 1;                
        }
        // New object to insert.
        bucketList.add(insertRow, bucket);
        this.insertRow(insertRow, new Object[] {bucket.getName()});
        return insertRow;
    }
    
    public void removeBucket(S3Bucket bucket) {
        int index = bucketList.indexOf(bucket);
        this.removeRow(index);
        bucketList.remove(bucket);
    }
    
    public void removeAllBuckets() {
        int rowCount = this.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            this.removeRow(0);
        }
        bucketList.clear();
    }
    
    public S3Bucket getBucket(int row) {
        return (S3Bucket) bucketList.get(row);
    }
    
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
}