package com.example.admin.expertexam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final static String EXTRA_QUESTION_BANK = "com.example.admin.expertexam.QUESTION_BANK";
    public final static int INTENT_EDIT_PROFILE = 5;
    public final static int INTENT_CHANGE_PASSWORD = 6;
    public final static int INTENT_ADD_EXAM = 7;
    public final static int INTENT_EDIT_EXAM = 8;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference reff;
    private FirebaseUser account;
    private List<QuestionBank> questionBanks;
    private DashboardAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTvUsername;
    private TextView mTvEmail;
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        account = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mTvUsername = (TextView) headerView.findViewById(R.id.tv_username);
        mTvEmail = (TextView) headerView.findViewById(R.id.tv_email);

        mTvUsername.setText(account.getDisplayName());
        mTvEmail.setText(account.getEmail());

        questionBanks = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.question_bank_recycler_view);
        mAdapter = new DashboardAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        reff = mFirebaseDatabase.getReference();
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionBanks = FirebaseQuery.fetchQuestionBanks(dataSnapshot);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR", databaseError.getMessage());
            }
        });
        admin = false;
        if (account.getEmail().equals("fenoh33001@1981pc.com")) {
            admin = true;
            Menu menu = navigationView.getMenu();
            for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
                MenuItem menuItem = menu.getItem(menuItemIndex);
                switch (menuItem.getItemId()) {
                    case R.id.grp_exam:
                        Menu submenu = menuItem.getSubMenu();
                        submenu.getItem(0).setVisible(true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent i = null;
        switch (id) {
            case R.id.nav_add_exam:
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.set_title_dialog, null);

                final EditText etTitle = (EditText) dialogView.findViewById(R.id.et_title);
                Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
                Button btnSubmit = (Button) dialogView.findViewById(R.id.btn_save);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String title = etTitle.getText().toString();
                        if (title.isEmpty()) {
                            etTitle.setError("Question bank title cannot be empty");
                        }
                        else {
                            reff.child("question_bank").orderByKey().equalTo(title).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        etTitle.setError("This title has already exists");
                                    }
                                    else {
                                        alertDialog.dismiss();
                                        QuestionBank questionBank = new QuestionBank(title);
                                        Intent i = new Intent(getApplicationContext(), AddExamActivity.class);
                                        i.putExtra(AddExamActivity.EXTRA_QUESTION_BANK, questionBank);
                                        startActivityForResult(i, INTENT_ADD_EXAM);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ERROR", databaseError.getMessage());
                                }
                            });
                        }
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                break;
            case R.id.nav_view_question:
                i = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(i);
                break;
            case R.id.nav_view_history:
                i = new Intent(getApplicationContext(), ViewResultsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_edit_profile:
                i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivityForResult(i, INTENT_EDIT_PROFILE);
                break;
            case R.id.nav_change_password:
                i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivityForResult(i, INTENT_CHANGE_PASSWORD);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_EDIT_PROFILE:
                if (resultCode == RESULT_OK) {
                    account = FirebaseAuth.getInstance().getCurrentUser();
                    mTvUsername.setText(account.getDisplayName());
                    mTvEmail.setText(account.getEmail());
                    Toast.makeText(this, "You have succesfully updated your profile", Toast.LENGTH_SHORT).show();
                }
                break;
            case INTENT_CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "You have succesfully change your password", Toast.LENGTH_SHORT).show();
                }
                break;
            case INTENT_ADD_EXAM:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "New exam is added", Toast.LENGTH_SHORT).show();
                    reff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            questionBanks = FirebaseQuery.fetchQuestionBanks(dataSnapshot);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ERROR", databaseError.getMessage());
                        }
                    });
                }
                break;
            case INTENT_EDIT_EXAM:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "The exam information is edited", Toast.LENGTH_SHORT).show();
                    reff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            questionBanks = FirebaseQuery.fetchQuestionBanks(dataSnapshot);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ERROR", databaseError.getMessage());
                        }
                    });
                }
                break;
        }
    }

    public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardHolder> {

        private LayoutInflater mInflater;

        public DashboardAdapter() {
            mInflater = LayoutInflater.from(Dashboard.this);
        }

        class DashboardHolder extends RecyclerView.ViewHolder {
            RelativeLayout mRelativeLayout;
            TextView mTvTitle;
            TextView mTvQuestionSize;
            ImageView btnEditButton;

            final DashboardAdapter mAdapter;

            public DashboardHolder(@NonNull View itemView, DashboardAdapter adapter) {
                super(itemView);
                mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
                mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                mTvQuestionSize = (TextView) itemView.findViewById(R.id.tv_question_size);
                btnEditButton = (ImageView) itemView.findViewById(R.id.edit_button);
                if (!admin) {
                    btnEditButton.setVisibility(View.INVISIBLE);
                }
                mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public DashboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View mItemView = mInflater.inflate(R.layout.question_item_layout, parent, false);
            return new DashboardHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(@NonNull DashboardHolder holder, int position) {
            final QuestionBank qb = questionBanks.get(position);
            final String title = qb.getTitle();
            final int size = qb.getQuestionSize();

            holder.mTvTitle.setText(title);
            holder.mTvQuestionSize.setText(size + " questions");
            holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), StartExamActivity.class);
                    i.putExtra(EXTRA_QUESTION_BANK, qb);
                    startActivity(i);
                }
            });
            holder.btnEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddExamActivity.class);
                    i.putExtra(AddExamActivity.EXTRA_QUESTION_BANK, qb);
                    startActivityForResult(i, INTENT_EDIT_EXAM);
                }
            });
        }

        @Override
        public int getItemCount() {
            return questionBanks.size();
        }
    }
}
