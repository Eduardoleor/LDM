package dev.eduardoleal.ldm.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CreateUserService {
    String API_ROUTE = "/backend";

    @GET(API_ROUTE)
    Call<CreateUser> getCreateUser(@Query("op") String op,
                                   @Query("addr") String addr,
                                   @Query("appid") String appid,
                                   @Query("name") String name,
                                   @Query("expiry") Integer expiry,
                                   @Query("active") Integer active,
                                   @Query("token") String token);
}
