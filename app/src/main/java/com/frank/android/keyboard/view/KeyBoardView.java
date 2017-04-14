package com.frank.android.keyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.frank.android.keyboard.R;

/**
 * Created by lizhenbin on 17/4/11 16:04.
 * Explain:
 */

public class KeyBoardView extends LinearLayout {

    /**
     * 输入模式，
     * 1：表示float类型，比如1.2， 有位数限制 INTEGERNUM
     * 2：表示整数类型，无位数限制
     */
    static final public int MODE_FLOAT = 1;
    static final public int MODE_NUM = 2;

    /**
     * 可以输入的小数位数
     */
    static final public int POINTONE = 1;
    static final public int POINTTWO = 2;

    /**
     * 可以输入的整数位数
     */
    static final public int INTEGERNUM = 5;

    /**
     * 当前编辑框的输入模式
     */
    private int curMode = MODE_FLOAT;

    /**
     * 数值按钮键
     */
    private Button[] numBtns = new Button[11];
    /**
     * 删除按钮键
     */
    private Button clearBtn;

    /**
     * 当前正在编辑的编辑框
     */
    private EditText curEditText = null;

    /**
     * 光标选中字符串的开始位置
     */
    private int start;

    /**
     * 光标选中字符串的结束位置
     */
    private int end;

    /**
     * 输入的内容
     */
    private String temp;

    /**
     * 输入的字符串长度
     */
    private int size;

    /**
     * 输入点
     */
    private int point;

    /**
     * 不能输入0
     */
    private boolean isInputZore = false;

    /**
     * 添加百分号
     */
    private boolean isPercen = false;

    /**
     * 小数位数
     */
    private int pointlength = POINTTWO;

    /**
     * 能否允许输入小数
     */
    private boolean pointEnable = true;


    public KeyBoardView(Context context) {
        super(context);
    }

    public KeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        getAttrs(context, attrs);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_number_keyboard, this);
        // 获取运算符
        clearBtn = (Button) view.findViewById(R.id.clear);
        clearBtn.setOnTouchListener(new ClearAction());

//         获取数字
        numBtns[0] = (Button) view.findViewById(R.id.num0);
        numBtns[1] = (Button) view.findViewById(R.id.num1);
        numBtns[2] = (Button) view.findViewById(R.id.num2);
        numBtns[3] = (Button) view.findViewById(R.id.num3);
        numBtns[4] = (Button) view.findViewById(R.id.num4);
        numBtns[5] = (Button) view.findViewById(R.id.num5);
        numBtns[6] = (Button) view.findViewById(R.id.num6);
        numBtns[7] = (Button) view.findViewById(R.id.num7);
        numBtns[8] = (Button) view.findViewById(R.id.num8);
        numBtns[9] = (Button) view.findViewById(R.id.num9);
        numBtns[10] = (Button) view.findViewById(R.id.point);

        // 实例化监听器对象
        NumberAction action = new NumberAction();
        for (Button btn : numBtns) {
            btn.setOnClickListener(action);
        }
    }

    public KeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }

    /**
     * 外部调用初始化参数
     *
     * @param editText ：当前输入框
     * @param mode     输入类型:小数，整数
     */
    public void setView(EditText editText, int mode) {
        if (mode == 1) {
            curMode = MODE_FLOAT;
            pointEnable = true;
        } else {
            curMode = MODE_NUM;
            pointEnable = false;
        }

        curEditText = editText;

    }


    /**
     * 类 NumberAction 的实现描述：数字按钮监听器
     *
     */
    private class NumberAction implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (curEditText == null
                    || curEditText.getInputType() == InputType.TYPE_NULL) {
                return;
            }
            Button btn = (Button) view;
            String input = btn.getText().toString();
            if (!pointEnable && ".".equals(input)) {
                return;
            }
            // 首次输入
            if (TextUtils.isEmpty(curEditText.getText().toString())) {
                // 一上就".",就什么也不做
                if (input.equals(".")) {
                    return;
                }
                // 如果是"0.0"的话,就清空
                if (curEditText.getText().toString().equals("0.0")) {
                    curEditText.setText("");
                }
            } else {
                String editTextStr = curEditText.getText().toString();
                // 判断显示区域的值里面是否已经有".",如果有,输入的又是".",就什么都不做
                if (editTextStr.indexOf(".") != -1 && input.equals(".")) {
                    return;
                }
                // 判断显示区域的值里面只有"-",输入的又是".",就什么都不做
                if (editTextStr.equals("-") && input.equals(".")) {
                    return;
                }
                // 判断显示区域的值里面数值是否超过5位
                if (curMode == MODE_FLOAT && editTextStr.indexOf(".") == -1
                        && editTextStr.length() >= INTEGERNUM && !input.equals(".")) {
                    return;
                }
                // 判断显示区域的值如果是"0",输入的不是".",就什么也不做
                // 根据用户进行更改
                if (curMode == MODE_FLOAT && editTextStr.equals("0")
                        && !input.equals(".")) {
                    curEditText.setText("");
                }

            }

            start = curEditText.getSelectionStart();
            end = curEditText.getSelectionEnd();
            temp = curEditText.getText().toString();
            size = temp.length();
            point = start;
            if (start == end && size == start) {
                // 输入一个字符
                temp = temp + input;
            } else if (start == end && size != start) {
                // 插入一个字符
                temp = temp.substring(0, start) + input
                        + temp.substring(start, size);
            } else {
                // 代替选中的字符串
                temp = temp.substring(0, start) + input + temp.substring(end);
            }
            point = start + 1;
            if (temp.contains(".")) {
                int posDot = temp.indexOf(".");
                //保证整数位数
                if (posDot > INTEGERNUM) {
                    temp = temp.substring(0, INTEGERNUM) + temp.substring(posDot);
                    --point;
                }

                //保证小数位数
                if (temp.length() - posDot - 1 > pointlength) {
                    temp = temp.substring(0, temp.length() - 1);
                    --point;
                }
            }

            temp = temp.replace("%", "");
            if ("0".equals(temp) && isInputZore) {
                temp = "1";
            }

            if (isPercen) {
                temp = temp.replace("%", "") + "%";
            }

            curEditText.setText(temp);
            curEditText.setSelection(point);
        }
    }

    /**
     * 类 ClearAction 的实现描述：删除键处理
     *
     */
    private class ClearAction implements OnTouchListener {

        private ClearAction.MiusThread miusThread;
        private boolean isOnLongClick = false;
        private Handler myHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (curEditText != null) {
                    start = curEditText.getSelectionStart();
                    end = curEditText.getSelectionEnd();
                    temp = curEditText.getText().toString();
                    size = temp.length();
                    point = start;
                    if (start > 0) {
                        if (start == end && size == start) {
                            // 光标在末尾，并且没有选中任何内容
                            temp = temp.substring(0, start - 1);
                            point = start - 1;
                        } else if (start == end && size != start) {
                            // 光标在已经输入字符串的某个中间位置，并且光标没有选中任何内容
                            temp = temp.substring(0, start - 1)
                                    + temp.substring(start, size);
                            point = start - 1;
                        } else {
                            // 光标选中了一部分字符串
                            temp = temp.substring(0, start)
                                    + temp.substring(end);
                            point = start;
                        }
                    }
                    curEditText.setText(temp);
                    curEditText.setSelection(point);
                }
            }

            ;
        };

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                miusThread = new ClearAction.MiusThread();
                isOnLongClick = true;
                miusThread.start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (miusThread != null) {
                    isOnLongClick = false;
                    miusThread.interrupt();
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (miusThread != null) {
                    isOnLongClick = true;
                }
            }
            return true;
        }

        /**
         * MiusThread 的实现描述：事件处理逻辑类
         *
         */
        class MiusThread extends Thread {
            @Override
            public void run() {
                while (isOnLongClick) {
                    try {
                        myHandler.sendEmptyMessage(0);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.interrupted();
                    }
                    super.run();
                }
            }
        }
    }


    /**
     * 得到属性值
     *
     * @param context
     * @param attrs
     */
    public void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.keyBoard);
        curMode = ta.getInteger(R.styleable.keyBoard_numType, 2);
        ta.recycle();
    }


    /**
     * 获取小数点后的长度
     *
     * @return
     */
    public int getPointlength() {
        return pointlength;
    }

    /**
     * 设置小数点后的长度
     *
     * @param pointlength
     */
    public void setPointlength(int pointlength) {
        this.pointlength = pointlength;
    }


}
