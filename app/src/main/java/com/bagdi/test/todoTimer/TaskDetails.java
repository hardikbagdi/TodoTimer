package com.bagdi.test.todoTimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Intent intent= getIntent();
        EditText textView = (EditText)findViewById(R.id.taskDescription);
        textView.setText(intent.getStringExtra(("TASKDESC")));
    }
}
