package com.dungeoncrawl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import battle.Battle;
import battle.GameSetup;
import battle.R;
import battle.SetupPlayer;
import character.Rogue;
import equipment.Inventory;
import equipment.Item;

public class DungeonCrawl extends AppCompatActivity {

    //TODO: make shops/chests less frequent
    //TODO: set a random index for the exit to appear at in the minimap before the start, instead of randomly picking when it shows up
    //TODO: generate new rooms when going west to unvisited indexes
    //TODO: store disarmed traps in the minimap
    //TODO: fix exit logic
    private int currMapRowIndex = 0, currMapColIndex = 0, chanceOfEnd = 0;
    private Character currDir;
    private HashMap<Character, String> currImage = new HashMap<Character, String>();
    private HashMap<Character, String> roomLayout = new HashMap<Character, String>();
    final Class drawableClass = R.drawable.class;
    final Field[] fields = drawableClass.getFields();
    private List<List<HashMap<Character, String>>> minimap = new ArrayList<List<HashMap<Character, String>>>();
    private GameSetup g = new GameSetup();
    private boolean disarmed = false;
    private int steps = 0;
    private boolean mIsBound = false;
    private MusicService mServ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);
        g.player = getIntent().getExtras().getParcelable("Player");
        System.out.println(g.player.getClass());
        System.out.println("NEW PLAYER HP: " + g.player.getFighterHP());
        for(Item i : g.player.characterInventory) {
            System.out.println("Item class: " + i.getClass().getName());
            System.out.println("NEW PLAYER INV: " + i.getType());
            System.out.println("NEW PLAYER INV: " + i.getGroup());
        }
        setupGame();
    }

    private ServiceConnection Scon =new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this,MusicService.class), Scon,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if(mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    private void setupGame() {
        setContentView(R.layout.activity_dungeon);
        //generate first row, add starting room to index 0,0, set player to face east
        currMapRowIndex = 0;
        currMapColIndex = 0;
        chanceOfEnd = 0;
        currImage= new HashMap<Character, String>();
        minimap.add(0, new ArrayList<HashMap<Character, String>>());
        currImage.put('e', "dx");
        currImage.put('s', "dx");
        currImage.put('w', "nx");
        currImage.put('n', "nx");
        minimap.get(0).add(0, currImage);
        currDir = 'e';
        populateRoomLayout();
        initializeImageSwitcher();
        setActionListeners();
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
                ImageView imageView = new ImageView(DungeonCrawl.this);
                return imageView;
            }
        });
    }

    private void setImageRotateListener() {
        final ImageButton upButton = (ImageButton) findViewById(R.id.btnUp);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String fileName = roomLayout.get(currDir);
                if(fileName.charAt(fileName.length()-1) == 'd' && !disarmed){
                    System.out.println("Ouch! Should have disarmed that trap...");
                    g.player.setFighterHP((g.player.getFighterHP())-1);
                }
                if(fileName.equals("dungeon_exit")){
                    System.out.println("Congratulations, You've made it!");
                    win();
                } else {
                    changeIndex();
                    setSwitcherAnimation("Up");
                    setCurrentRoom();
                    updateMinimap();
                }
                if(steps != 0) {
                    steps--;
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
                if(steps != 0) {
                    steps--;
                }
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
                if(steps != 0) {
                    steps--;
                }
            }
        });
    }

    private void setActionListeners(){
        final Intent intent = new Intent(this, Inventory.class);
        final Button inventory = (Button) findViewById(R.id.inventory);
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                intent.putExtra("Player", g.player);
                startActivityForResult(intent, 1);
            }
        });
        final Button fight = (Button) findViewById(R.id.fight);
        final Intent battleIntent = new Intent(this, Battle.class);
        fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                battleIntent.putExtra("Player", g.player);
                startActivity(battleIntent);
            }
        });
        final Button loot = (Button) findViewById(R.id.loot);
        loot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //generate random loot
                //add to player inventory
            }
        });
        final Button disarm = (Button) findViewById(R.id.disarm);
        final TextView textView = (TextView) findViewById(R.id.context);
        disarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               if(g.player.getClass().equals(Rogue.class)){
                   textView.setText("disarmed");
                   displayImage("dungeon_door_disarmed");
                   disarmed = true;
                   disarm.setEnabled(false);
               }
            }
        });
        final Button shop = (Button) findViewById(R.id.shop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //generate list of loot with prices
                //call new activity to display shop prices
                    //buy and sell loot
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        System.out.println("ONACTIVITYRESULT responsecode : " + requestCode);
        if(requestCode == 1) {
            System.out.println("Updating player after activity returned result");
            g.player = data.getExtras().getParcelable("player");
        }
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
            case "None":imageSwitcher.setInAnimation(null);
                imageSwitcher.setOutAnimation(null);
                break;
        }
    }

    private void setCurrentRoom() {
        Random r = new Random();
        List<Character> choices = new ArrayList<Character>();
        currImage = new HashMap<Character, String>();

        //determine which directions are valid
        if (currMapRowIndex > 0) {
            choices.add('n');
        }
        if (currMapColIndex > 0) {
            choices.add('w');
        }
        choices.add('e');
        choices.add('s');
        String choice, choice2;
        System.out.println(currMapRowIndex + ": " + currMapColIndex);
        System.out.println("Minimap size: " + minimap.size());
        if(minimap.size()-1 >= currMapRowIndex && minimap.get(currMapRowIndex).size()-1 >= currMapColIndex && minimap.get(currMapRowIndex).get(currMapColIndex) != null && !minimap.get(currMapRowIndex).get(currMapColIndex).equals("")){
            //if row and col already exist, take existing value as currImage
            currImage = minimap.get(currMapRowIndex).get(currMapColIndex);
            System.out.println("Using existing room: " + currImage + " as new currImage at index (" + currMapRowIndex + ", " + currMapColIndex + ")");
        } else {
            //pick 2 directions atleast
            choice = choices.get(r.nextInt(choices.size())).toString();
            choice2 = choices.get(r.nextInt(choices.size())).toString();
            while (choice.equals(choice2)) {
                choice2 = choices.get(r.nextInt(choices.size())).toString();
            }
            System.out.println("Choice 1: " + choice + ", Choice 2: " + choice2);
            choice = choice.concat(choice2);

            //make sure the direction you just came in through has a door
            Character oppDir = oppositeDir();
            if (!choice.contains(oppDir.toString())) {
                System.out.println("Adding direction you came from: " + oppDir);
                choice = choice.concat(oppDir.toString());
            }

            //create new list if minimap doesn't have that many rows
            System.out.println(currMapRowIndex + ", " + currMapColIndex);
            if(minimap.size()-1 < currMapRowIndex){
                System.out.println("Adding new lists to minimap up to row index: " + currMapRowIndex);
                //create new lists up to the new rowindex
                for (int i = 0; i <= currMapRowIndex; i++) {
                    if(null == minimap.get(i)) {
                        minimap.add(new ArrayList<HashMap<Character, String>>());
                    }
                }
                //populate list with empty strings up to currMapColIndex
                System.out.println("Populating new row up to currMapColIndex: " + currMapColIndex);
                for (int i = 0; i < currMapColIndex; i++) {
                    minimap.get(currMapRowIndex).add(i, new HashMap<Character, String>());
                }

                for(Character direction : choices){
                    String actions = "x";
                    if(steps == 0){
                        actions = actions.concat("f");
                    }

                    if(choice.contains(direction.toString())){
                        actions = actions.concat("t");
                        currImage.put(direction, "d" + actions.toCharArray()[r.nextInt(actions.length())]);
                    } else {
                        actions = actions.concat("ls");
                        currImage.put(direction, "n" + actions.toCharArray()[r.nextInt(actions.length())]);
                    }

                    //if monster encounter reset steps
                    if(currImage.get(direction).contains("f")){
                        steps = 10;
                    }
                }
                System.out.println("New Image selected: " + currImage);
                minimap.get(currMapRowIndex).add(currMapColIndex, currImage);
            } else {
                //if row already exists, make sure there are indexes up to currMapColIndex
                List<HashMap<Character,String>> row = minimap.get(currMapRowIndex);
                System.out.println("Row size: " + row.size());
                if (row.size() - 1 < currMapColIndex) {
                    System.out.println("Populating empty cells between end of row: " + (row.size() - 1) + " and currMapColIndex: " + currMapColIndex);
                    for (int i = 0; i < currMapColIndex; i++) {
                        //if i is out of bounds add empty string at index
                        if (i > row.size() - 1) {
                            row.add(i, new HashMap<Character, String>());
                            //else check to make sure row,col index is empty if it is, add empty string
                        } else if (null == row.get(i)) {
                            row.add(i, new HashMap<Character, String>());
                        }
                    }
                    for(Character direction : choices){
                        String actions = "x";
                        if(steps == 0){
                            actions = actions.concat("f");
                        }
                        if(choice.contains(direction.toString())){
                            actions = actions.concat("t");
                            currImage.put(direction, "d" + actions.toCharArray()[r.nextInt(actions.length())]);
                        } else {
                            actions = actions.concat("ls");
                            currImage.put(direction, "n" + actions.toCharArray()[r.nextInt(actions.length())]);
                        }
                    }
                    System.out.println("New Image selected: " + currImage);
                    minimap.get(currMapRowIndex).add(currMapColIndex, currImage);
                }
            }
        }

        checkIfEnd();
        populateRoomLayout();
        toggleNavigation();
        updateCompass();
        displayImage(roomLayout.get(currDir));
    }

    private void checkIfEnd(){
        chanceOfEnd++;
        Random r = new Random();
        System.out.println("Chance of End: " + chanceOfEnd);
        if(chanceOfEnd > r.nextInt(100)){
            currImage = new HashMap<Character, String>();
            currImage.put('z', "dx");
            currImage.put(oppositeDir(), "dx");
        }
    }

    private void updateMinimap(){
        TextView textView = (TextView) findViewById(R.id.miniMap);
        textView.setText(minimap.toString());
        for(List<HashMap<Character, String>> row : minimap){

        }
//        GridView gridView = (GridView) findViewById(R.id.miniMap);
//        List<String> rowListArray = new ArrayList<String>();
//        int maxColCount = 0;
//        for(int i=0; i < minimap.size(); i++) {
//           System.out.println(minimap.get(i));
//           for(int j=0; j < minimap.get(i).size(); j++) {
//               String row = minimap.get(i).get(j);
//               if(!row.equals("")) {
//                   rowListArray.add("1");
//               } else {
//                   rowListArray.add("0");
//               }
//               if(j > maxColCount){
//                   maxColCount = j;}
//           }
//        }
//        System.out.println("max Columns: " + maxColCount);
//        //TODO: fix multiple columns sizing down columns to invisible levels
//        gridView.setNumColumns(maxColCount+1);
//        String[] rowStringArray = rowListArray.toArray(new String[rowListArray.size()]);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, rowStringArray);
//        gridView.setAdapter(adapter);
    }

    private void turnLeft() {
        switch (currDir) {
            case 'n':
                currDir = 'w';
                break;
            case 'e':
                currDir = 'n';
                break;
            case 'w':
                currDir = 's';
                break;
            case 's':
                currDir = 'e';
                break;
        }
    }

    private void updateCompass(){
        final ImageView compass = (ImageView) findViewById(R.id.compass);
        switch(currDir){
            case 'n': compass.setImageResource(R.drawable.compass_north);
                      break;
            case 'e': compass.setImageResource(R.drawable.compass_east);
                      break;
            case 'w': compass.setImageResource(R.drawable.compass_west);
                      break;
            case 's': compass.setImageResource(R.drawable.compass_south);
                      break;
        }

    }

    private void displayImage(String imageName) {
        System.out.println(imageName);
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
        setActions(imageName);
    }

    private void setActions(String imageName){
        final Button disarm = (Button) findViewById(R.id.disarm);
        final Button fight = (Button) findViewById(R.id.fight);
        final Button loot = (Button) findViewById(R.id.loot);
        final Button shop = (Button) findViewById(R.id.shop);
        disarm.setEnabled(false);
        fight.setEnabled(false);
        loot.setEnabled(false);
        shop.setEnabled(false);
        switch(imageName.charAt(imageName.length()-1)){
            case 't':
                disarm.setEnabled(true);
                disarmed = false;
                setSwitcherAnimation("None");
                break;
            case 'f':
                fight.setEnabled(true);
                break;
            case 'l':
                loot.setEnabled(true);
                break;
            case 's':
                shop.setEnabled(true);
                break;
        }
    }

    private void populateRoomLayout() {
        System.out.println("Populating Room Layout for currImage: " + currImage);
        for (Character direction : currImage.keySet()) {
            String fileName = "";
            String room = currImage.get(direction);
            System.out.println("Room: " + room);
            if(room.contains("d")){
                fileName = "dungeon_door";
            }
            if(room.contains("n") || room.isEmpty()){
                fileName = "dungeon_no_door";
            }
            //get actions(s)
            fileName = fileName.concat(room.substring(1));
            if(direction.equals('z')){
                fileName = "dungeon_exit";
            }
            System.out.println("Filename: " + fileName);
            roomLayout.put(direction, fileName);
        }
    }

    private Character oppositeDir() {
        switch (currDir) {
            case 'n':
                return 's';
            case 'e':
                return 'w';
            case 'w':
                return 'e';
            case 's':
                return 'n';
            default:
                return null;
        }
    }

    private void changeIndex() {
        switch (currDir) {
            case 'n':
                currMapRowIndex--;
                break;
            case 'e':
                currMapColIndex++;
                break;
            case 'w':
                currMapColIndex--;
                break;
            case 's':
                currMapRowIndex++;
                if(minimap.size()-1 < currMapRowIndex){
                    minimap.add(new ArrayList<HashMap<Character, String>>());
                }
        }
    }

    private void toggleNavigation() {
        final ImageButton upButton = (ImageButton) findViewById(R.id.btnUp);
        if(roomLayout.containsKey(currDir)) {
            if (roomLayout.get(currDir).contains("dungeon_no_door")) {
                upButton.setEnabled(false);
                upButton.setImageResource(R.drawable.up_arrow_disabled);
            } else if ((roomLayout.get(currDir).contains("dungeon_door") && !roomLayout.get(currDir).contains("f")) || roomLayout.get(currDir).contains("dungeon_exit")) {
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
        for(List<HashMap<Character, String>> row : minimap){
            for(HashMap<Character, String> s : row){
                if(s != null && !s.isEmpty()) {
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
        minimap = new ArrayList<List<HashMap<Character, String>>>();
        mServ.stopMusic();
        doUnbindService();
        finish();
        startActivity(intent);
    }
}
