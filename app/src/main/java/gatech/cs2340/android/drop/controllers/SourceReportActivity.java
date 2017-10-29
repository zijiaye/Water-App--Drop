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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gatech.cs2340.android.drop.R;
import gatech.cs2340.android.drop.model.SourceReport;

public class SourceReportActivity extends AppCompatActivity {

    private static final String TAG = "SourceReportActivity";

    //private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<SourceReport, ReportViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_report);

        //hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation_view);
        View view = bottomNavigationView.findViewById(R.id.ic_report);
        view.performClick();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_report:
                        startActivity(new Intent(SourceReportActivity.this, SourceReportActivity.class));
                        break;
                    case R.id.ic_add:
                        startActivity(new Intent(SourceReportActivity.this, PurityReportActivity.class));
                        break;
                    case R.id.ic_map:
                        startActivity(new Intent(SourceReportActivity.this, MapsActivity.class));
                        break;
                    case R.id.ic_graph:
                        startActivity(new Intent(SourceReportActivity.this, HistoricalActivity.class));
                        break;
                    case R.id.ic_setting:
                        startActivity(new Intent(SourceReportActivity.this, SettingActivity.class));
                        break;
                }
                return true;
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_source_report);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Add source report Button Clicked");
                Intent editProfileInIntent = new Intent(SourceReportActivity.this, AddSourceReportActivity.class);
                startActivity(editProfileInIntent);
            }
        });

        //recycler view
        recyclerView = (RecyclerView)findViewById(R.id.report_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);

        //Database Initialization
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SourceReport, ReportViewHolder>(
                SourceReport.class,
                R.layout.source_report_item,
                ReportViewHolder.class,
                databaseReference.child("sourceReports")) {
            @Override
            protected void populateViewHolder(ReportViewHolder viewHolder, SourceReport model, int position) {
                viewHolder.date.setText(model._date);
                viewHolder.reportNum.setText(model._reportNum);
                viewHolder.reporter.setText(model._reporter);
                viewHolder.latitude.setText(model._latitude);
                viewHolder.longitude.setText(model._longitude);
                viewHolder.waterType.setText(model._waterType);
                viewHolder.waterCondition.setText(model._waterCondition);

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
        private final TextView reporter;
        private final TextView latitude;
        private final TextView longitude;
        private final TextView waterType;
        private final TextView waterCondition;

        public ReportViewHolder(View v) {
            super(v);
            date = (TextView) itemView.findViewById(R.id.sp_date);
            reportNum = (TextView) itemView.findViewById(R.id.sp_report_number);
            reporter = (TextView) itemView.findViewById(R.id.sp_reporter);
            latitude = (TextView) itemView.findViewById(R.id.sp_lati);
            longitude = (TextView) itemView.findViewById(R.id.sp_long);
            waterType = (TextView) itemView.findViewById(R.id.sp_water_type);
            waterCondition = (TextView) itemView.findViewById(R.id.sp_water_condition);
        }
    }

}
