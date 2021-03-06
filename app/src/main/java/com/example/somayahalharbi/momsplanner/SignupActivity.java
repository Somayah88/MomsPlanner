package com.example.somayahalharbi.momsplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import butterknife.BindView;
import butterknife.ButterKnife;


public class SignupActivity extends AppCompatActivity {


    @BindView(R.id.signup_progress)
    ProgressBar signu_progress;
    @BindView(R.id.signup_email)
    EditText mEmail;
    @BindView(R.id.signup_password)
    EditText mPassword;
    @BindView(R.id.email_sign_up_button)
    Button mSignUpButton;
    @BindView(R.id.login_link)
    Button login_link;

    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                return id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL;
            }
        });

        login_link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_email_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_password_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.short_password_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                signu_progress.setVisibility(View.VISIBLE);
                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        signu_progress.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, getResources().getString(R.string.sign_up_error) + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        signu_progress.setVisibility(View.GONE);
    }

}