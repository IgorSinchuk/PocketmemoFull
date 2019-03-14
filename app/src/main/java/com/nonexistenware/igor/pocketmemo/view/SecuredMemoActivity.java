package com.nonexistenware.igor.pocketmemo.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nonexistenware.igor.pocketmemo.R;
import com.nonexistenware.igor.pocketmemo.adapter.MemoAdapter;
import com.nonexistenware.igor.pocketmemo.database.SecuredDatabaseHelper;
import com.nonexistenware.igor.pocketmemo.model.Memo;
import com.nonexistenware.igor.pocketmemo.utils.MyDividerItemDecorator;
import com.nonexistenware.igor.pocketmemo.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class SecuredMemoActivity extends AppCompatActivity {

    private MemoAdapter mAdapter;
    private List<Memo> memoList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noMemo;

    private SecuredDatabaseHelper db;

//    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secured_memo);

        coordinatorLayout = findViewById(R.id.coordinate_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noMemo = findViewById(R.id.empty_memo_view);

        db = new SecuredDatabaseHelper(this);
        memoList.addAll(db.getAllMemo());

        FloatingActionButton newMemo = findViewById(R.id.newMemoBtn);
        newMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemoDialog(false, null, -1);
            }
        });

//        FloatingActionButton backBtn = findViewById(R.id.backBtn);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SecuredMemoActivity.this, NewNoteActivity.class));
//            }
//        });


        mAdapter = new MemoAdapter(this, memoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecorator(this, LinearLayout.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyMemos();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.MemoEventListener() {
                    @Override
                    public void onSingleClick(View view, int position) {

                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        showActionsDialog(position);
                    }
                }));

    }

    private void createNote(String memo) {
        long id = db.insertNote(memo);
        Memo mem = db.getMemo(id);

        if (mem != null) {
            memoList.add(0, mem);
            mAdapter.notifyDataSetChanged();

            toggleEmptyMemos();
        }
    }

    private void updateMemo(String memo, int position) {
        Memo mem = memoList.get(position);
        mem.setMemo(memo);

        db.updateMemo(mem);
        memoList.set(position, mem);
        mAdapter.notifyItemChanged(position);
        toggleEmptyMemos();
    }

    private void deleteMemo(int position) {
        db.deleteMemo(memoList.get(position));

        memoList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyMemos();
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showMemoDialog(true, memoList.get(position), position);
                } else {
                    deleteMemo(position);
                }
            }
        });
        builder.show();
    }

    private void showMemoDialog(final boolean shouldUpdate, final Memo memo, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.memo_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(SecuredMemoActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.memoEdit);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        dialogTitle.setText(!shouldUpdate ? getString(R.string.) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && memo != null) {
            inputNote.setText(memo.getMemo());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(SecuredMemoActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && memo != null) {
                    // update note by it's id
                    updateMemo(inputNote.getText().toString(), position);
                } else {
                    // create new note
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }

    private void toggleEmptyMemos() {
        if (db.getMemosCount() > 0) {
            noMemo.setVisibility(View.GONE);
        } else {
            noMemo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SecuredMemoActivity.this, NewNoteActivity.class));
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }

}
