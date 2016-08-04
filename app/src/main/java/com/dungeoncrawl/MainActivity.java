package com.dungeoncrawl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int currMapRowIndex = 0, currMapColIndex = 0, chanceOfEnd = 0;
    private String currDir = "", currImage = "";
    private HashMap<String, String> roomLayout = new HashMap<String, String>();
    final Class drawableClass = R.drawable.class;
    final Field[] fields = drawableClass.getFields();
    private List<List<String>> minimap = new ArrayList<List<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupGame();
    }

    private void setupGame() {
        setContentView(R.layout.activity_main);
        //generate first row, add starting room to index 0,0, set player to face east
        currMapRowIndex = 0;
        currMapColIndex = 0;
        currDir="";
        chanceOfEnd = 0;
        currImage="";
        minimap.add(0, new ArrayList<String>());
        currImage = "es";
        minimap.get(0).add(0, currImage);
        currDir = "e";
        populateRoomLayout();
        initializeImageSwitcher();
        setImageRotateListener();
        updateCompass();
        updateMinimap();
        displayImage(roomLayout.get(currDir));
    }

    private void initializeImageSwitcher() {
        final ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                return imageView;
            }
        });
    }

    private void setImageRotateListener() {
        final ImageButton upButton = (ImageButton) findViewById(R.id.btnUp);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(roomLayout.get(currDir).equals("dungeon_exit")){
                    System.out.println("Congratulations, You've made it!");
                    win();
                } else {
                    changeIndex();
                    setSwitcherAnimation("Up");
                    setCurrentRoom();
                }
            }
        });
        final ImageButton leftButton = (ImageButton) findViewById(R.id.btnLeft);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                System.out.println("Turning Left from currDir: " + currDir);
                turnLeft();
                System.out.println("New Direction: " + currDir);
                setSwitcherAnimation("Left");
                updateCompass();
                toggleNavigation();
                displayImage(roomLayout.get(currDir));
            }
        });
        final ImageButton rightButton = (ImageButton) findViewById(R.id.btnRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                turnLeft();
                currDir = oppositeDir();
                System.out.println("New Direction: " + currDir);
                setSwitcherAnimation("Right");
                updateCompass();
                toggleNavigation();
                displayImage(roomLayout.get(currDir));
            }
        });
    }

    private void setSwitcherAnimation(String direction){
        final ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        switch(direction){
            case "Up":   imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down_from_top));
                imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down_animation));
                break;
            case "Left": imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
                imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right));
                break;
            case "Right":imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right));
                imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left));
                break;
        }

    }

    private void setCurrentRoom() {
        Random r = new Random();
        List<String> choices = new ArrayList<String>();

        //determine which directions are valid
        if (currMapRowIndex > 0) {
            choices.add("n");
        }
        if (currMapColIndex > 0) {
            choices.add("w");
        }
        choices.add("e");
        choices.add("s");

        //pick 2 directions atleast, make sure the direction you just came in through has a door
        String choice = choices.get(r.nextInt(choices.size())), choice2 = choices.get(r.nextInt(choices.size()));
        while (choice.equals(choice2)) {
            choice2 = choices.get(r.nextInt(choices.size()));
        }
        System.out.println("Choice 1: " + choice + ", Choice 2: " + choice2);
        choice = choice.concat(choice2);
        String oppDir = oppositeDir();
        if (!choice.contains(oppDir)) {
            System.out.println("Adding direction you came from: " + oppDir);
            choice = choice.concat(oppDir);
        }

        //create new list if row index wasnt already created
        if(minimap.size()-1 < currMapRowIndex){
            System.out.println("Adding new list to minimap at row index: " + currMapRowIndex);
            minimap.add(currMapRowIndex, new ArrayList<String>());
            //populate list with empty strings up to currMapColIndex
            System.out.println("Populating new row up to currMapColIndex: " + currMapColIndex);
            for (int i = 0; i < currMapColIndex; i++) {
                minimap.get(currMapRowIndex).add(i, "");
            }
            currImage = choice;
            System.out.println("New Image selected: " + currImage);
        } else {
            //if row already exists, make sure there are indexes up to currMapColIndex
            List<String> row = minimap.get(currMapRowIndex);
            System.out.println("Row size: " + row.size());
            if (row.size()-1 < currMapColIndex) {
                System.out.println("Populating empty cells between end of row: " + (row.size()-1) + " and currMapColIndex: " + currMapColIndex);
                for (int i = 0; i < currMapColIndex; i++) {
                    //if i is out of bounds add empty string at index
                    if (i > row.size() - 1) {
                        row.add(i, "");
                        //else check to make sure row,col index is empty if it is, add empty string
                    } else if (row.get(i) == null) {
                        row.add(i, "");
                    }
                }
                currImage = choice;
                System.out.println("New Image selected: " + currImage);
            } else if(minimap.get(currMapRowIndex).get(currMapColIndex).equals("")) {
                currImage = choice;
            } else {
               //if row and col already exist, take existing value as currImage
               currImage = minimap.get(currMapRowIndex).get(currMapColIndex);
               System.out.println("Using existing room: " + currImage + " as new currImage at index (" + currMapRowIndex + ", " + currMapColIndex + ")");
            }
        }
        if(currImage.equals(choice)) {
            System.out.println("Adding new currImage: " + currImage + " to row: " + currMapRowIndex + ", col: " + currMapColIndex);
            minimap.get(currMapRowIndex).add(currMapColIndex, currImage);
        }
        checkIfEnd();
        populateRoomLayout();
        toggleNavigation();
        updateMinimap();
        updateCompass();
        displayImage(roomLayout.get(currDir));
    }

    private void checkIfEnd(){
        chanceOfEnd++;
        Random r = new Random();
        System.out.println("Chance of End: " + chanceOfEnd);
        if(chanceOfEnd > r.nextInt(100)){
            currImage = "z" + oppositeDir();
        }
    }

    private void updateMinimap(){
        GridView gridView = (GridView) findViewById(R.id.miniMap);
        List<String> rowListArray = new ArrayList<String>();
        int maxColCount = 0;
        for(int i=0; i < minimap.size(); i++) {
           System.out.println(minimap.get(i));
           for(int j=0; j < minimap.get(i).size(); j++) {
               String row = minimap.get(i).get(j);
               if(!row.equals("")) {
                   rowListArray.add("1");
               } else {
                   rowListArray.add("0");
               }
               if(j > maxColCount){
                   maxColCount = j;}
           }
        }
        System.out.println("max Columns: " + maxColCount);
        //TODO: fix multiple columns sizing down columns to invisible levels
        gridView.setNumColumns(maxColCount+1);
        String[] rowStringArray = rowListArray.toArray(new String[rowListArray.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, rowStringArray);
        gridView.setAdapter(adapter);
    }

    private void turnLeft() {
        switch (currDir) {
            case "n":
                currDir = "w";
                break;
            case "e":
                currDir = "n";
                break;
            case "w":
                currDir = "s";
                break;
            case "s":
                currDir = "e";
                break;
        }
    }

    private void updateCompass(){
        final ImageView compass = (ImageView) findViewById(R.id.compass);
        switch(currDir){
            case "n": compass.setImageResource(R.drawable.compass_north);
                      break;
            case "e": compass.setImageResource(R.drawable.compass_east);
                      break;
            case "w": compass.setImageResource(R.drawable.compass_west);
                      break;
            case "s": compass.setImageResource(R.drawable.compass_south);
                      break;
        }

    }

    private void displayImage(String imageName) {
        final ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        //display image using imageName
        for (Field f : fields) {
            if (f.getName().equals(imageName)) {
                System.out.println("field name = " + f.getName() + ", imageName = " + imageName);
                try {
                    imageSwitcher.setImageResource(f.getInt(drawableClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void populateRoomLayout() {
        System.out.println("Populating Room Layout for currImage: " + currImage);
        List<String> directions = new ArrayList<String>();
        directions.add("n");
        directions.add("s");
        directions.add("e");
        directions.add("w");
        for (String direction : directions) {
            if (currImage.contains(direction)) {
                System.out.println("direction: " + direction + "; dungeon_door");
                roomLayout.put(direction, "dungeon_door");
            } else if(currImage.contains("z")){
                System.out.println("found the exit");
                roomLayout.put(currDir, "dungeon_exit");
            } else {
                System.out.println("direction: " + direction + "; dungeon_no_door");
                roomLayout.put(direction, "dungeon_no_door");
            }
        }
    }

    private String oppositeDir() {
        switch (currDir) {
            case "n":
                return "s";
            case "e":
                return "w";
            case "w":
                return "e";
            case "s":
                return "n";
            default:
                return "";
        }
    }

    private void changeIndex() {
        switch (currDir) {
            case "n":
                currMapRowIndex--;
                break;
            case "e":
                currMapColIndex++;
                break;
            case "w":
                currMapColIndex--;
                break;
            case "s":
                currMapRowIndex++;
                if(minimap.size() < currMapRowIndex){
                    minimap.add(currMapRowIndex, new ArrayList<String>());
                }
        }
    }

    private void toggleNavigation() {
        final ImageButton upButton = (ImageButton) findViewById(R.id.btnUp);
        if(roomLayout.containsKey(currDir)) {
            if (roomLayout.get(currDir).equals("dungeon_no_door")) {
                upButton.setEnabled(false);
                upButton.setImageResource(R.drawable.up_arrow_disabled);
            } else if (roomLayout.get(currDir).equals("dungeon_door") || roomLayout.get(currDir).equals("dungeon_exit")) {
                upButton.setEnabled(true);
                upButton.setImageResource(R.drawable.up_arrow);
            } else {
                System.out.println("roomLayout currDir: " + currDir + " returning unexpected filename: " + roomLayout.get(currDir));
                upButton.setEnabled(false);
                upButton.setImageResource(R.drawable.up_arrow_disabled);
            }
        } else {
            System.out.println("ERROR: RoomLayout direction - " + currDir + " not defined");
        }
    }

    private void win(){
        //get number of rooms visited
        int numRooms = 0;
        for(List<String> row : minimap){
            for(String s : row){
                if(s != null && !s.equals("")) {
                    numRooms++;
                }
            }
        }
        //log to high scores
        SharedPreferences yourScore = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = yourScore.edit();
        editor.putInt("key", numRooms);
        editor.commit();

        Intent intent = new Intent(this, Win.class);
        minimap = new ArrayList<List<String>>();
        startActivity(intent);
        finish();
    }
}
