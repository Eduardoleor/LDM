package dev.eduardoleal.ldm.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CreteGroupService {
    String API_ROUTE = "/backend";

    @GET(API_ROUTE)
    Call<CreateGroup> getCreateGroup(@Query("op") String op,
                                    @Query("flag") Integer flag,
                                    @Query("name") String name,
                                    @Query("expiry") Integer expiry,
                                    @Query("expiryext") Integer expiryext,
                                    @Query("active") Integer active,
                                    @Query("token") String token);
}
