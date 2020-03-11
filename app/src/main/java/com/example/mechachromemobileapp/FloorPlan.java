package com.example.mechachromemobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FloorPlan extends AppCompatActivity {
    private Button button2;
    private static ImageView imageView;
    private int current_image;
    int [] images = {R.drawable.floor1, R.drawable.floor2};
    private void changeText(){
        final String [] floors = {"Floor I", "Floor II"};
        final TextView changingText = (TextView) findViewById(R.id.textView);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);
        buttonclick();
    }
    public void buttonclick()
    {
        imageView = (ImageView) findViewById(R.id.imageView);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        current_image++;
                        current_image=current_image% images.length;
                        imageView.setImageResource(images[current_image]);

                    }
                }

        );
    }

}