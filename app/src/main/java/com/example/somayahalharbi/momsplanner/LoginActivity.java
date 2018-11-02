package com.example.somayahalharbi.momsplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */

// Login tutorial: https://www.androidlearning.com/android-login-registration-firebase-authentication/
public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_email)
    EditText mLoginEmail;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
     @BindView(R.id.password_reset_link)
     Button mPasswordReset;
     @BindView (R.id.sign_up_link)
     Button mSignUpLink;
     @BindView(R.id.email_login_button)
     Button mLoginButton;

     @BindView(R.id.login_progress)
    ProgressBar mLoginProgress;

     private FirebaseAuth mFirebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFirebaseAuth=FirebaseAuth.getInstance();
        if(mFirebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        mSignUpLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        mPasswordReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();

            }
        });
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mLoginEmail.getText().toString().trim();
                final String password=mLoginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_email_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_password_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                mLoginProgress.setVisibility(View.VISIBLE);
                mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            if(password.length()<8){
                                mLoginPassword.setError(getString(R.string.error_invalid_password));
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            startActivity(new Intent (LoginActivity.this, MainActivity.class));
                            finish();
                        }

                    }
                });
            }
        });

    }

    private void resetPassword(){
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_reset, null);
        dialogBuilder.setView(dialogView);
       // ButterKnife.bind(this, dialogView);
        final EditText editEmail = dialogView.findViewById(R.id.email);
        final Button resetButton = dialogView.findViewById(R.id.reset_password_button);
        final ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        final Button backButton = dialogView.findViewById(R.id.btn_back);



        final AlertDialog dialog = dialogBuilder.create();
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_email_error), Toast.LENGTH_SHORT).show();
                    return;

                }
                progressBar.setVisibility(View.VISIBLE);
                mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.password_reset_message)
                                    , Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.reset_fail_message),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                });
            }
        });
        dialog.show();
    }






}

