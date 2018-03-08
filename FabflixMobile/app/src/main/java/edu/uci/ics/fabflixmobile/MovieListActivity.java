package edu.uci.ics.fabflixmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MovieListActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final static int ITEMS_PER_PAGE=10;

    private String[] movies;
    private int total_movies;
    private int lastPage;
    private int currentPage = 0;

    Button nextBtn, prevBtn;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        if (savedInstanceState != null) {
            movies = savedInstanceState.getString("MOVIES").split("#");
            total_movies = savedInstanceState.getInt("TOTAL_MOVIES");
            lastPage = savedInstanceState.getInt("LAST_PAGE");
            currentPage = savedInstanceState.getInt("CURRENT_PAGE");
        }
        preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().clear().commit();//has to clear, otherwise previous search result will overlap with the new search result

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        Bundle b=this.getIntent().getExtras();
        movies=b.getStringArray("jsonArray");
        for (int i = 0; i < movies.length; i++) {
            Log.d("response:", movies[i]);
        }
        Log.d("response:", String.valueOf(movies.length));
        total_movies = movies.length;
        lastPage = total_movies / ITEMS_PER_PAGE;

        mAdapter = new MovieListAdapter(generatePage(currentPage));
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        mRecyclerView.setAdapter(mAdapter);

        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevBtn = (Button) findViewById(R.id.prevBtn);

        prevBtn.setEnabled(false);
        if (lastPage < 1 || total_movies == ITEMS_PER_PAGE) {
            nextBtn.setEnabled(false);
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage += 1;
                mRecyclerView.setAdapter(new MovieListAdapter(generatePage(currentPage)));
                toggleButtons();
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage -= 1;
                mRecyclerView.setAdapter(new MovieListAdapter(generatePage(currentPage)));
                toggleButtons();
            }
        });

        Log.d("MovieListActivity", "onCreate");
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        movies = savedInstanceState.getString("MOVIES").split("#");
        total_movies = savedInstanceState.getInt("TOTAL_MOVIES");
        lastPage = savedInstanceState.getInt("LAST_PAGE");
        currentPage = savedInstanceState.getInt("CURRENT_PAGE");
        Log.d("MovieListActivity", "onRestoreInstanceState");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < movies.length; i++) {
            sb.append(movies[i]).append("#");
        }

        outState.putString("MOVIES", sb.toString());
        outState.putInt("TOTAL_MOVIES", total_movies);
        outState.putInt("LAST_PAGE", lastPage);
        outState.putInt("CURRENT_PAGE", currentPage);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
        Log.d("MovieListActivity", "onSaveInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getString("MOVIES", null) != null) {
            movies = preferences.getString("MOVIES", null).split("#");
            total_movies = preferences.getInt("TOTAL_MOVIES", 0);
            lastPage = preferences.getInt("LAST_PAGE", 0);
            currentPage = preferences.getInt("CURRENT_PAGE", 0);
        }
        Log.d("MovieListActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < movies.length; i++) {
            sb.append(movies[i]).append("#");
        }
        editor.putString("MOVIES", sb.toString()); // value to store
        editor.putInt("TOTAL_MOVIES", total_movies);
        editor.putInt("LAST_PAGE", lastPage);
        editor.putInt("CURRENT_PAGE", currentPage);
        // Commit to storage
        editor.commit();
        Log.d("MovieListActivity", "onPause");
    }


    private void toggleButtons() {
        if (currentPage == lastPage) {
            prevBtn.setEnabled(true);
            nextBtn.setEnabled(false);
        } else if (currentPage == 0) {
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(true);
        } else if (currentPage > 0 && currentPage < lastPage) {
            prevBtn.setEnabled(true);
            nextBtn.setEnabled(true);
        }
    }

    public String[] generatePage(int currentPage)
    {
        String[] pageData;
        int startItem = currentPage * ITEMS_PER_PAGE + 1;
        int items_remain = total_movies % ITEMS_PER_PAGE;

        if (currentPage == lastPage && items_remain > 0)
            pageData = new String[items_remain];
        else
            pageData = new String[ITEMS_PER_PAGE];

        if (currentPage == lastPage && items_remain > 0)
        {
            for (int i=startItem;i < startItem + items_remain; i++)
            {
                pageData[(i - 1)%10] = movies[i - 1];
            }
        }
        else
        {
            for (int i = startItem; i < startItem + ITEMS_PER_PAGE; i++)
            {
                pageData[(i - 1)%10] = movies[i - 1];
            }
        }
        return pageData;
    }

}
