package com.example.lap.stschat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TakenoteActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;
    private Toolbar mToolbar;

    private DatabaseReference fNotesDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takenote);

        mNotesList = (RecyclerView) findViewById(R.id.notes_list);



        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar1);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Take Note");

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);
        //gridLayoutManager.setReverseLayout(true);
        //gridLayoutManager.setStackFromEnd(true);
        mNotesList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        }



        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void loadData() {
        Query query = fNotesDatabase.orderByValue();
        FirebaseRecyclerOptions<NoteModel> options =
                new  FirebaseRecyclerOptions.Builder<NoteModel>()
                        .setQuery(query, NoteModel.class)
                        .build();


        FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModel,NoteViewHolder>(options)
        {

            protected void onBindViewHolder(final NoteViewHolder viewHolder, int position, NoteModel model) {
                final String noteId = getRef(position).getKey();

                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                            viewHolder.setNoteTitle(title);
                            //viewHolder.setNoteTime(timestamp);

                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            viewHolder.setNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getApplicationContext()));

                            viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(TakenoteActivity.this, NewNoteActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }



            public TakenoteActivity.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_note_layout, viewGroup,false);
                TakenoteActivity.NoteViewHolder viewHolder = new TakenoteActivity.NoteViewHolder(view);
                return viewHolder;
            }
        };
        mNotesList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);



        if (item.getItemId() == R.id.main_new_note_btn)
        {
            Intent newIntent = new Intent(TakenoteActivity.this, NewNoteActivity.class);
            startActivity(newIntent);
        }
        if (item.getItemId() == R.id.home)
        {
          onBackPressed();
          return true;
        }

        return super.onOptionsItemSelected(item);
    }





    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView textTitle, textTime;
        CardView noteCard;

        public NoteViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            textTitle = mView.findViewById(R.id.note_title);
            textTime = mView.findViewById(R.id.note_time);
            noteCard = mView.findViewById(R.id.note_card);

        }

        public void setNoteTitle(String title) {
            textTitle.setText(title);
        }

        public void setNoteTime(String time) {
            textTime.setText(time);
        }

    }


}