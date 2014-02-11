package ru.team55.gra.rating;

import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import ru.team55.gra.api.users.UserAccounts;

import java.util.List;


@EFragment(R.layout.page_login_part_register)
public class fragmentRegister extends Fragment {


    @ViewById Spinner  email;
    @ViewById EditText name;
    @ViewById EditText password;


    @AfterViews
    void setupUI(){
        List options = UserAccounts.getAccountsByType(getActivity().getBaseContext(), "com.google");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_spinner_dropdown_item, options);
        email.setAdapter(adapter);
    }


    @Click(R.id.log_in_button)
    void LogIn(){
        ((pageLoginRegister_)getActivity()).attemptRegister(name.getText().toString(), password.getText().toString(), email.getSelectedItem().toString());
    }


}
