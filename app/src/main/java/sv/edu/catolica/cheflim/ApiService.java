package sv.edu.catolica.cheflim;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ApiService {

    //API LOGIN
    @POST("checkifuser")
    Call<Map<String, Object>> checkIfUser(@Body Map<String, Object> request);

    @POST("createuser")
    Call<Map<String, Object>> createUser(@Body Map<String, Object> request);

    @POST("login")
    Call<Map<String, Object>> login(@Body Map<String, Object> request);

    @PATCH("edituser/{id}")
    Call<Map<String, Object>> editUser(@Path("id") int id, @Body Map<String, Object> request);

    @PATCH("changepass/{id}")
    Call<Map<String, Object>> changePassword(@Path("id") int id, @Body Map<String, Object> request);


}
