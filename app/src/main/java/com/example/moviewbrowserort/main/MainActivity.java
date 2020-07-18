package com.example.moviewbrowserort.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviewbrowserort.adapter.MovieAdapter;
import com.example.moviewbrowserort.model.MovieModel;
import com.example.moviewbrowserort.R;
import com.example.moviewbrowserort.other.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView rv_movieData;
    private ArrayList<MovieModel> arrayList;
    private GridLayoutManager gridLayoutManager;
    private int pageNumber = 1;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private int total_pages;
    private int total_results;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MovieAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValueToLoad();

        gridLayoutManager = new GridLayoutManager(this, 2);
        rv_movieData.setHasFixedSize(true);
        rv_movieData.setLayoutManager(gridLayoutManager);

        getMovieData(pageNumber, "popular");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                pageNumber = 1;
                getMovieData(pageNumber, "popular");
            }
        });

        rv_movieData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleitemcount = gridLayoutManager.getChildCount();
                    totalitemcount = gridLayoutManager.getItemCount();
                    pastvisibleitems = (gridLayoutManager).findFirstVisibleItemPosition();

                    if ((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                        swipeRefreshLayout.setRefreshing(true);
                        pageNumber++;
                        getMovieData(pageNumber, "popular");

                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initValueToLoad() {
        rv_movieData = findViewById(R.id.rv_movieData);
        swipeRefreshLayout = findViewById(R.id.srl_page);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent, null));
    }

    private void getMovieData(int pnumber, String serviceName) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLs.MAINLIST + serviceName + URLs.ATTACHED_API_KEY+URLs.API_KEY + URLs.PAGE + pnumber, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                swipeRefreshLayout.setRefreshing(false);

                Log.e("movie ", "onResponse: " + response);

                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    total_pages = obj.getInt("total_pages");
                    total_results = obj.getInt("total_results");

                    Log.e("movie ", "result&page: " + total_pages + total_results);

                    arrayList = new ArrayList<>();
                    arrayList.clear();

                    JSONArray array = obj.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        MovieModel movieModel = new MovieModel();

                        movieModel.setMovieId(object.getString("id"));
                        movieModel.setMoviePosterPath(object.getString("poster_path"));
                        movieModel.setMovieRating(object.getString("vote_average"));
                        movieModel.setMovieReleaseDate(object.getString("release_date"));
                        movieModel.setMovieSynopsis(object.getString("overview"));
                        movieModel.setMovieTitle(object.getString("original_title"));

                        Log.d("movie", "om: " + movieModel);

                        arrayList.add(movieModel);

                    }

                    MovieAdapter adapter = new MovieAdapter(MainActivity.this, arrayList);
                    adapter.notifyDataSetChanged();
                    rv_movieData.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText( MainActivity.this, "Request timeout retrying", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent( MainActivity.this,MainActivity.class));
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                        Toast.makeText( MainActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (networkResponse.statusCode == 404) {
                        Toast.makeText( MainActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        Toast.makeText( MainActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                    } else if (networkResponse.statusCode == 400) {
                        Toast.makeText( MainActivity.this, "Check your inputs", Toast.LENGTH_SHORT).show();
                    } else if (networkResponse.statusCode == 500) {
                        Toast.makeText( MainActivity.this, "Something is getting wrong", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText( MainActivity.this, "slow internet connection", Toast.LENGTH_SHORT).show();
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);

    }

    private void getMovieBySearch(String query) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLs.SEARCH + URLs.ATTACHED_API_KEY+URLs.API_KEY + URLs.QUERY + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("search ", "onResponse: " + response);

                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));

                    arrayList = new ArrayList<>();
                    arrayList.clear();

                    JSONArray array = obj.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        MovieModel movieModel = new MovieModel();

                        movieModel.setMovieId(object.getString("id"));
                        movieModel.setMoviePosterPath(object.getString("poster_path"));
                        movieModel.setMovieRating(object.getString("vote_average"));
                        movieModel.setMovieReleaseDate(object.getString("release_date"));
                        movieModel.setMovieSynopsis(object.getString("overview"));
                        movieModel.setMovieTitle(object.getString("original_title"));
                        arrayList.add(movieModel);

                    }

                    adapter = new MovieAdapter(MainActivity.this, arrayList);
                    adapter.notifyDataSetChanged();
                    rv_movieData.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText( MainActivity.this, "Request timeout retrying", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent( MainActivity.this,MainActivity.class));
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                        Toast.makeText( MainActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (networkResponse.statusCode == 404) {
                        Toast.makeText( MainActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        Toast.makeText( MainActivity.this, "Please login again", Toast.LENGTH_SHORT).show();
                    } else if (networkResponse.statusCode == 400) {
                        Toast.makeText( MainActivity.this, "Check your inputs", Toast.LENGTH_SHORT).show();
                    } else if (networkResponse.statusCode == 500) {
                        Toast.makeText( MainActivity.this, "Something is getting wrong", Toast.LENGTH_SHORT).show();
                    }
                    
                    Toast.makeText( MainActivity.this, "slow internet connection", Toast.LENGTH_SHORT).show();
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        MenuItem menuSearch, menuFilter;
        menuSearch = menu.findItem(R.id.menu_search);
        menuFilter = menu.findItem(R.id.menu_filter);

        SearchView searchView = (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.menu_popular:
                getMovieData(pageNumber, "popular");
                return true;
            case R.id.menu_topRated:
                getMovieData(pageNumber, "top_rated");
                return true;
            case R.id.menu_upcoming:
                getMovieData(pageNumber, "upcoming");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        List<MovieModel> list = new ArrayList<>();

        getMovieBySearch(userInput);

//        for (MovieModel model: arrayList){
//            if(model.toString().toLowerCase().contains(userInput)){
//                list.add(model);
//            }
//        }
//        if(arrayList.size()<=1){
//            adapter.updateList(list);
//        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(Intent.ACTION_MAIN);
        mainActivity.addCategory(Intent.CATEGORY_HOME);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
        finish();

    }
}
