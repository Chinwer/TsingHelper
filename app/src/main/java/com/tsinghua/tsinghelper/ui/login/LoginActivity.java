package com.tsinghua.tsinghelper.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tsinghua.tsinghelper.MainActivity;
import com.tsinghua.tsinghelper.R;
import com.tsinghua.tsinghelper.dtos.UserDTO;
import com.tsinghua.tsinghelper.util.ErrorHandlingUtil;
import com.tsinghua.tsinghelper.util.HttpUtil;
import com.tsinghua.tsinghelper.util.ToastUtil;
import com.tsinghua.tsinghelper.util.UserInfoUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_username)
    EditText mETUsername;
    @BindView(R.id.et_password)
    EditText mETPassword;
    @BindView(R.id.et_phone)
    EditText mETPhoneNumber;
    @BindView(R.id.cb_remember_login)
    CheckBox mRemember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    private HashMap<String, String> checkFields() {
        // 11位手机号码
        String phonePattern = "^\\d{11}$";
        // 中英文、数字和下划线
        String usernamePattern = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]{2,10}+$";
        // 5到16位字母和数字的组合
        String passwordPattern = "^\\w{5,16}$";
        String username = mETUsername.getText().toString();
        String password = mETPassword.getText().toString();
        String phone = mETPhoneNumber.getText().toString();

        if (!username.matches(usernamePattern)) {
            ToastUtil.showToast(this, "用户名必须为2到10位");
            return null;
        }
        if (!password.matches(passwordPattern)) {
            ToastUtil.showToast(this, "密码必须为5到16位");
            return null;
        }
        if (!phone.matches(phonePattern)) {
            ToastUtil.showToast(this, "请输入11位手机号");
            return null;
        }

        HashMap<String, String> res = new HashMap<>();
        res.put("phone", phone);
        res.put("username", username);
        res.put("password", password);
        return res;
    }

    public void login(View view) {
        HashMap<String, String> params = checkFields();
        if (params == null) {
            return;
        }

        HttpUtil.post(HttpUtil.USER_LOGIN, params, new Callback() {
            @Override
            public void onResponse(
                    @NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                switch (response.code()) {
                    case 201:
                        // login succeeded
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "登录成功");
                        if (mRemember.isChecked()) {
                            // remember login status
                            try {
                                JSONObject json = new JSONObject(resStr);
                                String token = json.getString("token");
                                UserInfoUtil.putPref("auth", token);
                            } catch (JSONException ignored) {
                            }
                            params.remove("password");
                            saveUserInfo(resStr);
                        }
                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
                        break;
                    case 400:
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "请求参数不合法");
                        break;
                    case 403:
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "密码错误");
                        break;
                    case 404:
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "用户名不存在");
                        break;
                    default:
                        Log.i("info", resStr);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ErrorHandlingUtil.handleNetworkError(
                        LoginActivity.this, "网络错误，登录失败", e);
            }
        });
    }

    public void register(View view) {
        HashMap<String, String> params = checkFields();
        if (params == null) {
            return;
        }

        HttpUtil.post(HttpUtil.USER_REGISTER, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ErrorHandlingUtil.handleNetworkError(
                        LoginActivity.this, "网络错误，注册失败", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                switch (response.code()) {
                    case 201:
                        // register succeeded
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "注册成功");
                        // login automatically
                        login(view);
                        break;
                    case 400:
                        // invalid params
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "请求参数不合法");
                        break;
                    case 403:
                        // username or phone number already existed
                        ToastUtil.showToastOnUIThread(LoginActivity.this, "此用户已注册");
                        break;
                    default:
                        Log.i("info", resStr);
                        break;
                }
            }
        });
    }

    private void saveUserInfo(String resStr) {
        JSONObject resJson;
        try {
            resJson = new JSONObject(resStr);
            UserInfoUtil.me = new UserDTO(resJson);
            UserInfoUtil.putPref("loggedIn", "true");
            UserInfoUtil.putPref(UserInfoUtil.ID, String.valueOf(UserInfoUtil.me.id));
            UserInfoUtil.putPref(UserInfoUtil.USERNAME, UserInfoUtil.me.username);
            UserInfoUtil.putPref(UserInfoUtil.PHONE, UserInfoUtil.me.phone);
            UserInfoUtil.putPref(UserInfoUtil.REALNAME, UserInfoUtil.me.realname);
            UserInfoUtil.putPref(UserInfoUtil.DEPARTMENT, UserInfoUtil.me.department);
            UserInfoUtil.putPref(UserInfoUtil.GRADE, UserInfoUtil.me.grade);
            UserInfoUtil.putPref(UserInfoUtil.DORMITORY, UserInfoUtil.me.dormitory);
            UserInfoUtil.putPref(UserInfoUtil.WECHAT, UserInfoUtil.me.wechat);
            UserInfoUtil.putPref(UserInfoUtil.EMAIL, UserInfoUtil.me.email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void forgetPassword(View view) {
        Intent it = new Intent(LoginActivity.this, PswForgetActivity.class);
        startActivity(it);
    }
}
