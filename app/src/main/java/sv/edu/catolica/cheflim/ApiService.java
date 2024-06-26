package sv.edu.catolica.cheflim;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


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

    @GET("receta")
    Call<List<Recetas>> getRecetas(@Query("nombre") String nombre);

    @GET("recetadetalle/{userId}/{recetaId}")
    Call<Recetas> getRecetas(@Path("userId") int userId, @Path("recetaId")int recetaId);

    @GET("obtenerfavid/{id}/{iduser}")
    Call<FavoritoResponse> checkFavorito(@Path("id") int idReceta, @Path("iduser") int idUsuario);

    @POST("addfav")
    Call<Map<String, Object>> createFav(@Body Map<String, Object> request);

    @POST("resena")
    Call<Map<String, Object>> createresena(@Body Map<String, Object> request);

    @GET("recetauser/{id}")
    Call<List<Recetas>> getUserRecetas(@Path("id") int id);

    @GET("recetafav/{id}")
    Call<List<Recetas>> getFavRecetas(@Path("id") int id);

    @GET("obtenerlista/{id}")
    Call<List<Lista>> checkList(@Path("id") int idReceta);

    @POST("anadirlista")
    Call<Map<String, Object>> anadirlista(@Body Map<String, Object> request);

    //API IMAGENES
    @DELETE("deletereceta/{id}")
    Call<Map<String,Object>> eliminarreceta(@Path("id") int id);
    @DELETE("borrarArchivo/{idReceta}")
    Call<Map<String, Object>> eliminarfile(@Path("idReceta") int id);

    @DELETE("eliminaritem/{id}")
    Call<Map<String,Object>> eliminarlista(@Path("id") int id);

    @POST("createreceta")
    Call<Map<String,Object>> crearReceta(@Body Recetas receta);
    @PUT("updatereceta/{id}")
    Call<Map<String,Object>> updateReceta(@Path("id") int id,@Body Recetas receta);

    @Multipart
    @POST("subir/{id}")
    Call<ResponseBody> subirImagen(
            @Path("id") int id,
            @Part MultipartBody.Part imagen
    );
    @Multipart
    @POST("resubir/{id}")
    Call<ResponseBody> resubirImagen(
            @Path("id") int id,
            @Part MultipartBody.Part imagen
    );

}
