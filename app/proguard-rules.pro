# La Taverne release ProGuard rules.
# kotlinx.serialization needs its generated serializers kept.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclasseswithmembers class com.beloucif.lataverne.content.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class com.beloucif.lataverne.content.**
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class com.beloucif.lataverne.content.** {
    static **$* *;
}
-keepclassmembers class com.beloucif.lataverne.content.<1>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
