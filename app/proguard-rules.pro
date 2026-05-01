-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class * extends androidx.room.RoomDatabase
