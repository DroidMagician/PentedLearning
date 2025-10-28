package com.pented.learningapp.myUtils

object Keys {

    //BUCKET_REGION="ap-south-1"
    //AWS_ACCESS_KEY="AKIAZEUBPEPZL4BEWOUK"
    //AWS_SECRET_KEY="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"
    //S3_BASE_URL="https://s3.console.aws.amazon.com/s3/buckets/pentedsolutionvideos/"
    //BUCKET_NAME="pentedapp"

    init {
        // Used to load the 'native-lib' library on application startup.
        System.loadLibrary("native-lib")
    }

    //Native calls from C++ to Kotlin

    /**
     * Returns the Bucket Region
     * @param isLive: Pass Build Variant only. [Client / Develop]
     * @return Bucket Region
     */
    external fun bucketRegion(isLive: Boolean): String



    external fun accessKey(isLive: Boolean): String

    external fun secretKey(isLive: Boolean): String

    /**
     * Returns the S3 Base URL
     * @param isLive: Pass Build Variant only. [Client / Develop]
     * @return S3 Base URL
     */
    external fun s3BaseUrl(isLive: Boolean): String

    /**
     * Returns the Bucket Name
     * @param isLive: Pass Build Variant only. [Client / Develop]
     * @return Bucket Name
     */
    external fun bucketName(isLive: Boolean): String


    /**
     * Returns the Stripe Key
     * @param isLive: Pass Build Variant only. [Client / Develop]
     * @return: Stripe Key
     */
    external fun stripeKey(isLive: Boolean): String



}