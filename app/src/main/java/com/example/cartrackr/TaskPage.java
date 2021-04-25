package com.example.cartrackr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cartrackr.model.Tasks;
import com.example.cartrackr.model.Vehicle;
import com.example.cartrackr.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * TaskPage class to add tasks related to a single vehicle
 * @since April 2021
 * @author Group 4
 */
public class TaskPage extends AppCompatActivity {
    /**
     * FirebaseFirestore instance
     */
    private FirebaseFirestore mFirestore;
    /**
     * Query instance
     */
    Query query;
    /**
     * Input for service name
     */
    private EditText textServiceName;
    /**
     * Input for service date
     */
    private EditText textServiceDate;
    /**
     * Save button
     */
    private Button saveTaskButton;
    /**
     * FirestoreRecyclerAdapter instance
     */
    private FirestoreRecyclerAdapter<Tasks, TasksHolder> adapter;
    /**
     * Vehicle instance
     */
    Vehicle vehicle;
    /**
     * RecyclerView instance
     */
    RecyclerView tasksList;
    /**
     * LinearLayoutManager instance
     */
    LinearLayoutManager linearLayoutManager;
    /**
     * String value for service name
     */
    String serviceName;
    /**
     * String value for service date
     */
    String serviceDate;

    /**
     * Override method for onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);
        ButterKnife.bind(this);

        // Initialize variables
        init();

        // Setup Action toolbar
        setupToolbar();

        // Retrieve tasks list from Firestore
        getTasksList();

        // Save button click listener
        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceName = textServiceName.getText().toString();
                serviceDate = textServiceDate.getText().toString();

                saveDateToFirestore();
                finish();
                startActivity(getIntent());
            }
        });
    }

    /**
     * Method to init variables
     */
    private void init() {
        tasksList = findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(this);
        tasksList.setLayoutManager(linearLayoutManager);

        // Initialize Firestore and the main RecyclerView
        mFirestore = FirebaseUtil.getFirestore();

        // View components
        textServiceName = findViewById(R.id.service_name);
        textServiceDate = findViewById(R.id.task_due_date);
        saveTaskButton = findViewById(R.id.save_task_button);

        // Vehicle data
        vehicle = (Vehicle) getIntent().getExtras().getSerializable("DATA");
    }

    /**
     * Method to set up toolbar
     */
    private void setupToolbar() {
        // Setup toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.carlogonotext);
        int color = Color.parseColor("#259504");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    /**
     * Method to get tasks list from Firestore
     */
    private void getTasksList() {
        String userId = FirebaseUtil.getAuth().getCurrentUser().getUid();
        query = mFirestore.collection("users").document(userId).collection("vehicles").document(vehicle.getId()).collection("tasks");
        Log.i("QUERY", query.toString());
        FirestoreRecyclerOptions<Tasks> tasks = new FirestoreRecyclerOptions.Builder<Tasks>()
                .setQuery(query, Tasks.class).build();

        adapter = new FirestoreRecyclerAdapter<Tasks, TasksHolder>(tasks) {
            @Override
            public void onBindViewHolder(TasksHolder tasksHolder, int i, Tasks model) {
                tasksHolder.taskName.setText(model.toString());
            }

            @NonNull
            @Override
            public TasksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
                return new TasksHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        tasksList.setAdapter(adapter);
    }

    /**
     * TasksHolder for RecyclerView
     */
    public class TasksHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        public TasksHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.service_task_name);
        }
    }

    /**
     * Method to start listening for adapter
     */
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Method to stop listening for adapter
     */
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /**
     * Method to save data to Firestore
     */
    private void saveDateToFirestore() {
        Map<String, Object> task = new HashMap<>();
        task.put("serviceName", serviceName);
        task.put("serviceDate", serviceDate);

        String id = vehicle.getId();
        String userId = FirebaseUtil.getAuth().getCurrentUser().getUid();

        mFirestore.collection("users").document(userId)
                .collection("vehicles")
                .document(id)
                .collection("tasks")
                .document()
                .set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DOC", "DocumentSnapshot successfully written!");
                        getTasksList();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DOC", "Error writing document", e);
                    }
                });

        textServiceDate.setText("");
        textServiceName.setText("");
    }

    /**
     * Method needed for custom Toolbar
     * @param menu Menu instance
     * @return bool
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Method needed for custom Toolbar
     * @param item MenuItem instance
     * @return bool
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to run when clicking sign out
     */
    private void signOut() {
        AddCarPage.mFirebaseAuth.signOut();
        AddCarPage.mSignInClient.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}