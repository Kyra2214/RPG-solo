# Serializadores gerados pelo kotlinx.serialization usados pelos módulos compartilhados.
-keep class **$$serializer { *; }
-keepclassmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers enum ** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Room descobre entidades, DAOs e bancos por metadados/anotações.
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class androidx.room.** { *; }

# Anotações são necessárias para os processadores/runtime.
-keepattributes *Annotation*
-dontwarn javax.annotation.**
