package com.example.a0104.crolling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText ID, PW;
    private String id, pw;
    ArrayList<String> SubList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("LoginActivity", "onCreate 호출됨");

        ID = findViewById(R.id.ID); //EditText 아이디 입력창
        PW = findViewById(R.id.PW); //EditText 비밀번호 입력창
        Button Login = findViewById(R.id.Login); //Button 로그인 버튼
        Button Cancel = findViewById(R.id.Cancel);

        Login.setOnClickListener(new View.OnClickListener() { //클릭 되었을때
            @Override
            public void onClick(View v) {
                Log.d("LoginBtn", "onClick 호출");
                GetData getData = new GetData();
                id = ID.getText().toString();
                pw = PW.getText().toString();
                getData.start();
                try {
                    Log.d("LoginBtn","join");
                    getData.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putStringArrayListExtra("SubList",SubList);
                startActivity(intent);
                finish();
                Log.d("LoginBtn", "onClick 종료");
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "onClick 호출");
                finish();
            }
        });
    }
    public class GetData extends Thread {
        @Override
        public void run() {
            try {
                Log.d("GetData","Thread 호출");
                Connection.Response loginPageResponse =
                        Jsoup.connect("https://sso.daegu.ac.kr/dgusso/ext/lms/login_form.do") //대구대 로그인폼
                                .method(Connection.Method.GET)
                                .userAgent("Mozila")
                                .timeout(0)
                                .execute();

                Map<String, String> cookie1 = loginPageResponse.cookies(); //로그인 전에 쿠키
                Map<String, String> data = new HashMap<>(); //데이터들을 해쉬맵으로 저장한다.
                data.put("Return_Url", "http://lms.daegu.ac.kr/ilos/lo/login_sso.acl"); //로그인 창의 Return_url
                data.put("overLogin", "true");
                data.put("loginName", id); //로그인 데이터
                data.put("password", pw); //비밀번호 데이터

                Connection.Response Response = Jsoup.connect("https://sso.daegu.ac.kr/dgusso/ext/tigersweb/login_process.do") //프로세스 접속
                        .data(data) //해쉬맵 데이터
                        .cookies(cookie1) //로그인 전에 쿠키
                        .timeout(0)
                        .execute();

                Map<String, String> cookie2 = Response.cookies(); //로그인에 성공하면 받는 쿠키 + 로그인 전에 쿠키

                Connection.Response Parse = Jsoup.connect("http://lms.daegu.ac.kr/ilos/lo/login_sso.acl") //로그인 성공시에 들어가는 창
                        .cookies(cookie2) //로그인하고난 뒤에 들고있어야 하는 쿠키
                        .timeout(0)
                        .execute();

                Document document = Parse.parse(); //정보들을 파싱해온다
                Element name = document.select("strong.user").get(0);
                Elements list = document.select("div.m-box2").get(0).select("li");
                Elements sub_sub = list.select("em");
                Elements sub_time = list.select("span");

                for (int i = 0; i < sub_sub.size(); i++) { //ArrayList 에 넣는 작업
                    SubList.add(sub_sub.get(i).text());
                    SubList.add(sub_time.get(i).text());
                }
                Log.d("GetData", name.toString());
                Log.d("GetData", SubList.get(0).toString());
                Log.d("GetData", "Thread 종료");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}