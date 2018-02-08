package com.example.cameron.ethereumtest1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.cameron.ethereumtest1.R;

import static com.example.cameron.ethereumtest1.activities.EditContentActivity.PARAM_DRAFT_CONTENT_BODY_MARKDOWN;
import static com.example.cameron.ethereumtest1.activities.EditContentActivity.PICK_IMAGE_REQUEST;

public class EditContentBodyActivity extends AppCompatActivity {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content_body);

        String existing = getIntent().getStringExtra(PARAM_DRAFT_CONTENT_BODY_MARKDOWN);

        mEditText = (EditText)findViewById(R.id.editBody);

        if (!TextUtils.isEmpty(existing)) {
            mEditText.setText(existing);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PARAM_DRAFT_CONTENT_BODY_MARKDOWN, mEditText.getText().toString());
        setResult(PICK_IMAGE_REQUEST, intent);

        super.onBackPressed();
    }
}
