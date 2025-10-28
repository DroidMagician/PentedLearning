package com.pented.learningapp.helper.exoplayer;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.Format;

class HLSUtil {

    private HLSUtil() {
    }

    @NonNull
    static HLSQuality getQuality(@NonNull Format format) {
             switch (format.height) {
            case 1080: {
                return HLSQuality.Quality1080;
            }
            case 720: {
                return HLSQuality.Quality720;
            }
            case 480:
            case 486: {
                return HLSQuality.Quality480;
            }
            default: {
                return HLSQuality.NoValue;
            }
        }
    }

    static boolean isQualityPlayable(@NonNull Format format) {
        return format.height <= 1080;
    }
}
