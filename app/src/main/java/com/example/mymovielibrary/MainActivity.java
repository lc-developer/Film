package com.example.mymovielibrary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBManager dbManager;

    BaseAdapter baseAdapter;

    ArrayList<ContentValues> movies = new ArrayList<>();

    final int ADD_EDIT_CODE = 100;

    void initializeAdapter() {

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {

                return movies.size();
            }

            @Override
            public ContentValues getItem(int i) {

                return movies.get(i);
            }

            @Override
            public long getItemId(int i) {

                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if (view == null) {

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                    view = layoutInflater.inflate(R.layout.movie_item_layout, viewGroup, false);
                }

                ContentValues movie = getItem(i);

                TextView movieTitleTV = view.findViewById(R.id.movieTitleTextView);
                TextView descTV = view.findViewById(R.id.descTextView);

                movieTitleTV.setText(movie.getAsString("nome"));
                descTV.setText(movie.getAsString("genere") + " - " + movie.getAsString("anno_prod"));

                return view;
            }
        };
    }

    void loadMovies() {

        movies.clear();

        movies.addAll(dbManager.getMovies());

        baseAdapter.notifyDataSetChanged();
    }

    void openMovieDetailActivity(ContentValues movie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

        intent.putExtra("movie", movie);

        startActivityForResult(intent, ADD_EDIT_CODE);
    }

    void openDelationConfirmationDialog(final ContentValues movie) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Attenzione");
        alert.setMessage("Sei sicuro di volere cancellare?");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbManager.deleteMovie(movie);
                Toast.makeText(MainActivity.this, "Elemento eliminato!", Toast.LENGTH_SHORT).show();
                loadMovies();

            }
        });

        alert.setNegativeButton("NO", null);
        alert.show();
        //alert.setCancelable()
    }



    void openDeleteAllConfirmationDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Attenzione");
        alert.setMessage("Sei sicuro di volere eliminare tutti gli elemnti?");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbManager.deleteAllMovies();
                Toast.makeText(MainActivity.this, "Elementi eliminati!", Toast.LENGTH_SHORT).show();
                loadMovies();

            }
        });

        alert.setNegativeButton("NO", null);
        alert.show();
        //alert.setCancelable()
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_CODE) {
            if (resultCode == RESULT_OK) {
                loadMovies();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new DBManager(this, DBManager.DATABASE_NAME, null, DBManager.DATABASE_VERSION);

        initializeAdapter();
        loadMovies();

        ListView moviesLV = findViewById(R.id.moviesListView);

        moviesLV.setAdapter(baseAdapter);
        moviesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContentValues movie = movies.get(i);
                openMovieDetailActivity(movie);
            }
        });

        moviesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                ContentValues movie = movies.get(i);

                openDelationConfirmationDialog(movie);

                return true;
            }
        });

        ImageView addBtn = findViewById(R.id.addButton);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMovieDetailActivity(null);
            }
        });

        Button deleteBtn = (Button) findViewById(R.id.deleteButton);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteAllConfirmationDialog();
            }
        });
    }
}
