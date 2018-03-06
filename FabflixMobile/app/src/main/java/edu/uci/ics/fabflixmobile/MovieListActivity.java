package edu.uci.ics.fabflixmobile;

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
    private String[] movies;

    private int total_movies;
    private final static int ITEMS_PER_PAGE=10;
    private int lastPage;
    private int currentPage = 0;

    Button nextBtn, prevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
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
