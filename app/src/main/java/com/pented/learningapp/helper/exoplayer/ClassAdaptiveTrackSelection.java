package com.pented.learningapp.helper.exoplayer;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.upstream.BandwidthMeter;

import java.util.List;

@SuppressLint("RestrictedApi")
public class ClassAdaptiveTrackSelection extends BaseTrackSelection {

    public static final class Factory implements TrackSelectionUtil.AdaptiveTrackSelectionFactory {
        private final BandwidthMeter bandwidthMeter;
        private final int maxInitialBitrate = 2000000;
        private final int minDurationForQualityIncreaseMs = 10000;
        private final int maxDurationForQualityDecreaseMs = 25000;
        private final int minDurationToRetainAfterDiscardMs = 25000;
        private final float bandwidthFraction = 0.75f;
        private final float bufferedFractionToLiveEdgeForQualityIncrease = 0.75f;

        public Factory(BandwidthMeter bandwidthMeter) {
            this.bandwidthMeter = bandwidthMeter;
        }

        @Override
        public ExoTrackSelection createAdaptiveTrackSelection(Definition trackSelectionDefinition) {
            return  new ClassAdaptiveTrackSelection(
                    trackSelectionDefinition.group,
                    trackSelectionDefinition.tracks,
                    bandwidthMeter,
                    maxInitialBitrate,
                    minDurationForQualityIncreaseMs,
                    maxDurationForQualityDecreaseMs,
                    minDurationToRetainAfterDiscardMs,
                    bandwidthFraction,
                    bufferedFractionToLiveEdgeForQualityIncrease
            );
        }

//        @Override
//        public ClassAdaptiveTrackSelection createTrackSelection(TrackGroup group, int... tracks) {
//            Log.d(ClassAdaptiveTrackSelection.class.getSimpleName(), " Video player quality reset to Auto");
//            sHLSQuality = HLSQuality.Auto;
//
//            return new ClassAdaptiveTrackSelection(
//                    group,
//                    tracks,
//                    bandwidthMeter,
//                    maxInitialBitrate,
//                    minDurationForQualityIncreaseMs,
//                    maxDurationForQualityDecreaseMs,
//                    minDurationToRetainAfterDiscardMs,
//                    bandwidthFraction,
//                    bufferedFractionToLiveEdgeForQualityIncrease
//            );
//        }

//        @Override
//        public TrackSelection createTrackSelection(TrackGroup group, BandwidthMeter bandwidthMeter, int... tracks) {
//
//        }

//        @Override
//        public TrackSelection[] createTrackSelections(Definition[] definitions, BandwidthMeter bandwidthMeter) {
//            return new TrackSelection[0];
//        }
    }

    private static HLSQuality sHLSQuality = HLSQuality.Auto;
    private final BandwidthMeter bandwidthMeter;
    private final int maxInitialBitrate;
    private final long minDurationForQualityIncreaseUs;
    private final long maxDurationForQualityDecreaseUs;
    private final long minDurationToRetainAfterDiscardUs;
    private final float bandwidthFraction;
    private final float bufferedFractionToLiveEdgeForQualityIncrease;

    private int selectedIndex;
    private int reason;

    @SuppressLint("RestrictedApi")
    private ClassAdaptiveTrackSelection(TrackGroup group,
                                        int[] tracks,
                                        BandwidthMeter bandwidthMeter,
                                        int maxInitialBitrate,
                                        long minDurationForQualityIncreaseMs,
                                        long maxDurationForQualityDecreaseMs,
                                        long minDurationToRetainAfterDiscardMs,
                                        float bandwidthFraction,
                                        float bufferedFractionToLiveEdgeForQualityIncrease) {
        super(group, tracks);
        this.bandwidthMeter = bandwidthMeter;
        this.maxInitialBitrate = maxInitialBitrate;
        this.minDurationForQualityIncreaseUs = minDurationForQualityIncreaseMs * 1000L;
        this.maxDurationForQualityDecreaseUs = maxDurationForQualityDecreaseMs * 1000L;
        this.minDurationToRetainAfterDiscardUs = minDurationToRetainAfterDiscardMs * 1000L;
        this.bandwidthFraction = bandwidthFraction;
        this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
        selectedIndex = determineIdealSelectedIndex(Long.MIN_VALUE);
        reason = C.SELECTION_REASON_INITIAL;
    }



    @Override
    public int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public int getSelectionReason() {
        return reason;
    }

    @Override
    public Object getSelectionData() {
        return null;
    }

    @Override
    public void onDiscontinuity() {

    }

    @Override
    public void onRebuffer() {

    }

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady) {

    }

    @Override
    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
        long nowMs = SystemClock.elapsedRealtime();
        // Stash the current selection, then make a new one.
        int currentSelectedIndex = selectedIndex;
        selectedIndex = determineIdealSelectedIndex(nowMs);
        if (selectedIndex == currentSelectedIndex) {
            return;
        }

        if (!isBlacklisted(currentSelectedIndex, nowMs)) {
            // Revert back to the current selection if conditions are not suitable for switching.
            Format currentFormat = getFormat(currentSelectedIndex);
            Format selectedFormat = getFormat(selectedIndex);
            if (selectedFormat.bitrate > currentFormat.bitrate
                    && bufferedDurationUs < minDurationForQualityIncreaseUs(availableDurationUs)) {
                // The selected track is a higher quality, but we have insufficient buffer to safely switch
                // up. Defer switching up for now.
                selectedIndex = currentSelectedIndex;
            } else if (selectedFormat.bitrate < currentFormat.bitrate
                    && bufferedDurationUs >= maxDurationForQualityDecreaseUs) {
                // The selected track is a lower quality, but we have sufficient buffer to defer switching
                // down for now.
                selectedIndex = currentSelectedIndex;
            }
        }
        // If we adapted, update the trigger.
        if (selectedIndex != currentSelectedIndex) {
            reason = C.SELECTION_REASON_ADAPTIVE;
        }
    }

    @Override
    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (queue.isEmpty()) {
            return 0;
        }
        int queueSize = queue.size();
        long bufferedDurationUs = queue.get(queueSize - 1).endTimeUs - playbackPositionUs;
        if (bufferedDurationUs < minDurationToRetainAfterDiscardUs) {
            return queueSize;
        }
        int idealSelectedIndex = determineIdealSelectedIndex(SystemClock.elapsedRealtime());
        Format idealFormat = getFormat(idealSelectedIndex);
        // If the chunks contain video, discard from the first SD chunk beyond
        // minDurationToRetainAfterDiscardUs whose resolution and bitrate are both lower than the ideal
        // track.
        for (int i = 0; i < queueSize; i++) {
            MediaChunk chunk = queue.get(i);
            Format format = chunk.trackFormat;
            long durationBeforeThisChunkUs = chunk.startTimeUs - playbackPositionUs;
            if (durationBeforeThisChunkUs >= minDurationToRetainAfterDiscardUs
                    && format.bitrate < idealFormat.bitrate
                    && format.height != Format.NO_VALUE && format.height < 720
                    && format.width != Format.NO_VALUE && format.width < 1280
                    && format.height < idealFormat.height) {
                return i;
            }
        }
        return queueSize;
    }

    @Override
    public boolean shouldCancelChunkLoad(long playbackPositionUs, Chunk loadingChunk, List<? extends MediaChunk> queue) {
        return false;
    }

    private int determineIdealSelectedIndex(long nowMs) {
        if (sHLSQuality != HLSQuality.Auto) {
            Log.d(ClassAdaptiveTrackSelection.class.getSimpleName(), " Video player quality seeking for " + String.valueOf(sHLSQuality));
            for (int i = 0; i < length; i++) {
                Format format = getFormat(i);
                if (HLSUtil.getQuality(format) == sHLSQuality) {
                    Log.d(ClassAdaptiveTrackSelection.class.getSimpleName(), " Video player quality set to " + String.valueOf(sHLSQuality));
                    return i;
                }
            }
        }

        Log.d(ClassAdaptiveTrackSelection.class.getSimpleName(), " Video player quality seeking for auto quality " + String.valueOf(sHLSQuality));
        long bitrateEstimate = bandwidthMeter.getBitrateEstimate();
        long effectiveBitrate = bitrateEstimate == bandwidthMeter.getBitrateEstimate() ? maxInitialBitrate : (long) (bitrateEstimate * bandwidthFraction);
        int lowestBitrateNonBlacklistedIndex = 0;
        for (int i = 0; i < length; i++) {
            if (nowMs == Long.MIN_VALUE || !isBlacklisted(i, nowMs)) {
                Format format = getFormat(i);
                if (format.bitrate <= effectiveBitrate && HLSUtil.isQualityPlayable(format)) {
                    Log.d(ClassAdaptiveTrackSelection.class.getSimpleName(), " Video player quality auto quality found " + String.valueOf(sHLSQuality));
                    return i;
                } else {
                    lowestBitrateNonBlacklistedIndex = i;
                }
            }
        }
        return lowestBitrateNonBlacklistedIndex;
    }

    private long minDurationForQualityIncreaseUs(long availableDurationUs) {
        boolean isAvailableDurationTooShort = availableDurationUs != C.TIME_UNSET
                && availableDurationUs <= minDurationForQualityIncreaseUs;
        return isAvailableDurationTooShort
                ? (long) (availableDurationUs * bufferedFractionToLiveEdgeForQualityIncrease)
                : minDurationForQualityIncreaseUs;
    }

    static void setHLSQuality(HLSQuality HLSQuality) {
        sHLSQuality = HLSQuality;
    }
}