package edu.umich.imlc.studyspace.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import edu.umich.imlc.studyspace.R;

/**
 * Created by abbey on 19/02/15.
 */
public class CreateAccountFragment extends DialogFragment {

    private EditText     password;
    private EditText     confirmPassword;
    private EditText     email;
    private EditText     fullName;
    private ViewSwitcher mViewSwitcher;

    private boolean clickConfirm;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_create_acct, null);
        builder.setView(view);
        builder.setTitle("Create Account");

        password = (EditText) view.findViewById(R.id.user_password);
        confirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        email = (EditText) view.findViewById(R.id.user_email);
        fullName = (EditText) view.findViewById(R.id.user_name);
        mViewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);


        password.setTypeface(Typeface.DEFAULT);
        confirmPassword.setTypeface(Typeface.DEFAULT);

        password.setTransformationMethod(new PasswordTransformationMethod());
        confirmPassword.setTransformationMethod(new PasswordTransformationMethod());

        builder.setPositiveButton("Create", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        setEditTextListeners();
        clickConfirm = false;

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewSwitcher.setDisplayedChild(1);
                        clickConfirm = true;
                        createAccount();
                    }
                });
            }
        });

        return dialog;
    }


    private void setEditTextListeners() {
        fullName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                errorChecking();
                return false; // return is important...
            }
        });
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                errorChecking();
                return false; // return is important...
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                errorChecking();
                return false; // return is important...
            }
        });
        confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                errorChecking();
                return false; // return is important...
            }
        });
    }


    private boolean errorChecking() {

        String password_str = password.getText().toString();
        String confirm_password_str = confirmPassword.getText().toString();
        String email_str = email.getText().toString();
        String name_str = fullName.getText().toString();


        boolean checker = true;

        if (clickConfirm) {

            fullName.setError(null);
            email.setError(null);
            password.setError(null);
            confirmPassword.setError(null);

            if (name_str.isEmpty()) {
                fullName.setError("Enter full name.");
                checker = false;
            }
            if (email_str.isEmpty()) {
                email.setError("Enter email.");
                checker = false;
            }
            if (password_str.isEmpty()) {
                password.setError("Enter password.");
                checker = false;
            }
            if (confirm_password_str.isEmpty()) {
                confirmPassword.setError("Enter password.");
                checker = false;
            }
            else if (!(password_str).equals((confirm_password_str))) {
                confirmPassword.setError("Passwords don't match.");
                checker = false;
            }
        }

        return checker;
    }

    private void createAccount() {

        String password_str = password.getText().toString();
        String email_str = email.getText().toString();
        String name_str = fullName.getText().toString();

        fullName.setError(null);
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);


        if (!errorChecking()) {
            mViewSwitcher.setDisplayedChild(0);
            return;
        }


        ParseUser user = new ParseUser();
        user.setUsername(email_str);
        user.setEmail(email_str);
        user.setPassword(password_str);
        user.put("Name", name_str);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Success!
                    getDialog().dismiss();
                    Toast.makeText(getActivity(),
                                   "Account Created!",
                                   Toast.LENGTH_LONG).show();
                }
                else {
                    //Error!
                    mViewSwitcher.setDisplayedChild(0);
                    if (e.getCode() == 202) {
                        email.setError("Email already used.");
                        clickConfirm = false;
                    }
                    else {
                        email.setError(e.getMessage());
                        clickConfirm = false;
                    }
                }
            }
        });
    }
}
