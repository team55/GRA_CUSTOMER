package ru.team55.gra.rating;

import android.support.v4.app.Fragment;
import android.widget.EditText;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;



@EFragment(R.layout.page_login_part_login)
public class fragmentLogin extends Fragment {

    @ViewById EditText name;
    @ViewById EditText password;

    @Click(R.id.log_in_button)
    void LogIn(){
        ((pageLoginRegister_)getActivity()).attemptLogin(name.getText().toString(), password.getText().toString());
    }

}
