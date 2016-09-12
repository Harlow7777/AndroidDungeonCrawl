package battle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dungeoncrawl.*;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import enemy.Enemy;
import equipment.Inventory;
import equipment.Item;
import equipment.Utility;

/**
 * Created by ab04191 on 8/25/2016.
 */
public class Battle extends GameSetup {

    GameSetup g = new GameSetup();
    static Scanner input = new Scanner(System.in);
    static String in = "";
    Enemy Angorus = new Enemy("Angorus");
    TextView textView;
    TextView playerHP;
    TextView enemyHP;
    TextView playerHealthField;
    TextView enemyHealthField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        g.player = getIntent().getExtras().getParcelable("Player");
        getTextViews();
        setActionListeners();
        encounter();
    }

    public void getTextViews() {
        textView = (TextView) findViewById(R.id.context);
        playerHP = (TextView) findViewById(R.id.playerHealthField);
        enemyHP = (TextView) findViewById(R.id.enemyHP);
        playerHealthField = (TextView) findViewById(R.id.playerHealthField);
        enemyHealthField = (TextView) findViewById(R.id.enemyHealthField);
    }

    public void encounter(){
        Battle b = new Battle();
        if(in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y")){
            textView.setText("It's a hostile creature!");
            //TODO: generate creature dynamically
            b.turnSystem();
            b.bodyCount();
            b.levelUp();
        }
    }

    public void setActionListeners(){
        final Intent intent = new Intent(this, Inventory.class);
        final Button attack = (Button) findViewById(R.id.attack);
        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                g.player.PAttacks(Angorus);
            }
        });

        final Button inventory = (Button) findViewById(R.id.inventory);
        final List<String> itemNames = new ArrayList<String>();
        for(Item i : g.player.characterInventory){
            if(i != null) {
                itemNames.add(i.getGroup() + ":" + i.getType());
            }
        }
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                for(int i = 0; i < g.player.characterInventory.size(); i++) {
                    intent.putExtra("items_to_parse"+i, g.player.characterInventory.get(i));
                }
                startActivity(intent);
            }
        });
    }

    public void turnSystem(){
        while (Angorus.getFighterHP() > 0) {
            if (Angorus.getFighterHP() <= 0) {
                break;
            }
            if (player.getFighterHP() <= 0) {
                textView.setText(player.getFighterName() + " has given into the darkness and cannot fight.");
                break;
            } else {
                textView.setText('\n' + "1. " + player.getFighterName() + " [a]ttack");
                textView.setText("2. " + player.getFighterName() + " [i]nventory");
                textView.setText("3. " + player.getFighterName() + " use [item]");
                textView.setText("4. " + player.getFighterName() + " equip [weapon]");
                Method[] actions = player.getClass().getDeclaredMethods();
                int j = 4;
                for (Method method : actions) {
                    textView.setText(j + ". " + player.getFighterName() + " " + method.getName());
                    j++;
                }
                boolean actionFound = false;
                while (actionFound == false) {
                    textView.setText('\n' + player.getFighterName() + "'s health: " + player.getFighterHP());
                    textView.setText(Angorus.getFighterName() + "'s health: " + Angorus.getFighterHP());
                    textView.setText("What will you do?: ");
                    in = input.nextLine().trim();

                    if (in.equals("a")) {
                        player.PAttacks(Angorus);
                        actionFound = true;
                    } else if (in.contains("equip")) {
                        if (player.equip(in.substring(6))) {
                            System.out.println(in.substring(6) + " equipped!");
                            actionFound = true;
                        } else {
                            System.out.println(in.substring(6) + " not found in inventory/not a weapon");
                        }
                    } else {
                        for (Method action : actions) {
                            //Have to call Character methods directly
                            if (action.getName().contains(in)) {
                                try {
                                    action.invoke(player, Angorus);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                actionFound = true;
                            }
                        }
                        if (actionFound == false) {
                            System.out.println(player.getFighterName() + " can't do that!");
                        }
                    }
                }
            }
            if(Angorus.getFighterHP() <= 0){
                textView.setText("The monster is dead!");
                break;
            } else {
                textView.setText("The monster is attacking " + player.getFighterName() + "!");
                Angorus.PAttacks(player);
            }
        }
    }

    public void bodyCount(){
        if(g.player.getFighterHP() <= 0){
            textView.setText("RIP: " + g.player.getFighterName() + " died in combat.");
        }
    }

    //TODO: put level up in playerStats class
    public void levelUp(){
        //TODO: add exp point system?
        //TODO: add money/gil/gold system for eventual shop?
        textView.setText("Congratulations, you have defeated the Monster!");
        g.player.newMaxFighterHealth();
    }
}
