package org.oopproject.deserializers;

import com.google.gson.annotations.SerializedName;

public class VideoDeserializer {
    @SerializedName("key")
    public String key;  // Ключ видео, который будет использоваться для ссылки

    @SerializedName("name")
    public String name;  // Название видео (например, "Трейлер")

    @SerializedName("site")
    public String site;  // Платформа видео (например, "YouTube")

    // Конструкторы и методы, если нужно
}
