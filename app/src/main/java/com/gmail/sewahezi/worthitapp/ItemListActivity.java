package com.gmail.sewahezi.worthitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TreeMap;

/**
 * Author: Edrees W.
 *
 * Page that displays all items
 * Contains Delete All menu button, or individual delete functionality
 */
public class ItemListActivity extends AppCompatActivity {
    TreeMap<String, ItemObject> itemMap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemMap = new TreeMap<String, ItemObject>();

        Intent intent = getIntent();

        SaveDataObject.loadSaveToMap(this, itemMap, MainActivity.SAVE_FILE_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();


        //load map values into the page table
        loadItemsToTable();
    }

    protected void loadItemsToTable() {
        TableLayout table = (TableLayout) findViewById(R.id.itemListTable);
        TableRow tableRow = null;

        //clear table
        table.removeAllViews();

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1);
        //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        if(itemMap.isEmpty()) {
            tableRow = new TableRow(this);

            tableRow.setLayoutParams(tableParams);

            TextView noItemsText = new TextView(this);
            noItemsText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 1));
            noItemsText.setText("No items available, add some items from the main page to view them here!");

            tableRow.addView(noItemsText);
            table.addView(tableRow);
        } else {
            for (String key : itemMap.keySet()) {
                ItemObject item = itemMap.get(key);
                final String itemName = item.itemName;
                final String itemCost = item.itemCost;
                System.out.println("==== IN LIST PAGE, ITEM: " + item.itemName);

                tableRow = new TableRow(this);

                tableRow.setLayoutParams(tableParams);

                TextView rowTextItemName = new TextView(this);
                HorizontalScrollView rowTextItemNameScrollView = new HorizontalScrollView(this);
                rowTextItemNameScrollView.setLayoutParams(new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
                rowTextItemNameScrollView.setPadding(0,0,32,0);
                rowTextItemName.setTextSize(24f);
                rowTextItemName.setMaxLines(1);
                rowTextItemName.setText(item.itemName);
                rowTextItemNameScrollView.addView(rowTextItemName);

                TextView rowTextItemPrice = new TextView(this);
                HorizontalScrollView rowTextItemPriceScrollView = new HorizontalScrollView(this);
                rowTextItemPriceScrollView.setLayoutParams(new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
                rowTextItemPriceScrollView.setPadding(0,0,32,0);
                rowTextItemPrice.setTextSize(24f);
                rowTextItemPrice.setMaxLines(1);
                rowTextItemPrice.setText("$" + item.itemCost);
                rowTextItemPriceScrollView.addView(rowTextItemPrice);

                Button rowDeleteButton = new Button(this);
                rowDeleteButton.setLayoutParams(new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 0.2f));
                //rowDeleteButton.setTextSize(18f);
                rowDeleteButton.setText("DEL");
                rowDeleteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("===IN BUTTON CLICK: " + itemName + " ===");
                        deleteItemAlert(itemName);
                    }
                });

                tableRow.addView(rowTextItemNameScrollView);
                tableRow.addView(rowTextItemPriceScrollView);
                tableRow.addView(rowDeleteButton);
                table.addView(tableRow);

            }
        }
    }

    private void deleteItemAlert(final String itemToDelete) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete Item");
        alertDialog.setMessage("Are you sure you want to delete item " + itemToDelete + "?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(itemToDelete);
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        alertDialog.show();
    }

    protected void deleteItem(String itemNameKey) {
        itemMap.remove(itemNameKey);

        SaveDataObject.saveDataToFile(this, itemMap, MainActivity.SAVE_FILE_NAME);

        loadItemsToTable();

        Toast.makeText(getApplicationContext(), "Item: " + itemNameKey + " Deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuDeleteAllItems:
                deleteAllItemAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllItemAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete All Items");
        alertDialog.setMessage("Are you sure you want to delete ALL items from the list? This cannot be undone!");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllItems();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void deleteAllItems() {
        //clear save data, reload page with empty list
        itemMap.clear();
        SaveDataObject.clearSaveFile(this, MainActivity.SAVE_FILE_NAME);
        loadItemsToTable();
    }
}
