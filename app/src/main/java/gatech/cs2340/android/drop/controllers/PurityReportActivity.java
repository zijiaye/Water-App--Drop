package gatech.cs2340.android.drop.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.PurityReport;
import gatech.cs2340.android.drop.model.User;

public class PurityReportActivity extends AppCompatActivity {

    private static final String TAG = "SourceReportActivity";

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<PurityReport, PurityReportActivity.ReportViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purity_report);

        //hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_view);
        View view = bottomNavigationView.findViewById(R.id.ic_add);
        view.performClick();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_report:
                        startActivity(new Intent(PurityReportActivity.this, SourceReportActivity.class));
                        break;
                    case R.id.ic_map:
                        startActivity(new Intent(PurityReportActivity.this, MapsActivity.class));
                        break;
                    case R.id.ic_add:
                        startActivity(new Intent(PurityReportActivity.this, PurityReportActivity.class));
                        break;
                    case R.id.ic_graph:
                        startActivity(new Intent(PurityReportActivity.this, HistoricalActivity.class));
                        break;
                    case R.id.ic_setting:
                        startActivity(new Intent(PurityReportActivity.this, SettingActivity.class));
                        break;
                }
                return true;
            }

        });

        fab = (FloatingActionButton) findViewById(R.id.add_purity_report);

        //get profile from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String uid = user.getUid();
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User userInfo = dataSnapshot.child(uid).getValue(User.class);
                if (userInfo._userType.equalsIgnoreCase("User")) {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Add purity report Button Clicked");
                Intent editProfileInIntent = new Intent(PurityReportActivity.this, AddPurityReportActivity.class);
                startActivity(editProfileInIntent);
            }
        });

        //recycler view
        recyclerView = (RecyclerView)findViewById(R.id.report_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);

        //Database Initialization
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PurityReport, PurityReportActivity.ReportViewHolder>(
                PurityReport.class,
                R.layout.purity_report_item,
                PurityReportActivity.ReportViewHolder.class,
                databaseReference.child("purityReports")) {
            @Override
            protected void populateViewHolder(PurityReportActivity.ReportViewHolder viewHolder, PurityReport model, int position) {
                viewHolder.date.setText(model._date);
                viewHolder.reportNum.setText(model._reportNum);
                viewHolder.worker.setText(model._worker);
                viewHolder.latitude.setText(model._latitude);
                viewHolder.longitude.setText(model._longitude);
                viewHolder.overallCondition.setText(model._overallCond);
                viewHolder.virus.setText(model._virusPPM);
                viewHolder.contaminant.setText(model._contaminant);

/*                viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(SourceReportActivity.this, "Clicked", Toast.LENGTH_LONG).show();
                    }
                });*/
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
                int roomCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (roomCount -1) && lastVisiblePosition == (positionStart -1))){
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    /**
     * ReportViewHolder to hold all the report info
     */
    private static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView reportNum;
        private final TextView worker;
        private final TextView latitude;
        private final TextView longitude;
        private final TextView overallCondition;
        private final TextView virus;
        private final TextView contaminant;

        public ReportViewHolder(View v) {
            super(v);
            date = (TextView) itemView.findViewById(R.id.pr_date);
            reportNum = (TextView) itemView.findViewById(R.id.pr_report_number);
            worker = (TextView) itemView.findViewById(R.id.pr_worker);
            latitude = (TextView) itemView.findViewById(R.id.pr_lati);
            longitude = (TextView) itemView.findViewById(R.id.pr_long);
            overallCondition = (TextView) itemView.findViewById(R.id.pr_overall_condition);
            virus = (TextView) itemView.findViewById(R.id.pr_virus);
            contaminant = (TextView) itemView.findViewById(R.id.pr_contaminant);
        }
    }
}
