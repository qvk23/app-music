package com.example.music_app.Model

import java.util.*
import java.util.concurrent.TimeUnit


class DateTimeFormatUtils()  {
    fun formatDuration(duration: Long) = String.format(
        Locale.getDefault(),"%02d:%d",
        TimeUnit.MILLISECONDS.toMinutes(duration),
        TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
    )
}
