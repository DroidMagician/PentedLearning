package com.pented.learningapp.amazonS3;

import android.content.Context;
import android.text.TextUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pented.learningapp.BuildConfig;
import com.pented.learningapp.myUtils.Keys;

import java.io.File;

public class S3Util { // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    // set socket timeout as per needed default time out is 30 seconds
    private static int SOCKET_TIMEOUT = 30 * 1000;
    // set connection timeout as per needed default time out is 30 seconds
    private static int CONNECTION_TIMEOUT = 30 * 1000;
    // set maximum retry on error or cancel uploads
    private static int MAX_RETRY = 3;

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
//    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
//        if (sCredProvider == null) {
//            sCredProvider = new CognitoCachingCredentialsProvider(
//                    context.getApplicationContext(),
//                    S3Constants.COGNITO_POOL_ID,
//                    Regions.AP_SOUTHEAST_2);//Regions.US_EAST_1
//
//        }
//        return sCredProvider;
//    }


//    public static AmazonS3Client getS3Client() {
//        if (sS3Client == null) {
////            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
//            sS3Client = new AmazonS3Client(new BasicAWSCredentials(
//                    BuildConfig.AWS_ACCESS_KEY
//                    , BuildConfig.AWS_SECRET_KEY), Region.getRegion(Regions.fromName(BuildConfig.BUCKET_REGION)));
//        }
//        return sS3Client;
//    }

    public static AmazonS3Client getS3Client() {

        if (sS3Client == null) {

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.withSocketTimeout(SOCKET_TIMEOUT);
            clientConfiguration.setConnectionTimeout(CONNECTION_TIMEOUT);
            clientConfiguration.setMaxErrorRetry(MAX_RETRY);

//            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
//            sS3Client = new AmazonS3Client(new BasicAWSCredentials(
//                    Keys.INSTANCE.accessKey(true)
//                    ,Keys.INSTANCE.secretKey(true)), Region.getRegion(Regions.fromName(Keys.INSTANCE.bucketRegion(true))), clientConfiguration);

            sS3Client = new AmazonS3Client(new BasicAWSCredentials(
                    Keys.INSTANCE.accessKey(true)
                    ,Keys.INSTANCE.secretKey(true)), clientConfiguration);

        }
        // 👇 Add your custom endpoint here
        sS3Client.setEndpoint("https://s3.in-west2.purestore.io");

        //https://s3.in-west2.purestore.io
        // 👇 If PureStore requires path-style access
        sS3Client.setS3ClientOptions(
                S3ClientOptions.builder()
                        .setPathStyleAccess(true)
                        .build()
        );
        return sS3Client;
    }
    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
   /* public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            *//*
     * setup client configuration infinite socket timeout & Connection timeout
     * *//*
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.withSocketTimeout(SOCKET_TIMEOUT);
            clientConfiguration.setConnectionTimeout(CONNECTION_TIMEOUT);
            clientConfiguration.setMaxErrorRetry(MAX_RETRY);
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()), clientConfiguration);

        }
        return sS3Client;
    }*/

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
//    public static TransferUtility getTransferUtility(Context context) {
//        if (sTransferUtility == null) {
//
//            ClientConfiguration clientConfiguration = new ClientConfiguration();
//            clientConfiguration.withSocketTimeout(SOCKET_TIMEOUT);
//            clientConfiguration.setConnectionTimeout(CONNECTION_TIMEOUT);
//            clientConfiguration.setMaxErrorRetry(MAX_RETRY);
//            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()), clientConfiguration);
//
//            sTransferUtility = TransferUtility.builder().s3Client(getS3Client()).context(context).build();
//
//
//        }
//
//        return sTransferUtility;
//    }

    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder().s3Client(getS3Client()).context(context).build();
        }

        return sTransferUtility;
    }
    /**
     * Used to get filename from path
     *
     * @param path pass the path
     * @return name of file
     */
    public static String getFilenameFromPath(String path) {
        if (TextUtils.isEmpty(path)) return "";
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Used to delete media
     *
     * @param context
     * @param key
     */

    public static void deleteMedia(Context context, String key) {
        if (TextUtils.isEmpty(key))
            return;
        AmazonS3Client s3Client = getS3Client();
        s3Client.deleteObject(S3Constants.BUCKET_NAME, key);
    }
    public static void clearAllUploads(Context context) {
        TransferUtility transferUtility = getTransferUtility(context);
        transferUtility.cancelAllWithType(TransferType.UPLOAD);
    }

    public static boolean cancelTask(Context context, TransferObserver transferObserver) {
        TransferUtility transferUtility = getTransferUtility(context);
        return transferUtility.cancel(transferObserver.getId());
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

     /*Server Urls

        Image : /image/image_12345674.jpg
                /image/thumb/thumb_12345674.jpg

        Video : /video/video_12345674.jpg
                /video/thumb/thumb_12345674.jpg

     */


    public static TransferObserver uploadMedia(final Context context, File file, String s3Path, TransferListener l) {
        TransferObserver observer = getTransferUtility(context).upload(S3Constants.BUCKET_NAME, s3Path, file);
        observer.setTransferListener(l);
        return observer;
    }

    public static TransferObserver uploadMedia(final Context context, File file, String s3Path, ObjectMetadata objectMetadata, TransferListener l) {
        TransferObserver observer = getTransferUtility(context).upload(S3Constants.BUCKET_NAME, s3Path, file, objectMetadata);
        observer.setTransferListener(l);
        return observer;
    }

    public static String getFilenameFronPath(String mFilePath) {
        if (!TextUtils.isEmpty(mFilePath)) {
            return mFilePath.substring(mFilePath.lastIndexOf("/") + 1);
        }
        return "";
    }
}
