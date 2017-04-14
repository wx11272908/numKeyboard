package com.frank.android.keyboard.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.frank.android.keyboard.R;
import com.frank.android.keyboard.view.KeyBoardView;

public class MainActivity extends AppCompatActivity {

    private EditText etKeyboard;
    private KeyBoardView kvKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etKeyboard = (EditText) findViewById(R.id.et_keyboard);
        kvKeyBoard = (KeyBoardView) findViewById(R.id.kv_keyboard);
        kvKeyBoard.setView(etKeyboard,KeyBoardView.MODE_NUM);//这里规定输入类型（小数，整数）
    }
}
