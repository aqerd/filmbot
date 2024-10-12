package org.oopproject;

import feign.Param;
import feign.RequestLine;
import org.oopproject.responses.AuthResponse;
import org.oopproject.responses.FilmResponse;
import org.oopproject.responses.ListResponse;

/******************************************************* СПРАВКА *******************************************************
* Тесты:
* - Для String никаких доп тестов не нужно;
* - boolean должны быть true или false;
* - int должны быть формата int32 (short int?), минимум -2147483648 и максимум 2147483647;
* - float должны быть минимум -3.402823669209385e+38 и максимум 3.402823669209385e+38;
* - Для некоторых параметров (release_date_lte, release_date_gte, primary_release_date_lte и primary_release_date_gte)
* вместо типа String стоит тип date. Это значит что эти строки должны выглядить так: "YYYY-MM-DD", где
* 1000 <= YYYY <= 9999, 01 <= MM <= 12, 01 <= DD <= (31, 30, 29 или 28), учитывая сколько дней было в выбранном месяце;

* Параметры:
* - К параметрам with_cast, with_companies, with_crew, with_genres, with_keywords, with_people, with_release_type,
* with_watch_monetization_types, with_watch_providers можно применить AND и OR. Для AND нужно перечислить данные
* запятой, а для OR нужно поставить |.
* - sort_by можно применить только следующие данные: original_title.asc, original_title.desc, popularity.asc,
* popularity.desc, revenue.asc, revenue.desc, primary_release_date.asc, title.asc, title.desc,
* primary_release_date.desc, vote_average.asc, vote_average.desc, vote_count.asc, vote_count.desc
* - Если заменить language на "ru" или "ru-RU" то данные фильма будут выводиться на русском. Для американского
* английского код "en" или "en-US"
* - page обязательный параметр. Наименьшее значение: 1

* Термины:
* - desc - сортировка по убыванию
* - asc - сортировка по возрастанию
* - gte - greater than or equal to, больше или равно
* - lte - less than or equal to, меньше или равно
***********************************************************************************************************************/

public interface SiteRequests {
    @RequestLine("GET /authentication?api_key={api_key}")
    AuthResponse checkAuthStatus(@Param("api_key") String token);

    @RequestLine("GET /movie/popular?api_key={api_key}")
    ListResponse getPopularMovies(@Param("api_key") String token);

    @RequestLine("GET /movie/{id}?api_key={api_key}")
    FilmResponse getMovieById(@Param("api_key") String token, @Param("id") String id);

    @RequestLine("GET /discover/movie" +
            "?api_key={api_key}" +
//            "&certification_country={certification_country}" +
            "&include_adult={include_adult}" +
//            "&include_video={include_video}" +
            "&language={language}" +
            "&page={page}" +
//            "&primary_release_year={primary_release_year}" +
//            "&region={region}" +
            "&release_date.gte={release_date_gte}" +
            "&release_date.lte={release_date_lte}" +
            "&sort_by={sort_by}" +
            "&vote_average.gte={vote_average_gte}" +
            "&vote_average.gte={vote_average_lte}" +
//            "&watch_region={watch_region}" +
//            "&with_cast={with_cast}" +
//            "&with_companies={with_companies}" +
//            "&with_crew={with_crew}" +
            "&with_genres={with_genres}" +
//            "&with_origin_country={with_origin_country}" +
//            "&with_original_language={with_original_language}" +
            "&with_runtime.gte={with_runtime_gte}" +
//            "&with_runtime.lte={with_runtime_lte}" +
//            "&without_genres={without_genres}" +
            "&year={year}")
    ListResponse findMovie(@Param("api_key") String token,
//                           @Param("certification_country") String certification_country,
                           @Param("include_adult") boolean include_adult,
//                           @Param("include_video") boolean include_video,
                           @Param("language") String language,
                           @Param("page") int page,
//                           @Param("primary_release_year") int primary_release_year,
//                           @Param("region") String region,
                           @Param("release_date_gte") String release_date_gte,
                           @Param("release_date_lte") String release_date_lte,
                           @Param("sort_by") String sort_by,
                           @Param("vote_average_gte") float vote_average_gte,
                           @Param("vote_average_lte") float vote_average_lte,
//                           @Param("watch_region") String watch_region,
//                           @Param("with_cast") String with_cast,
//                           @Param("with_companies") String with_companies,
//                           @Param("with_crew") String with_crew,
                           @Param("with_genres") String with_genres,
//                           @Param("with_origin_country") String with_origin_country,
//                           @Param("with_original_language") String with_original_language,
                           @Param("with_runtime_gte") float with_runtime_gte,
//                           @Param("with_runtime_lte") float with_runtime_lte,
//                           @Param("without_genres") String without_genres,
                           @Param("year") int year
    );

//    @RequestLine("GET /movie/{id}/similar?api_key={api_key}")
//    ListResponse getSimilarMovies(@Param("api_key") String token, @Param("id") String id);
//
//    @RequestLine("GET /movie/{id}/recommendations?api_key={api_key}")
//    ListResponse getRecommendationsForMovie(@Param("api_key") String token, @Param("id") String id);
//
//    @RequestLine("GET /search/movie?query={query}&include_adult={adult}&language={language}&page={page}&year={year}")
//    ListResponse searchMovie(@Param("api_key") String token,
//                             @Param("query") String query,
//                             @Param("adult") boolean adult,
//                             @Param("language") String language,
//                             @Param("page") int page,
//                             @Param("year") String year);
}
