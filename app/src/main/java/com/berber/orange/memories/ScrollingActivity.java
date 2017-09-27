package com.berber.orange.memories;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.berber.orange.memories.adapter.TimeLineAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


//        Glide.with(this).load("https://cdn.pixabay.com/photo/2016/10/28/11/57/tic-tac-toe-1777859_960_720.jpg")
//                .into(imageView);


        ImageView imageView = findViewById(R.id.image_content);
        Glide.with(this).load("https://cdn.pixabay.com/photo/2016/10/28/11/57/tic-tac-toe-1777859_960_720.jpg").into(imageView);
        initRecycler();
    }

    private void initRecycler() {

        recycler = (RecyclerView) findViewById(R.id.time_line_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        TimeLineAdapter adapter = new TimeLineAdapter(getData(), this.getApplicationContext());

        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);


    }

    private List<String> getData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add("This is berberber from index: " + i);
        }
        return datas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
