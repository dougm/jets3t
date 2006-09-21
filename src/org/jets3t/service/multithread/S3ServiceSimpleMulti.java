package org.jets3t.service.multithread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

/**
 * S3 service wrapper that performs multiple S3 requests at a time using multi-threading and an
 * underlying thread-safe {@link S3Service} implementation. 
 * <p>
 * This service provides a simplified interface to the {@link S3ServiceMulti} service. It will block while 
 * doing its work, return the results of an operation when it is finished, and throw an exception if
 * anything goes wrong. 
 * <p>
 * For a non-blocking multi-threading service that is more powerful, but also more complicated, 
 * see {@link S3ServiceMulti}.
 * 
 * @author James Murty
 */
public class S3ServiceSimpleMulti {    
    private S3Service s3Service = null;

    public S3ServiceSimpleMulti(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    /**
     * Utility method to check an {@link S3ServiceEventAdaptor} for the occurrence of an error, and if
     * one is present to throw it.
     * 
     * @param adaptor
     * @throws S3ServiceException
     */
    protected void throwError(S3ServiceEventAdaptor adaptor) throws S3ServiceException {
        if (adaptor.wasErrorThrown()) {
            if (adaptor.getErrorThrown() instanceof S3ServiceException) {
                throw (S3ServiceException) adaptor.getErrorThrown();
            } else {
                throw new S3ServiceException(adaptor.getErrorThrown());
            }
        }        
    }
    
    /**
     * Creates multiple buckets.
     * 
     * @param buckets
     * the buckets to create.
     * @return
     * the created buckets.
     * @throws S3ServiceException
     */
    public S3Bucket[] createBuckets(final S3Bucket[] buckets) throws S3ServiceException {
        final List bucketList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(CreateBucketsEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    bucketList.addAll(Arrays.asList(event.getCreatedBuckets()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).createBuckets(buckets);
        throwError(adaptor);
        return (S3Bucket[]) bucketList.toArray(new S3Bucket[] {});
    }
    
    /**
     * Creates/uploads multiple objects.
     * 
     * @param bucket
     * the bucket to create the objects in.
     * @param objects
     * the objects to create/upload.
     * @return
     * the created/uploaded objects.
     * @throws S3ServiceException
     */
    public S3Object[] putObjects(final S3Bucket bucket, final S3Object[] objects) throws S3ServiceException {    
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(CreateObjectsEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getCreatedObjects()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).putObjects(bucket, objects);
        throwError(adaptor);
        return (S3Object[]) objectList.toArray(new S3Object[] {});
    }
    
    /**
     * Deletes multiple objects
     * 
     * @param bucket
     * the bucket containing the objects to delete.
     * @param objects
     * the objects to delete.
     * @throws S3ServiceException
     */
    public void deleteObjects(final S3Bucket bucket, final S3Object[] objects) throws S3ServiceException {
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(DeleteObjectsEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getDeletedObjects()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).deleteObjects(bucket, objects);
        throwError(adaptor);
    }
    
    /**
     * Retrieves multiple objects (including details and data)
     * 
     * @param bucket
     * the bucket containing the objects.
     * @param objects
     * the objects to retrieve.
     * @return
     * the retrieved objects.
     * @throws S3ServiceException
     */
    public S3Object[] getObjects(S3Bucket bucket, S3Object[] objects) throws S3ServiceException {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        return getObjects(bucket, objectKeys);
    }
    
    /**
     * Retrieves multiple objects (including details and data)
     * 
     * @param bucket
     * the bucket containing the objects.
     * @param objectKeys
     * the key names of the objects to retrieve.
     * @return
     * the retrieved objects.
     * @return
     * @throws S3ServiceException
     */
    public S3Object[] getObjects(final S3Bucket bucket, final String[] objectKeys) throws S3ServiceException {
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(GetObjectsEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getCompletedObjects()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).getObjects(bucket, objectKeys);
        throwError(adaptor);
        return (S3Object[]) objectList.toArray(new S3Object[] {});
    }

    /**
     * Retrieves details of multiple objects (details only, no data)
     * 
     * @param bucket
     * the bucket containing the objects.
     * @param objects
     * the objects to retrieve.
     * @return
     * objects populated with the details retrieved.
     * @throws S3ServiceException
     */
    public S3Object[] getObjectsHeads(S3Bucket bucket, S3Object[] objects) throws S3ServiceException {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        return getObjectsHeads(bucket, objectKeys);
    }
    
    /**
     * Retrieves details of multiple objects (details only, no data)
     * 
     * @param bucket
     * the bucket containing the objects.
     * @param objectKeys
     * the key names of the objects to retrieve.
     * @return
     * objects populated with the details retrieved.
     * @throws S3ServiceException
     */
    public S3Object[] getObjectsHeads(final S3Bucket bucket, final String[] objectKeys) throws S3ServiceException {
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(GetObjectHeadsEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getCompletedObjects()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).getObjectsHeads(bucket, objectKeys);
        throwError(adaptor);
        return (S3Object[]) objectList.toArray(new S3Object[] {});
    }
    
    /**
     * Retrieves Access Control List (ACL) settings for multiple objects. 
     *  
     * @param bucket
     * the bucket containing the objects.
     * @param objects
     * the objects whose ACLs will be retrieved.
     * @return
     * objects including the ACL information retrieved.
     * @throws S3ServiceException
     */
    public S3Object[] getObjectACLs(final S3Bucket bucket, final S3Object[] objects) throws S3ServiceException {
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(LookupACLEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getObjectsWithACL()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).getObjectACLs(bucket, objects);
        throwError(adaptor);
        return (S3Object[]) objectList.toArray(new S3Object[] {});
    }

    /**
     * Updates/sets Access Control List (ACL) settings for multiple objects. 
     *  
     * @param bucket
     * the bucket containing the objects.
     * @param objects
     * objects containing ACL settings that will be updated/set.
     * @return
     * objects whose ACL settings were updated/set.
     * @throws S3ServiceException
     */
    public S3Object[] putACLs(final S3Bucket bucket, final S3Object[] objects) throws S3ServiceException {
        final List objectList = new ArrayList();
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor() {
            public void s3ServiceEventPerformed(UpdateACLEvent event) {
                super.s3ServiceEventPerformed(event);
                if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                    objectList.addAll(Arrays.asList(event.getObjectsWithUpdatedACL()));
                }
            };
        };
        (new S3ServiceMulti(s3Service, adaptor)).putACLs(bucket, objects);
        throwError(adaptor);
        return (S3Object[]) objectList.toArray(new S3Object[] {});
    }
    
    /**
     * A convenience method to download multiple objects from S3 to pre-existing output streams, which
     * is particularly useful for downloading objects to files. 
     * 
     * @param bucket
     * the bucket containing the objects
     * @param objectAndOutputStream
     * an array of S3Object/OutputStream pairs indicating the object to be downloaded, and the output 
     * stream where the object's contents will be written.
     * @throws S3ServiceException
     */
    public void downloadObjects(final S3Bucket bucket, final S3ObjectAndOutputStream[] objectAndOutputStream) throws S3ServiceException {
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor();
        (new S3ServiceMulti(s3Service, adaptor)).downloadObjects(bucket, objectAndOutputStream);
        throwError(adaptor);
    }
    
    
}
