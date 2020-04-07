package com.example.mechachromemobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.R;

import java.util.Arrays;
import java.util.List;

public class FloorPlan extends AppCompatActivity {

    private Button button2;
    private ImageView imageView;
    private String roomNumber;
    private int current_image;
    int [] images = {R.drawable.floor1, R.drawable.floor2};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);

        initViews();
        buttonclick();

    }


    public void initViews() {
        imageView = findViewById(R.id.imageView);
        button2 = findViewById(R.id.button2);

        String [] roomsFloorOne = {"Student services" , "Cantine" ,"Student Finance", "101" , "102","103", "104", "105"};
        String [] roomsFloorTwo = {"Student Wellbeing" , "201" ,"202" ,"203" ,"204" ,"205" ,"206" ,"207"};

        List<String> floorOneRooms = Arrays.asList(roomsFloorOne);
        List<String> floorTwoRooms = Arrays.asList(roomsFloorTwo);

        Intent intent = getIntent();
        roomNumber = intent.getStringExtra("roomNumber");

        if(floorOneRooms.contains(roomNumber)){
            imageView.setImageResource(images[0]);
        } else if (floorTwoRooms.contains(roomNumber)) {
            imageView.setImageResource(images[1]);
        }

    }



    public void buttonclick() {
        button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        current_image++;
                        current_image=current_image% images.length;
                        imageView.setImageResource(images[current_image]);

                    }
                }

        );
    }

    private void changeText(){
        final String [] floors = {"Floor I", "Floor II"};
        final TextView changingText = findViewById(R.id.textView);

    }

}