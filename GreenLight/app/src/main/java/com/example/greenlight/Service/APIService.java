package com.example.greenlight.Service;

import com.example.greenlight.Notifications.MyResponse;
import com.example.greenlight.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key =AAAA2dzm2YI:APA91bEw2xo43OimakWyVkrWarTrbuXLbuGOLnqpD_5vJMm2X6QUfJ8hJx6eIhOWvtDl4OMB5FnyzWFGZJ1ybxg_UIF3aZNtklDYVBTfLOKNFr_bU-vXnHGtvOgrgkZ8b8Hx_p62d-3v"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
