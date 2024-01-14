# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Skip useless warnings
-dontwarn sun.**
-dontwarn java.**
-dontnote org.apache.http.**
-dontnote android.net.**
#-ignorewarnings
-dontoptimize
-dontshrink

# Very important line below
-dontskipnonpubliclibraryclassmembers

# Keep annotations
-keepattributes *Annotation*

# Needed for eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Add any project specific keep options here:
-keep class com.nex3z.**
-keepclasseswithmembernames class com.esotericsoftware.** {
    <fields>;
    <methods>;
}
-keepclasseswithmembernames class creationsahead.speedwordsearch.mod.** {
    <fields>;
    <methods>;
}