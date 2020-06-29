package ca.cmpt276.restaurantapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.restaurantapplication.adapters.RecentInspectionsFavouriteRestaurantsAdapter;

/**
 * This activity displays the list of all favourite restaurants with newest inspections of each one.
 */
public class RecentInspectionsFavouriteRestaurants extends AppCompatActivity {

    RestaurantManager manager_restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_restaurants);

        manager_restaurant = RestaurantManager.getInstance();

        populateRecyclerView();

    }

    // Instantiates RestaurantAdapter
    private void populateRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.activity_favourite_restaurants_recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecentInspectionsFavouriteRestaurantsAdapter(this, manager_restaurant.getFavouriteRestaurants()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * Creating app buttons for action bar in the issue activity
     * Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourite_restaurants_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Creating app buttons for action bar - Code adapted from 'John's Android Studio Tutorials':
     * https://www.youtube.com/watch?v=5MSKuVO2hV4
     *
     * Image icon8_calculator_24 obtained from https://icons8.com/icon/3352/calculator
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Acquires the item selected in Action Bar.
        int id = item.getItemId();

        if(id == R.id.actionbar_back_favouriteActivity) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, RecentInspectionsFavouriteRestaurants.class);
    }








}




