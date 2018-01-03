package com.example.thanggun99.test2.one;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import com.example.thanggun99.test2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BlankFragment extends DialogFragment {
    @BindView(R.id.edt)
    AutoCompleteTextView editText;
    private Unbinder unbinder;

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        //xu ly o day
                        return true;
                    }
                }
                return false;
            }
        });

        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            hideKeyboard(getActivity());
            return true;
        });


       /* if (getView() != null) {
            getView().setOnKeyListener((view1, i, keyEvent) -> {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        //xu ly o day
                        return true;
                    }
                }
                return false;
            });
        }*/
        /*editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int actionid, KeyEvent event) {
                String setKeyword = editText.getText().toString().trim();
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (!setKeyword.isEmpty()) {
                        setTextSearch("", false);
                        loadKeyword();
                        editText.clearFocus();

                        return true;
                    }
                }
                return false;
            }
        });
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!editText.getText().toString().trim().isEmpty()) {
                        setTextSearch("", false);
                        loadKeyword();
                        return true;
                    } else {
                        getActivity().onBackPressed();
                        return true;
                    }
                }
                return false;
            }
        });*/
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
