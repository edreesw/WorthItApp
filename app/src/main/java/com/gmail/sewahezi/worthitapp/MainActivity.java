package com.gmail.sewahezi.worthitapp;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * Author: Edrees W.
 *
 * Main page for the "Worth It App" application.
 * Add items, Display Worth It list, Navigate to item list.
 */

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MAP = "com.gmail.sewahezi.MAP";
    EditText nameText;
    EditText priceText;
    TextView infoText;

    static String SAVE_FILE_NAME = "save.txt";


    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;

    Context context = null;

    TreeMap<String, ItemObject> itemMap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        nameText = (EditText) findViewById(R.id.itemNameText);
        priceText = (EditText) findViewById(R.id.priceText);
        infoText = (TextView) findViewById(R.id.infoText);




        /***

         */
        //check if save file exists and set initial info text
        try {
            File saveFile = new File(context.getFilesDir(), SAVE_FILE_NAME);
            System.out.println("CHECKING IF FILE EXISTS: " + saveFile.exists());
            if (!saveFile.exists()) {
                System.out.println("===No save file found, attempting to create===");

                SaveDataObject.createSaveFile(context, SAVE_FILE_NAME);
            }
            System.out.println("CHECKING IF FILE EXISTS  2: " + saveFile.exists());


            //moving map setup/load to onResume();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        //handle main page behavior
        //moved map/data load here to account for item deletion on the list page
        //Set up map
        itemMap = new TreeMap<String, ItemObject>();
        File saveFile = new File(context.getFilesDir(), SAVE_FILE_NAME);
        if(saveFile.length() == 0) {
            //file is empty
            infoText.setText(R.string.infoMessage_noItems);
            //findViewById(R.id.worthItButton).invalidate();
        } else {
            infoText.setText(R.string.infoMessage_compareText);
            //load items
            SaveDataObject.loadSaveToMap(context, itemMap, SAVE_FILE_NAME);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuViewItems:
                //launch item list activity
                //send activity the itemMap
                Intent intent = new Intent(this, ItemListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearFields(View view) {
        nameText.setText("");
        priceText.setText("");

        //added clear list items
        TableLayout table = (TableLayout) findViewById(R.id.mainItemTable);
        //clear table
        table.removeAllViews();

        //reset info text
        File saveFile = new File(context.getFilesDir(), SAVE_FILE_NAME);
        if(saveFile.length() == 0) {
            //file is empty
            infoText.setText(R.string.infoMessage_noItems);
        } else {
            infoText.setText(R.string.infoMessage_compareText);
        }
    }

    /*
        test function

        called when pressing the "add" button
     */

    public void addItemToSavedItems(View view) {
       String nameStr = nameText.getText().toString();
       String priceStr = priceText.getText().toString();
       System.out.println("ADDING NAME: " + nameStr);
       System.out.println("ADDING COST: " + priceStr);
       if(nameStr.length() > 0 &&
            priceStr.length() > 0) {
           BigDecimal price = new BigDecimal(priceStr);
           price = price.setScale(2, BigDecimal.ROUND_FLOOR);
           priceStr = price.toString();


           //check if name already exists in map
           if (itemMap.get(nameStr) != null) {
               //alert saying this already exists
               //TODO: add question, asking if you want to overwrite (not sure how to use user response yet, alerts dont return anything?) so for now just tell the user to delete the duplicate themselves
               addDuplicateAlert(nameStr);
               return;

           }

           //Add values to Map
           itemMap.put(nameStr, new ItemObject(nameStr, priceStr));

           //Save map values to save file
           SaveDataObject.saveDataToFile(context, itemMap, SAVE_FILE_NAME);

           Toast.makeText(getApplicationContext(), "Item Added!", Toast.LENGTH_SHORT).show();

           //update info text if item is added (and therefore no longer empty)
           //if (infoText.getText().toString().equals(R.string.infoMessage_noItems)) {
               infoText.setText(R.string.infoMessage_compareText);
           //}

       } else {
           fieldsNotFilledAlert();
       }
        /*
        //test functionality, makes new activity
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.itemNameText);
        String itemNameValue = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, itemNameValue);
        startActivity(intent);
        */
    }

    private void addDuplicateAlert(String duplicateName) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Cannot Add Duplicate Item!");
        alertDialog.setMessage("Item already exists! Please delete original if you want to add it again with a different value.");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });


        alertDialog.show();
    }

    private void fieldsNotFilledAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Missing Fields!");
        alertDialog.setMessage("Make sure the Item Name and Item Cost fields are filled out!");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });


        alertDialog.show();
    }

    private void addItemsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("No Saved Items!");
        alertDialog.setMessage("Add some items first so you have something to compare with!");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });


        alertDialog.show();
    }



    public void worthItCalculation(View view) {

        String worthItName = nameText.getText().toString();
        String worthItCost = priceText.getText().toString();
        if(worthItName.length() > 0 &&
                worthItCost.length() > 0) {
            //make sure there are item saved in the item list
            if(itemMap.isEmpty()) {
                addItemsAlert();
                return;
            }
            BigDecimal worthItCostDec = new BigDecimal(worthItCost);
            worthItCostDec = worthItCostDec.setScale(2, BigDecimal.ROUND_FLOOR);
            worthItCost = worthItCostDec.toString();

            //fill out info field
            infoText.setText(worthItName + " is worth approximately: ");

            //calculate and post rows
            TableLayout table = (TableLayout) findViewById(R.id.mainItemTable);
            TableRow tableRow = null;
            //clear table
            table.removeAllViews();

            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1);


            for(String key : itemMap.keySet()) {
                ItemObject item = itemMap.get(key);
                final String itemName = item.itemName;
                final String itemCost = item.itemCost;

                BigDecimal itemCostDec = new BigDecimal(itemCost);
                itemCostDec = itemCostDec.setScale(2, BigDecimal.ROUND_FLOOR);

                BigDecimal dividedNum = worthItCostDec.divide(itemCostDec, 1, BigDecimal.ROUND_FLOOR);


                tableRow = new TableRow(this);

                tableRow.setLayoutParams(tableParams);

                TextView rowTextItemName = new TextView(this);
                HorizontalScrollView rowTextItemNameScrollView = new HorizontalScrollView(this);
                rowTextItemNameScrollView.setLayoutParams(new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
                //rowTextItemNameScrollView.setPadding(0,0,32,0);
                rowTextItemName.setTextSize(24f);
                rowTextItemName.setMaxLines(1);
                rowTextItemName.setText(item.itemName);
                rowTextItemNameScrollView.addView(rowTextItemName);

                TextView rowTextItemAmount = new TextView(this);
                HorizontalScrollView rowTextItemAmountScrollView = new HorizontalScrollView(this);
                rowTextItemAmountScrollView.setLayoutParams(new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 0.2f));
                rowTextItemAmountScrollView.setPadding(0,0,32,0);
                rowTextItemAmount.setTextSize(24f);
                rowTextItemName.setMaxLines(1);
                rowTextItemAmount.setText(dividedNum.toString());
                rowTextItemAmountScrollView.addView(rowTextItemAmount);


                tableRow.addView(rowTextItemAmountScrollView);
                tableRow.addView(rowTextItemNameScrollView);
                table.addView(tableRow);
            }

        } else {

            fieldsNotFilledAlert();
        }




    }

}
