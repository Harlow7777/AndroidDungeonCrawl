package equipment;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import battle.Fighter;
import battle.GameSetup;
import battle.R;

/**
 * Created by ab04191 on 8/25/2016.
 */
public class Inventory extends ListActivity {

    List<Item> list = new ArrayList<>();
    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter;
    private GameSetup g = new GameSetup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        g.player = getIntent().getExtras().getParcelable("Player");
        for(Item i : g.player.characterInventory) {
            System.out.println("Item class: " + i.getClass().getName());
            System.out.println("NEW PLAYER TYPE: " + i.getType());
            System.out.println("NEW PLAYER GROUP: " + i.getGroup());
            System.out.println("NEW PLAYER AMOUNT: " + i.getAmount());
            list.add(i);
        }
        TextView hp = (TextView) findViewById(R.id.player);
        hp.setText("HP: " + g.player.getFighterHP());
        setActionListeners();
        updateInventoryView();
    }

    public void setActionListeners(){
        final Button confirm = (Button) findViewById(R.id.ok);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("player", g.player);
                setResult(1,resultIntent);
                finish();
            }
        });
    }

    public void updateInventoryView(){
        System.out.println("Updating Inventory View");
        for(Item i : list) {
            System.out.println(i.getClass().getName() + "," + i.getGroup() + "," + i.getType() + "," + i.getAmount());
            listItems.add(i.getClass().getName() + "," + i.getGroup() + "," + i.getType() + "," + i.getAmount());
        }
        adapter.notifyDataSetChanged();
    }

    //TODO: fix listItems display to update item amount
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final TextView hp = (TextView) findViewById(R.id.player);
        String itemAtPosition = getListView().getItemAtPosition(position).toString();
        System.out.println("item clicked: " + getListView().getItemAtPosition(position));
        final String[] itemAttrs = itemAtPosition.split(",");
        if(itemAttrs[1].equals("Utility")) {
            new AlertDialog.Builder(this)
                    .setTitle("Use")
                    .setMessage(itemAtPosition)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Item clicked: " + which);
                                    for(Item i : list) {
                                        String itemType = i.getType().toString();
                                        if(i.getType().equals(itemAttrs[2])) {
                                            if(useUtility(itemType, g.player)) {
                                                System.out.println("Decrementing item amount");
                                                int index = list.indexOf(i);
                                                i.setAmount(i.getAmount()-1);
                                                list.set(index, i);
                                                g.player.setEquipment(list);
                                                listItems.clear();
                                                updateInventoryView();
                                            }
                                        }
                                    }
                                    hp.setText("HP: " + g.player.getFighterHP());
                                }
                            })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Equip")
                    .setMessage(itemAtPosition)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Item clicked: " + which);
                                }
                            })
                    .show();
        }
    }

    public boolean useUtility(String itemType, Fighter fighter){
        System.out.println("Using utility: " + itemType);
        boolean itemUsed = false;
        switch (itemType) {
            case "potion":
                itemUsed = this.usePotion(fighter);
                break;
        }
        return itemUsed;
    }

    public boolean usePotion(Fighter fighter) {
        //Heal fighter
        TextView context = (TextView) findViewById(R.id.ctxt);
        int healthDifference = fighter.fighterMaxHP - fighter.fighterHP;
        if (healthDifference == 0) {
            context.setText("Fighter already at max health.");
            return false;
        } else if(healthDifference > 0){
            if (healthDifference > 3) {
                healthDifference = 3;
            }
            fighter.fighterHP += healthDifference;
            if (healthDifference == 1) {
                context.setText("Healed for 1 point!");
            } else {
                context.setText("Healed for " + healthDifference + " points!");
            }
            return true;
        }
        return false;
    }
}
