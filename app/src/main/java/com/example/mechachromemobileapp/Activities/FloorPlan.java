package com.example.mechachromemobileapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mechachromemobileapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * FloorPlan activity
 *
 * Displays floorImages of a floor plan
 */
public class FloorPlan extends AppCompatActivity {

    // Global variables
    private Button changeFloorButton;
    private ImageView floorImage;
    private int currentImage;
    private int [] floorImages = {R.drawable.floor1, R.drawable.floor2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);
        initViews();
        setButtons();
    }

    /**
     *  Method for initialization widgets, fields and Firebase instances
     */
    public void initViews() {
        // Layout elements
        floorImage = findViewById(R.id.floor_image);
        changeFloorButton = findViewById(R.id.change_floor_button);

        // Arrays listing all the rooms on each floor
        String [] roomsFloorOne = {"Student services" , "Canteen" ,"Student Finance", "101" , "102","103", "104", "105"};
        String [] roomsFloorTwo = {"Student Well-being" , "201" ,"202" ,"203" ,"204" ,"205" ,"206" ,"207"};

        // changing arrays to list type objects
        List<String> floorOneRooms = Arrays.asList(roomsFloorOne);
        List<String> floorTwoRooms = Arrays.asList(roomsFloorTwo);

        // getting data from previous activity(timetable)
        Intent intent = getIntent();
        String roomNumber = intent.getStringExtra("roomNumber");

        // condition to display appropriate floor
        if(floorOneRooms.contains(roomNumber)){
            floorImage.setImageResource(floorImages[0]);
        } else if (floorTwoRooms.contains(roomNumber)) {
            floorImage.setImageResource(floorImages[1]);
        }
    }

    /**
     *  This method sets the onClickListener to buttons
     */
    public void setButtons() {
        changeFloorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // rising currentImage by 1
                        currentImage++;
                        currentImage=currentImage% floorImages.length;
                        // setting current floorImage to element from the list
                        floorImage.setImageResource(floorImages[currentImage]);
                    }
                }
        );
    }

}