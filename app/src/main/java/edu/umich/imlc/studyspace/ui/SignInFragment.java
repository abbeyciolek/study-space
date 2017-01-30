package edu.umich.imlc.studyspace.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import edu.umich.imlc.studyspace.R;

/**
 * Created by abbey on 19/02/15.
 */
public class SignInFragment extends DialogFragment {


    /**
     * Created by abbey on 19/02/15.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_sign_in, null);
        builder.setView(view);
        builder.setTitle("Sign In");

        final ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        final EditText password = (EditText) view.findViewById(R.id.user_password);
        final EditText username = (EditText) view.findViewById(R.id.user_email);

        password.setTypeface(Typeface.DEFAULT);

        password.setTransformationMethod(new PasswordTransformationMethod());

        builder.setPositiveButton("Sign In", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewSwitcher.setDisplayedChild(1);
                        String password_str = password.getText().toString();
                        String username_str = username.getText().toString();

                        ParseUser.logInInBackground(username_str,
                                                    password_str,
                                                    new LogInCallback() {
                                                        @Override
                                                        public void done(ParseUser parseUser,
                                                                         ParseException e) {
                                                            if (parseUser != null) {
                                                                //success
                                                                Toast.makeText(getActivity(),
                                                                               "You are now signed in as " +
                                                                               ParseUser.getCurrentUser()
                                                                                        .getEmail(),
                                                                               Toast.LENGTH_LONG)
                                                                     .show();
                                                                getTargetFragment().getActivity()
                                                                                   .invalidateOptionsMenu();
                                                                dialog.dismiss();
                                                            }
                                                            else {
                                                                viewSwitcher.setDisplayedChild(0);
                                                                username.setError(e.getMessage());
                                                            }
                                                        }
                                                    });
                    }
                });
            }
        });

        return dialog;
    }
}


