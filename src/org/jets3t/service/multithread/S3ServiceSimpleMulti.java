package org.jets3t.service.multithread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public class S3ServiceSimpleMulti {
    private final Log log = LogFactory.getLog(S3ServiceSimpleMulti.class);
    
    private S3Service s3Service = null;

    public S3ServiceSimpleMulti(S3Service s3Service) {
        this.s3Service = s3Service;
    }
    
    protected void throwError(S3ServiceEventAdaptor adaptor) throws S3ServiceException {
        if (adaptor.wasErrorThrown()) {
            if (adaptor.getErrorThrown() instanceof S3ServiceException) {
                throw (S3ServiceException) adaptor.getErrorThrown();
            } else {
                throw new S3ServiceException(adaptor.getErrorThrown());
            }
        }        
    }
    
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
    
    public S3Object[] getObjects(S3Bucket bucket, S3Object[] objects) throws S3ServiceException {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        return getObjects(bucket, objectKeys);
    }
    
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

    public S3Object[] getObjectsHeads(S3Bucket bucket, S3Object[] objects) throws S3ServiceException {
        String[] objectKeys = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            objectKeys[i] = objects[i].getKey();
        }
        return getObjectsHeads(bucket, objectKeys);
    }
    
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
    
    public void downloadObjects(final S3Bucket bucket, final S3ObjectAndOutputStream[] objectAndOutputStream) throws S3ServiceException {
        S3ServiceEventAdaptor adaptor = new S3ServiceEventAdaptor();
        (new S3ServiceMulti(s3Service, adaptor)).downloadObjects(bucket, objectAndOutputStream);
        throwError(adaptor);
    }
    
    
}
