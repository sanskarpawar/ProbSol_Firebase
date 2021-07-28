package com.example.lap.stschat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.mbms.MbmsErrors;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.example.lap.stschat.R.id.list_view;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment
{

    private View groupFragmentView;
    private ListView list_item;
    private ArrayAdapter<String> arrayAdapter;
    private  ArrayList<String> list_of_groups  =  new ArrayList<>();

    private DatabaseReference Groupref;



    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);


       Groupref = FirebaseDatabase.getInstance().getReference().child("Groups");




        InitializeField();

        RetriveAndDisplayGroup();


        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName" , currentGroupName);



                startActivity(groupChatIntent);
            }
        });

        return  groupFragmentView;
    }

    private void RetriveAndDisplayGroup()
    {

        Groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeField()
    {
        list_item =(ListView) groupFragmentView.findViewById(R.id.list_item);
       arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
       list_item.setAdapter(arrayAdapter);



    }

}
