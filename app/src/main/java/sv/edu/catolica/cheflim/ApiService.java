package sv.edu.catolica.cheflim;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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

    //API RECETAS
    @GET("receta")
    Call<List<Recetas>> getRecetas();

    //API IMAGENES


}