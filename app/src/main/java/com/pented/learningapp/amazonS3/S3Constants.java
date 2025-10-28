package com.pented.learningapp.amazonS3;

public class S3Constants { public static final String BUCKET_URL = "https://s3-ap-southeast-2.amazonaws.com/nosho/";
    /* public static final String URL = "https://s3-us-west-2.amazonaws.com/";

     *//*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     *//*
    public static final String COGNITO_POOL_ID = "us-east-1:580a3e39-1f42-4228-a90e-7247db72b8ef";

    *//*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     *//*
    public static final String BUCKET_NAME = "noshobucket";*/

    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    //public static final String COGNITO_POOL_ID = "ap-south-1:5585c871-6236-432a-92a9-97415e3871dd";
 public static final String COGNITO_POOL_ID = "ap-south-1:628410491890:userpool/ap-south-1_fKY1Pi4Ep";

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    public static final String BUCKET_NAME = "nosho";



    /*
     * Specify max no_data_layout of retry count when uploading fail.
     * */
    public static final int mMaxRetry=3;

}
