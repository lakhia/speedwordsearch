# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Skip useless warnings
-dontwarn sun.**
-dontwarn java.**
-dontnote org.apache.http.**
-dontnote android.net.**
#-ignorewarnings
#-dontoptimize
#-dontshrink

# Keep annotations
-keepattributes *Annotation*

# Models
-keep class creationsahead.speedwordsearch.mod.** {
    <fields>;
    <init>();
}

# Needed for eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# External libs
-keep class com.nex3z.**
-keep class com.esotericsoftware.** { *; }
