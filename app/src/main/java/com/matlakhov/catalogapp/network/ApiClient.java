package com.matlakhov.catalogapp.network;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Утилитный класс для настройки и получения клиента Retrofit для работы с API.
 * <p>
 * Этот класс предоставляет Singleton-объект Retrofit, настроенный с базовым URL и
 * конвертером Gson для десериализации JSON-ответов. Используется для создания
 * экземпляров сервисов API.
 * </p>
 * <p>
 * <b>Примечание:</b> Текущий базовый URL ({@code https://drive.google.com/}) может быть
 * не предназначен для API. Убедитесь, что он соответствует вашему серверу.
 * </p>
 */
public class ApiClient {

    /** Тег для целей логирования. */
    private static final String TAG = "ApiClient";

    /** Базовый URL для подключения к API. */
    private static final String BASE_URL = "https://drive.google.com/";

    /** Единый экземпляр Retrofit для повторного использования. */
    private static Retrofit retrofit = null;

    /**
     * Возвращает настроенный экземпляр Retrofit.
     * <p>
     * Если экземпляр ещё не создан, инициализирует его с базовым URL и конвертером Gson.
     * Повторные вызовы возвращают сохранённый экземпляр для повышения производительности.
     * </p>
     *
     * @return настроенный объект {@link Retrofit}
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d(TAG, "Retrofit инициализирован с URL: " + BASE_URL);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Ошибка инициализации Retrofit: " + e.getMessage());
                throw new RuntimeException("Не удалось инициализировать Retrofit", e);
            }
        }
        return retrofit;
    }

    /**
     * Сбрасывает текущий экземпляр Retrofit, позволяя создать новый.
     * <p>
     * Полезно для изменения конфигурации (например, базового URL) во время выполнения.
     * </p>
     */
    public static void resetClient() {
        retrofit = null;
        Log.d(TAG, "Retrofit сброшен");
    }
}
