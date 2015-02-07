package com.yurkiv.pianoflow;

import android.content.Context;

public interface Notifier {
    void operationFinished(Context context, int city, String cityName);
}