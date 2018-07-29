package com.example.clair.welp;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity{
    ArrayAdapter<String> adapter;
    ListView lvSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        lvSearch = (ListView) findViewById(R.id.lvSearchList);
        List<String> arraySearchResults = new ArrayList<>();
        arraySearchResults.addAll(Arrays.asList(getResources().getStringArray(R.array.array_search)));
        Log.d("TAG", "SEARCH COUNT" + arraySearchResults.size());
        adapter = new ArrayAdapter<>(this, R.layout.activity_search_item, R.id.tvSearchName, arraySearchResults);
        Log.d("TAG", "ADAPTER COUNT" + adapter.getCount());
        lvSearch.setAdapter(adapter);
        lvSearch.setVisibility(View.INVISIBLE);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")){
                    lvSearch.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(newText);
                }else{
                    lvSearch.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });


        //SET Colors of Search Text
        int searchViewDarkGrey = Color.parseColor("#303030");
        int searchViewLightGrey = Color.parseColor("#737373");
        TextView textSearch = (TextView)searchView.findViewById(R.id.search_src_text);
        textSearch.setTextColor(searchViewDarkGrey);

        //SET Colors and Action of Close Button and Search
        // Get the search close button image view
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setColorFilter(searchViewLightGrey);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Find EditText view
                EditText et = (EditText) findViewById(R.id.search_src_text);
                //Clear the text from EditText view
                et.setText("");
                //Clear query
                searchView.setQuery("", false);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                //TODO: search page
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                if (imm.isActive()){
                    // Hide keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                super.onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
